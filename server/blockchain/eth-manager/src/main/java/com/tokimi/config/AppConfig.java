package com.tokimi.config;

import com.tokimi.common.chain.model.AssetDTO;
import com.tokimi.common.chain.service.AssetService;
import com.tokimi.common.chain.service.sync.BlockService;
import com.tokimi.common.chain.service.wallet.WalletService;
import com.tokimi.common.network.rpc.JsonRpcAgent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author william
 */
@Slf4j
@Configuration
public class AppConfig {

    @Resource
    private AssetService assetService;

    @Resource
    private BlockService blockService;

    @Resource
    private WalletService walletService;

    @Setter
    @Resource
    private JsonRpcAgent jsonRpcAgent;

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {

        // load asset
        AssetDTO asset = assetService.getManagerAsset();
        ManagerHub.getInstance().setAssetDTO(asset);

        List<AssetDTO> assets = assetService.getMyAssets();
        ManagerHub.getInstance().setAssetDTOs(assets);

        log.info("asset loaded");

        // for sync
        // check remote and local block
        blockService.getBestBlockReactive()
                .subscribe(
                        remoteBlock -> {
                            ManagerHub.getInstance().setRemoteBestBlock(remoteBlock);
                            ManagerHub.getInstance().setLocalBestBlock(blockService.getLocalBestBlock());
                            ManagerHub.getInstance().setBlockReady(true);

                            log.info("block fetch success");
                        }
                );

        // for deposit import address
        walletService.importAll();
        ManagerHub.getInstance().setTxReady(true);

        log.info("addresses imported");

         // for withdraw
//         // fee rate
//         jsonRpcAgent.send(new EstimateSmartFeeRpcRequest(asset.getWithdrawConfirmations()))
//                 .filter(response -> null != response && response.isSuccess())
//                 .subscribe(response -> ManagerHub.getInstance().setFeeRate(response.getResult().getFeerate()));
    }
}
