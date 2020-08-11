package com.tokimi.config;

import com.tokimi.common.chain.model.AssetDTO;
import com.tokimi.common.chain.model.BlockDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author william
 */
public class ManagerHub {

    private static ManagerHub instance;

    private ManagerHub() {

        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public synchronized static ManagerHub getInstance() {
        if (null == instance) {
            instance = new ManagerHub();
        }

        return instance;
    }

    public boolean isSyncReady() {
        return isBlockReady;
    }

    public boolean isDepositReady() {
        return isBlockReady && isTxReady;
    }

    public boolean isWithdrawReady() {
        return isBlockReady && isTxReady;
    }

    @Getter
    @Setter
    private AssetDTO assetDTO;

    @Getter
    @Setter
    private List<AssetDTO> assetDTOs;

    public List<Long> getAssetIds() {
        return this.assetDTOs.stream().map(AssetDTO::getId).collect(Collectors.toList());
    }

    @Setter
    private List<Long> assetIds;

    @Getter
    @Setter
    private BlockDTO remoteBestBlock;

    @Getter
    @Setter
    private BlockDTO localBestBlock;

    @Getter
    @Setter
    private BigDecimal feeRate;

    @Getter
    private volatile Map<String, Long> address = new HashMap<>();

    @Getter
    @Setter
    private boolean isBlockReady;

    @Getter
    @Setter
    private boolean isTxReady;
}
