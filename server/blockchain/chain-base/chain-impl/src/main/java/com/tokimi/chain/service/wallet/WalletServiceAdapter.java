package com.tokimi.chain.service.wallet;

import com.tokimi.address.manager.core.DefaultSeedRule;
import com.tokimi.address.manager.core.WalletEngine;
import com.tokimi.address.manager.model.HdWalletDTO;
import com.tokimi.chain.dao.AddressDAO;
import com.tokimi.chain.dao.ExchangeWalletDAO;
import com.tokimi.chain.dao.LocalBalanceDAO;
import com.tokimi.chain.entity.Address;
import com.tokimi.chain.entity.ExchangeWallet;
import com.tokimi.chain.entity.LocalBalance;
import com.tokimi.common.Utils;
import com.tokimi.common.chain.model.AddressGuardDTO;
import com.tokimi.common.chain.service.ChainService;
import com.tokimi.common.chain.service.tokenize.TokenizeService;
import com.tokimi.common.chain.service.wallet.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.tokimi.common.chain.utils.WalletType.EGRESS;

/**
 * @author william
 */
@Slf4j
public abstract class WalletServiceAdapter implements WalletService {

    @Resource
    protected ExchangeWalletDAO exchangeWalletDAO;

    @Resource
    protected ChainService chainService;

    @Resource
    protected AddressDAO addressDAO;

    @Resource
    protected LocalBalanceDAO localBalanceDAO;

    @Resource
    protected Collection<WalletEngine> walletEngines;

    @Override
    public void listAvailable(Long tokenId) {

        Address probe = new Address();
        probe.setTokenId(tokenId);

        List<Address> addresses = addressDAO.findAll(Example.of(probe));

        addresses.forEach(addr -> {
            String address = addr.getAddress();
            log.info("address {}, balance {}, token balance {}", address, balance(address, tokenId));
        });
    }

    @Override
    public boolean lock(TimeUnit timeUnit) {
        return false;
    }

    @Override
    public boolean unlock(byte[] passphrase) {
        return false;
    }

    @Override
    @Transactional
    public void setInsufficientLimit(Long tokenId, BigDecimal amount) {

        List<ExchangeWallet> wallets = findHotWallets(tokenId);

        wallets.forEach(wallet -> {
            wallet.setInsufficientLimit(amount);
            exchangeWalletDAO.saveAndFlush(wallet);
        });
    }

    @Override
    @Transactional
    public void setWithdrawLimit(Long tokenId, BigDecimal amount) {

        List<ExchangeWallet> wallets = findHotWallets(tokenId);

        wallets.forEach(wallet -> {
            wallet.setWithdrawLimit(amount);
            exchangeWalletDAO.saveAndFlush(wallet);
        });
    }

    @Override
    @Transactional
    public BigDecimal getInsufficientLimit(Long tokenId) {

        List<ExchangeWallet> wallets = findHotWallets(tokenId);

        if (Utils.isEmpty(wallets)) {
            return BigDecimal.ONE.negate();
        } else {
            return wallets.get(0).getInsufficientLimit();
        }
    }

    @Override
    @Transactional
    public BigDecimal getWithdrawLimit(Long tokenId) {

        List<ExchangeWallet> wallets = findHotWallets(tokenId);

        if (Utils.isEmpty(wallets)) {
            return BigDecimal.ONE.negate();
        } else {
            ExchangeWallet hotWallet = wallets.get(0);

            return hotWallet.getWithdrawLimit();
        }
    }

    protected List<ExchangeWallet> findHotWallets(Long tokenId) {

        ExchangeWallet probe = new ExchangeWallet();
        probe.setType(EGRESS.getValue());
        probe.setTokenId(tokenId);

        return exchangeWalletDAO.findAll(Example.of(probe));
    }

    protected String getSalt(Long tokenId, Long userId, Integer index) {

        String salt = null;

        if (userId > 0) {
            Address probe = new Address();
            probe.setTokenId(tokenId);
            probe.setUserId(userId);
            probe.setIndex(index);

            Optional<Address> any = addressDAO.findOne(Example.of(probe));

            if (any.isPresent()) {
                salt = any.get().getSalt();
            }
        } else {
            ExchangeWallet probe = new ExchangeWallet();
            probe.setUserId(userId);
            probe.setTokenId(tokenId);
            probe.setType(EGRESS.getValue());

            Optional<ExchangeWallet> any = exchangeWalletDAO.findOne(Example.of(probe));

            if (any.isPresent()) {
                salt = any.get().getSalt();
            }
        }

        return salt;
    }

