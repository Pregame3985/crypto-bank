package com.tokimi.chain.service.wallet;

import com.tokimi.chain.dao.LocalBalanceDAO;
import com.tokimi.chain.dao.SequenceDAO;
import com.tokimi.chain.entity.LocalBalance;
import com.tokimi.chain.rpc.model.eth.request.GetBalanceRequest;
import com.tokimi.chain.rpc.model.eth.response.GetBalanceResponse;
import com.tokimi.common.chain.model.AddressGuardDTO;
import com.tokimi.common.chain.model.AssetDTO;
import com.tokimi.common.chain.model.PropertyDTO;
import com.tokimi.common.chain.service.AssetService;
import com.tokimi.common.chain.service.tokenize.TokenizeService;
import com.tokimi.common.chain.utils.BinaryUtils;
import com.tokimi.config.ManagerHub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * @author william
 */
@Slf4j
@Service
public class DefaultWalletService extends WalletServiceAdapter {

    // setting default fee scale is 1.0
    @Value("${app.chain.feeScale:1.0}")
    private Double feeScale;

    @Resource
    private LocalBalanceDAO localBalanceDAO;

    @Resource
    private SequenceDAO sequenceDAO;

    @Resource
    private AssetService assetService;

    @Override
    public TokenizeService getTokenizeService() {
        return this;
    }

    @Override
    public boolean isSupport(Long tokenId) {
        return ManagerHub.getInstance().getAssetIds().contains(tokenId);
    }

    @Override
    public BigDecimal balance(String address, Long tokenId) {

        LocalBalance probe = new LocalBalance();
        probe.setTokenId(tokenId);
        probe.setAddress(address.toLowerCase());

        Optional<LocalBalance> any = localBalanceDAO.findOne(Example.of(probe));

        if (any.isPresent()) {
            return any.get().getBalance();
        } else {
            return BigDecimal.ZERO;
        }
    }

    @Override
    @Transactional
    public void updateBalance(Long tokenId) {

        findHotWallets(tokenId).forEach(hotWallet -> hotWallet.setGasBalance(balance(hotWallet.getAddress(), tokenId)));
    }

    @Override
    public int scale(Long tokenId) {
        PropertyDTO defaultPropertyDTO = new PropertyDTO();
        defaultPropertyDTO.setDecimal(18);

        AssetDTO asset = assetService.getAsset(tokenId);
        return asset.getProperties()
                .stream().findFirst()
                .orElse(defaultPropertyDTO).getDecimal();
    }

    @Override
    public BigDecimal sync(Long tokenId, Long userId, String address) {

        LocalBalance probe = new LocalBalance();
        probe.setTokenId(tokenId);
        probe.setAddress(address);
        Optional<LocalBalance> any = localBalanceDAO.findOne(Example.of(probe));

        LocalBalance localBalance;

        if (any.isPresent()) {
            localBalance = any.get();
        } else {
            localBalance = new LocalBalance();
            localBalance.setTokenId(tokenId);
            localBalance.setAddress(address);
        }

        localBalance.setUserId(getUserId(tokenId, address));
        localBalance.setBalance(balance(address, tokenId));

        GetBalanceResponse response = getBalance(address);

        if (response.isSuccess()) {

            BigDecimal precision = precision(chainService.getAssetId());
            int scale = scale(chainService.getAssetId());

            localBalance.setBalance(new BigDecimal(BinaryUtils.hexToLong(response.getResult())).divide(precision, scale,
                    RoundingMode.DOWN));
        } else {
            localBalance.setBalance(BigDecimal.ZERO);
        }

        log.info("token id {}, address {}, balance {}", tokenId, address, localBalance.getBalance().toPlainString());

        localBalanceDAO.saveAndFlush(localBalance);

        return localBalance.getBalance();
    }

    @Override
    public void importOne(AddressGuardDTO addressGuardDTO) {

        if (addressGuardDTO.getTokenId().equals(chainService.getAssetId())) {

            ManagerHub.getInstance().getAddress().putIfAbsent(
                    addressGuardDTO.getAddress().toLowerCase(), addressGuardDTO.getUserId());
        }
    }

    private GetBalanceResponse getBalance(String address) {

        GetBalanceRequest request = new GetBalanceRequest(address);
        return chainService.getJsonRpcAgent().sendToNetwork(request);
    }
}