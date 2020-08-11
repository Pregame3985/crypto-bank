package com.tokimi.chain.service.sweep;

import com.tokimi.chain.dao.AddressDAO;
import com.tokimi.chain.dao.ExchangeWalletDAO;
import com.tokimi.chain.dao.LocalBalanceDAO;
import com.tokimi.chain.dao.RegistryDAO;
import com.tokimi.chain.dao.SweepFlowDAO;
import com.tokimi.chain.dao.WithdrawRequestDAO;
import com.tokimi.chain.entity.Address;
import com.tokimi.chain.entity.ExchangeWallet;
import com.tokimi.chain.entity.LocalBalance;
import com.tokimi.chain.entity.Registry;
import com.tokimi.chain.entity.SweepFlow;
import com.tokimi.common.Utils;
import com.tokimi.common.chain.model.AddressBalanceDTO;
import com.tokimi.common.chain.model.AddressGuardDTO;
import com.tokimi.common.chain.model.UnsweepAmoutDTO;
import com.tokimi.common.chain.service.AssetService;
import com.tokimi.common.chain.service.ChainService;
import com.tokimi.common.chain.service.sweep.SweepService;
import com.tokimi.common.chain.service.wallet.WalletService;
import com.tokimi.common.chain.utils.WalletType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.tokimi.common.chain.utils.SweepStatus.FAILED;

/**
 * @author william
 */
@Slf4j
public abstract class SweepServiceAdapter implements SweepService {

    @Resource
    protected SweepFlowDAO sweepFlowDAO;

    @Resource
    protected AddressDAO addressDAO;

    @Resource
    protected LocalBalanceDAO localBalanceDAO;

    @Resource
    protected ChainService chainService;

    @Resource
    private AssetService assetService;

    @Resource
    private ExchangeWalletDAO exchangeWalletDAO;

    @Resource
    private RegistryDAO registryDAO;

    @Resource
    protected WithdrawRequestDAO withdrawRequestDAO;

