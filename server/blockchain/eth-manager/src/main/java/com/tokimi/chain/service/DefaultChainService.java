package com.tokimi.chain.service;

import com.tokimi.common.chain.service.AssetService;
import com.tokimi.common.chain.service.ChainService;
import com.tokimi.common.network.rpc.JsonRpcAgent;
import com.tokimi.config.ManagerHub;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author william
 */
@Service
public class DefaultChainService implements ChainService {

    @Setter
    @Getter
    @Resource
    private JsonRpcAgent jsonRpcAgent;

    @Resource
    private AssetService assetService;

    @Override
    public Long getAssetId() {
        return ManagerHub.getInstance().getAssetDTO().getId();
    }

    @Override
    public Integer getNetwork() {
        return ManagerHub.getInstance().getAssetDTO().getOnNetwork();
    }

    @Override
    public Integer getDepositConfirmations() {
        return ManagerHub.getInstance().getAssetDTO().getDepositConfirmations();
    }

    @Override
    public Integer getWithdrawConfirmations() {
        return ManagerHub.getInstance().getAssetDTO().getWithdrawConfirmations();
    }

    @Override
    public Long getBlockHeight() {
        if (null != ManagerHub.getInstance().getRemoteBestBlock()) {
            return ManagerHub.getInstance().getRemoteBestBlock().getHeight();
        } else {
            return null;
        }
    }
}