    @Override
    public byte[] generateR(String addressStr) {
        Address probe = new Address();
        probe.setAddress(addressStr);
        Optional<Address> any = addressDAO.findOne(Example.of(probe));
        if (any.isPresent()) {
            Address address = any.get();
            WalletEngine walletEngine = walletEngines.stream()
                    .filter(item -> item.isSupportEngine(address.getTokenId())).findAny().orElse(null);

            if (Objects.nonNull(walletEngine)) {
                HdWalletDTO wallet = walletEngine.generateAddress(new DefaultSeedRule(address.getExtUserId(), address.getAppId(), address.getSalt()), address.getIndex(), null);
                return wallet.getRawPrivateKey();
            }
        }

        return null;
    }

    @Override
    public byte[] generateU(String addressStr) {
        Address probe = new Address();
        probe.setAddress(addressStr);
        List<Address> addressList = addressDAO.findAll(Example.of(probe));

        if (!Utils.isEmpty(addressList)) {
            Address address = addressList.get(0);

            Long userId = address.getUserId();
            Integer index = address.getIndex();
            String salt = getSalt(address.getTokenId(), userId, index);

            Optional<WalletEngine> any = walletEngines.stream()
                    .filter(item -> item.isSupportEngine(address.getTokenId())).findAny();

            if (any.isPresent()) {
                HdWalletDTO wallet = any.get().generateAddress(new DefaultSeedRule(userId.toString(), "", salt), index, null);
                return wallet.getRawPublicKey();
            }
        }

        return null;
    }

    @Override
    public BigDecimal precision(Long tokenId) {
        return BigDecimal.TEN.pow(scale(tokenId));
    }

    @Override
    public void syncAll(Long tokenId) {
        Address probe = new Address();
        probe.setTokenId(chainService.getAssetId());
        List<Address> addresses = addressDAO.findAll(Example.of(probe));
        addresses.forEach(address -> sync(tokenId, address.getUserId(), address.getAddress()));
    }

    protected Long getUserId(Long tokenId, String address) {
        Address probe = new Address();
        probe.setTokenId(tokenId);
        probe.setAddress(address);
        List<Address> addresses = addressDAO.findAll(Example.of(probe));

        if (!Utils.isEmpty(addresses)) {
            return addresses.get(0).getUserId();
        }

        return null;
    }

    @Override
    public BigDecimal add(Long tokenId, Long userId, String address, BigDecimal amount) {

        LocalBalance probe = new LocalBalance();
        probe.setTokenId(tokenId.longValue());
        probe.setAddress(address);
        Optional<LocalBalance> any = localBalanceDAO.findOne(Example.of(probe));

        LocalBalance addressBalance;

        if (any.isPresent()) {
            addressBalance = any.get();
            BigDecimal balance = addressBalance.getBalance();

            if (null == balance || balance.compareTo(BigDecimal.ZERO) < 0) {
                log.error("sync balance token {} address {} firstly", tokenId, address);
                return BigDecimal.ZERO;
            } else {
                addressBalance.setBalance(balance.add(amount));
            }
        } else {
            addressBalance = new LocalBalance();
            addressBalance.setTokenId(tokenId.longValue());
            addressBalance.setAddress(address);
            addressBalance.setBalance(amount);
            addressBalance.setUserId(userId);
        }

        localBalanceDAO.saveAndFlush(addressBalance);

        return addressBalance.getBalance();
    }

    public abstract TokenizeService getTokenizeService();

    @Override
    public boolean isSupport(Long tokenId) {
        return getTokenizeService().isSupport(tokenId);
    }

    @Override
    public void importAll() {

        Address probe = new Address();
        probe.setTokenId(chainService.getAssetId());

        addressDAO.findAll().forEach(addressGuardDTO -> importOne(new AddressGuardDTO(addressGuardDTO.getTokenId(),
                addressGuardDTO.getUserId(), addressGuardDTO.getAddress())));
    }
}