    protected static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    @Override
    public List<UnsweepAmoutDTO> unsweepAmount() {

        List<Long> tokenIds = assetService.getMyAssetIds();
        List<String> hotWalletAddresses = prepareHotWallet();

        return tokenIds.stream().filter(this::isSupport).map(tokenId -> {
            Address probe = new Address();
            probe.setTokenId(tokenId);
            List<Address> addressGuards = addressDAO.findAll(Example.of(probe));

            BigDecimal amount = addressGuards.stream()
                    .filter(a -> !Utils.isEmpty(hotWalletAddresses) && !hotWalletAddresses.contains(a.getAddress()))
                    .filter(distinctByKey(Address::getAddress)).map(addressGuard -> getWalletService()
                            .balance(addressGuard.getAddress(), addressGuard.getTokenId()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return new UnsweepAmoutDTO(tokenId, amount.toPlainString());
        }).collect(Collectors.toList());
    }

    @Override
    public List<AddressBalanceDTO> listAvailableByTokenId(Long tokenId) {

        if (!isSupport(tokenId)) {
            return null;
        }

        List<LocalBalance> data = localBalanceDAO.findAllByTokenIdAndBalanceGreaterThanOrderByBalanceDesc(tokenId,
                BigDecimal.ZERO);

        return data.stream().filter(distinctByKey(LocalBalance::getAddress)).map(item -> {
            AddressBalanceDTO addressBalance = new AddressBalanceDTO();
            addressBalance.setAddress(item.getAddress());
            addressBalance.setUserId(item.getUserId());
            addressBalance.setBalance(item.getBalance().toPlainString());
            addressBalance.setTokenId(item.getTokenId());
            return addressBalance;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AddressBalanceDTO> listAvailableByUserId(Long userId) {

        Address probe = new Address();
        probe.setTokenId(chainService.getAssetId());
        probe.setUserId(userId);
        List<Address> addressGuards = addressDAO.findAll(Example.of(probe));

        return extractAddressBalances(addressGuards);
    }

    @Override
    public List<AddressBalanceDTO> listAvailableByAddress(String address) {

        Address probe = new Address();
        probe.setTokenId(chainService.getAssetId());
        probe.setAddress(address);
        List<Address> addressGuards = addressDAO.findAll(Example.of(probe));

        return extractAddressBalances(addressGuards);
    }

    private List<AddressBalanceDTO> extractAddressBalances(List<Address> addressGuards) {

        List<AddressBalanceDTO> addressBalances = new ArrayList<>();

        List<Long> tokenIds = assetService.getMyAssetIds();
        tokenIds.stream().filter(this::isSupport)
                .forEach(tokenId -> addressBalances.addAll(toAddressBalances(tokenId, addressGuards)));

        return addressBalances;
    }

    protected List<String> prepareHotWallet() {

        ExchangeWallet probe = new ExchangeWallet();
        probe.setTokenId(chainService.getAssetId());
        probe.setType(WalletType.EGRESS.getValue());

        List<String> appIds = registryDAO.findAll().stream()
                .map(Registry::getAppId).collect(Collectors.toList());

        List<ExchangeWallet> hotWallets = exchangeWalletDAO.findAll(Example.of(probe));

        if (Utils.isEmpty(hotWallets)) {

            log.warn("hot wallet not initialized");
            return null;
        }

        return hotWallets.stream().filter(h -> null != h && !Utils.isEmpty(h.getAddress()))
                .map(ExchangeWallet::getAddress).collect(Collectors.toList());
    }

    protected abstract List<AddressBalanceDTO> toAddressBalances(Long tokenId, List<Address> addressGuards);

    protected abstract BigDecimal threshold(Integer times);

    protected abstract WalletService getWalletService();

    protected abstract void _sweep(List<String> hotWalletAddresses, AddressGuardDTO addressGuardDTO);

    @Override
    public void sweepByUserId(Long userId) {

        List<String> hotWalletAddresses = prepareHotWallet();

        if (Utils.isEmpty(hotWalletAddresses)) {

            log.warn("hot wallet address not exist");
            return;
        }

        Address probe = new Address();
        probe.setTokenId(chainService.getAssetId());
        probe.setUserId(userId);
        List<Address> addressGuards = addressDAO.findAll(Example.of(probe));

        if (Utils.isEmpty(addressGuards)) {

            log.warn("no address need sweep");
            return;
        }

        List<Long> tokenIds = assetService.getMyAssetIds();

        addressGuards.stream().filter(distinctByKey(Address::getAddress)).forEach(
                addressGuard -> tokenIds.stream().filter(this::isSupport).forEach(tokenId -> _sweep(hotWalletAddresses,
                        new AddressGuardDTO(tokenId, addressGuard.getUserId(), addressGuard.getAddress()))));
    }

    @Override
    public void sweepByAddress(String address) {

        Address probe = new Address();
        probe.setAddress(address);
        addressDAO.findOne(Example.of(probe, ExampleMatcher.matchingAll().withIgnoreCase())).ifPresent(addr -> {

                    // get app id
                    String appId = addr.getAppId();

                    // get assets
                    List<Long> assetIds = assetService.getMyAssetIds();

                    // find hot wallet by app id
                    Address hotWalletProbe = new Address();
                    hotWalletProbe.setAppId(appId);
                    hotWalletProbe.setHotWallet(true);
                    Address hotWalletAddr = addressDAO.findOne(Example.of(hotWalletProbe)).orElse(null);

                    if (Objects.isNull(hotWalletAddr)) {
                        log.warn("hot wallet address not exist");
                        return;
                    }

                    if (hotWalletAddr.getAddress().equalsIgnoreCase(addr.getAddress())) {
                        log.warn("no need to sweep hot wallet");
                        return;
                    }

                    // sweep to hot wallet
                    assetIds.forEach(assetId -> _sweep(new ArrayList<>(Collections.singleton(hotWalletAddr.getAddress())),
                            new AddressGuardDTO(assetId, addr.getUserId(), addr.getAddress())));
                }
        );

//        List<String> hotWalletAddresses = prepareHotWallet();
//
//        if (Utils.isEmpty(hotWalletAddresses)) {
//
//            log.warn("hot wallet address not exist");
//            return;
//        }
//
//        if (hotWalletAddresses.contains(address)) {
//
//            log.warn("no need to sweep hot wallet");
//            return;
//        }
//
//        Address probe = new Address();
//        probe.setTokenId(chainService.getAssetId());
//        probe.setAddress(address);
//        List<Address> addressGuards = addressDAO.findAll(Example.of(probe));
//
//
//        addressGuards.stream().filter(distinctByKey(Address::getAddress)).forEach(
//                addressGuard -> tokenIds.stream().filter(this::isSupport).forEach(tokenId -> _sweep(hotWalletAddresses,
//                        new AddressGuardDTO(tokenId, addressGuard.getUserId(), addressGuard.getAddress()))));

    }

    @Override
    @Transactional
    public void sweepAll() {

        List<String> hotWalletAddresses = prepareHotWallet();

        if (Utils.isEmpty(hotWalletAddresses)) {

            log.warn("hot wallet address not exist");
            return;
        }

        List<LocalBalance> data = localBalanceDAO.findAllByTokenIdAndBalanceGreaterThanOrderByBalanceDesc(
                chainService.getAssetId().longValue(), BigDecimal.ZERO);

        data.stream().filter(distinctByKey(LocalBalance::getAddress))
                .filter(item -> !hotWalletAddresses.contains(item.getAddress()))
                .filter(item -> item.getBalance().compareTo(BigDecimal.valueOf(500L)) >= 0)
                .forEach(item -> _sweep(hotWalletAddresses,
                        new AddressGuardDTO(item.getTokenId(), item.getUserId(), item.getAddress())));
    }

    @Override
    public void fail(Long tokenId, Long sweepId) {

        SweepFlow probe = new SweepFlow();
        probe.setTokenId(tokenId);
        probe.setId(sweepId);
        Optional<SweepFlow> any = sweepFlowDAO.findOne(Example.of(probe));

        if (any.isPresent()) {
            SweepFlow sweepFlow = any.get();
            sweepFlow.setStatus(FAILED.getValue());
            sweepFlow.setStatusStr(FAILED.getName());
            sweepFlowDAO.saveAndFlush(sweepFlow);
        }
    }

    @Override
    public boolean isSupport(Long tokenId) {
        return getWalletService().isSupport(tokenId);
    }

    protected int getFeeScale() {
        return getWalletService().scale(chainService.getAssetId());
    }

    protected BigDecimal getFeePrecision() {
        return getWalletService().precision(chainService.getAssetId());
    }
}