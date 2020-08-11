package com.tokimi.tasks;

import com.tokimi.common.chain.model.BlockDTO;
import com.tokimi.common.chain.service.sync.BlockService;
import com.tokimi.common.chain.service.sync.BlockSyncService;
import com.tokimi.config.AppFunction;
import com.tokimi.config.ManagerHub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

@Slf4j
@Component
public class BlockSyncTask {

    @Value("${app.chain.sync-from}")
    private Long syncFromHeight;

    @Value("${app.chain.fetch-count:1}")
    private Integer fetchCount;

    @Value("${app.chain.catch-mode:false}")
    private boolean catchMode;

    @Resource
    private AppFunction appFunction;

    @Resource
    private BlockSyncService blockSyncService;

    @Resource
    private BlockService blockService;

    @Scheduled(
            initialDelayString = "${app.tasks.syncBlock.schedule.initialDelay}",
            fixedDelayString = "${app.tasks.syncBlock.schedule.fixedDelay}"
    )
    public void task() {

        if (!appFunction.isSync()) {
            return;
        }

        try {
            if (ManagerHub.getInstance().isSyncReady()) {

                BlockDTO remoteBlockDTO = ManagerHub.getInstance().getRemoteBestBlock();
                BlockDTO localBlockDTO = ManagerHub.getInstance().getLocalBestBlock();

                if (null == localBlockDTO) {

                    log.info("remote block height: {}, local block height is null", remoteBlockDTO.getHeight());

                    blockSyncService.sync(null, (null != syncFromHeight) ? syncFromHeight : remoteBlockDTO.getHeight())
                            .subscribe(blockDTO -> ManagerHub.getInstance().setLocalBestBlock(blockDTO), null, () -> {

                                BlockDTO remoteBlock = ManagerHub.getInstance().getRemoteBestBlock();
                                BlockDTO localBlock = ManagerHub.getInstance().getLocalBestBlock();
                                blockService.updateBlockStatus(remoteBlock.getHeight(), localBlock.getHeight());
                            });
                } else if (remoteBlockDTO.getHeight() > localBlockDTO.getHeight()) {

                    int delta = remoteBlockDTO.getHeight().intValue() - localBlockDTO.getHeight().intValue();

                    Integer range = catchMode ? delta : fetchCount;

                    Flux.range(1, range).flatMap(step -> {
                        BlockDTO latestlocalBlockDTO = ManagerHub.getInstance().getLocalBestBlock();

                        log.info("remote block height: {}, local block height: {}, step: {}, catch mode: {}",
                                remoteBlockDTO.getHeight(), latestlocalBlockDTO.getHeight(), step, catchMode);

                        return blockSyncService.sync(latestlocalBlockDTO, latestlocalBlockDTO.getHeight() + 1);
                    }).subscribe(blockDTO -> {
                        if (blockDTO.getHeight() > 0) {
                            ManagerHub.getInstance().setLocalBestBlock(blockDTO);
                        } else {
                            ManagerHub.getInstance().setLocalBestBlock(null);
                        }
                    }, null, () -> {

                        BlockDTO remoteBlock = ManagerHub.getInstance().getRemoteBestBlock();
                        BlockDTO localBlock = ManagerHub.getInstance().getLocalBestBlock();
                        blockService.updateBlockStatus(remoteBlock.getHeight(), localBlock.getHeight());
                    });
                } else {

                    blockService.getBestBlockReactive().subscribe(remoteBlock -> ManagerHub.getInstance().setRemoteBestBlock(remoteBlock), null, () -> {

                        BlockDTO remoteBlock = ManagerHub.getInstance().getRemoteBestBlock();
                        BlockDTO localBlock = ManagerHub.getInstance().getLocalBestBlock();
                        blockService.updateBlockStatus(remoteBlock.getHeight(), localBlock.getHeight());
                    });
                }
            }
        } catch (Exception e) {
            log.error("error running sync task", e);
            throw e;
        }
    }
}