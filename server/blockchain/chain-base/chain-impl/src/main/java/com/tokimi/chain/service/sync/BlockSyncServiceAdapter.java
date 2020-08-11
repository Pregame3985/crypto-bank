package com.tokimi.chain.service.sync;

import com.tokimi.chain.dao.BlockInfoDAO;
import com.tokimi.chain.dao.DepositFlowDAO;
import com.tokimi.chain.dao.PendingTxDAO;
import com.tokimi.chain.entity.BlockInfo;
import com.tokimi.chain.entity.DepositFlow;
import com.tokimi.chain.entity.PendingTx;
import com.tokimi.common.Utils;
import com.tokimi.common.chain.model.BlockDTO;
import com.tokimi.common.chain.model.ChainTransaction;
import com.tokimi.common.chain.model.RawTx;
import com.tokimi.common.chain.model.ReceiverDTO;
import com.tokimi.common.chain.model.TransactionDTO;
import com.tokimi.common.chain.service.ChainService;
import com.tokimi.common.chain.service.sync.BlockService;
import com.tokimi.common.chain.service.sync.BlockSyncService;
import com.tokimi.common.chain.service.sync.IndexService;
import com.tokimi.common.chain.service.tx.TransactionService;
import com.tokimi.common.chain.utils.DepositStatus;
import com.tokimi.common.chain.utils.TokenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.tokimi.common.chain.utils.TokenType.NATIVE;
import static com.tokimi.common.chain.utils.TokenType.TOKEN;

/**
 * @author william
 */
@Slf4j
@Service
public abstract class BlockSyncServiceAdapter implements BlockSyncService, IndexService {

    @Resource
    protected ChainService chainService;

    @Resource
    protected PendingTxDAO pendingTxDAO;

    @Resource
    protected DepositFlowDAO depositFlowDAO;

    @Resource
    protected BlockService blockService;

    @Resource
    private BlockInfoDAO blockInfoDAO;

    @Override
    @Transactional
    public void parseBlock(BlockDTO latestBlockDTO) {

        List<ChainTransaction<? extends RawTx>> txs = latestBlockDTO.getTxs();

        if (!Utils.isEmpty(txs)) {
            txs.parallelStream().map(wrapTx -> {
                TransactionDTO chainTx;
                if (wrapTx.needFetch()) {
                    chainTx = getTransactionService(wrapTx.getTokenType()).get(wrapTx.getTxid(),
                            address -> getAddresses().contains(address));
                } else {
                    chainTx = getTransactionService(wrapTx.getTokenType()).parseTx(() -> wrapTx, null,
                            address -> getAddresses().contains(address));
                }
                return chainTx;
            }).filter(Objects::nonNull).forEach(chainTx -> {

                String blockhash = chainTx.getBlockhash();

                if (Utils.isEmpty(blockhash)) {
                    return;
                }

                log.debug("sync tx {}", chainTx.getTxid());

                this.syncTx(latestBlockDTO, chainTx);
            });

            log.info("parse done at block {}({}) / handle {} txs", latestBlockDTO.getHash(), latestBlockDTO.getHeight(),
                    txs.size());
        }
    }

    @Override
    @Transactional
    public void resyncTokenTx(String txid) {

        TransactionDTO chainTx = getTransactionService(TOKEN).get(txid,
                address -> getAddresses().contains(address));

        BlockDTO blockDTO = blockService.getBlock(chainTx.getBlockhash());

        syncTx(blockDTO, chainTx);
    }


    @Override
    @Transactional
    public void resyncNativeTx(String txid) {

        TransactionDTO chainTx = getTransactionService(NATIVE).get(txid,
                address -> getAddresses().contains(address));

        BlockDTO blockDTO = blockService.getBlock(chainTx.getBlockhash());

        syncTx(blockDTO, chainTx);
    }

    @Override
    public Mono<BlockDTO> sync(BlockDTO localBlockDTO, Long height) {

        BlockDTO newRemoteBlockDTO = blockService.getBlock(height);

        if (null == localBlockDTO || newRemoteBlockDTO.getPreviousblockhash().equals(localBlockDTO.getHash())) {

            log.info("sync block at {}, hash: {}", newRemoteBlockDTO.getHeight(), newRemoteBlockDTO.getHash());

            StopWatch stopWatch = new StopWatch();

            stopWatch.start();

            // fetch tx from block info
            parseBlock(newRemoteBlockDTO);

            stopWatch.stop();

            newRemoteBlockDTO.setParseSeconds((int) TimeUnit.MILLISECONDS.toSeconds(stopWatch.getLastTaskTimeMillis()));

            blockService.saveBlock(newRemoteBlockDTO);

            return Mono.fromSupplier(() -> newRemoteBlockDTO);
        } else {

            log.info("reorg happened at height {}", localBlockDTO.getHeight());

            return Mono.fromSupplier(() -> calibrate(localBlockDTO, height));
        }
    }

    private BlockDTO calibrate(BlockDTO localBlockDTO, Long height) {

        if (null != localBlockDTO) {

            reorg(localBlockDTO);

            log.info("reorg height at : {}", localBlockDTO.getHeight());

            localBlockDTO = blockService.getLocalBestBlock();
        }

        return localBlockDTO;
    }

    private void reorg(BlockDTO localBlockDTO) {

        BlockInfo headLogProbe = new BlockInfo();
        headLogProbe.setCurrentHash(localBlockDTO.getHash());
        headLogProbe.setHeight(localBlockDTO.getHeight());

        blockInfoDAO.findAll(Example.of(headLogProbe)).forEach(headLog -> {

            headLog.setReorg(true);

            blockInfoDAO.saveAndFlush(headLog);
        });

        PendingTx probe = new PendingTx();
        probe.setBlockHash(localBlockDTO.getHash());
        probe.setHeight(localBlockDTO.getHeight());
        List<PendingTx> txs = pendingTxDAO.findAll(Example.of(probe));

        txs.forEach(pendingTx -> {

            pendingTx.setReorg(true);
            pendingTx.setProcessed(false);

            pendingTxDAO.saveAndFlush(pendingTx);
        });

        // TODO: handle reorg tx, first need to record block hash in deposit record
    }

    @Override
    @Transactional
    public void fix() {
        List<PendingTx> headlogTxs = pendingTxDAO.findAll();

        headlogTxs.forEach(dbTx -> {

            TransactionDTO chainTx = getTransactionService(defaultTokenType()).get(dbTx.getTxid());

            log.info("fix head log tx : {}, token id : {}", dbTx.getId(), dbTx.getTokenId());

            chainTx.getReceivers().stream().filter(receiver -> dbTx.getAddress().equals(receiver.getAddress()))
                    .forEach(receiver -> {
                        if (null != dbTx.getIndex()) {
                            if (dbTx.getIndex().equals(receiver.getIndex())) {
                                dbTx.setAmount(receiver.getAmount());
                            }
                        } else {
                            dbTx.setAmount(receiver.getAmount());
                        }
                        pendingTxDAO.saveAndFlush(dbTx);
                    });
        });

        DepositFlow probe = new DepositFlow();
        probe.setTokenType(TOKEN.getValue());
        List<DepositFlow> data = depositFlowDAO.findAll(Example.of(probe));
        data.forEach(depositFlow -> depositFlow.setTokenType(TOKEN.getValue()));
        depositFlowDAO.saveAll(data);
    }

    @Override
    public void verify() {
        List<PendingTx> headlogTxs = pendingTxDAO.findAll();

        headlogTxs.forEach(dbTx -> {
            DepositFlow probe = new DepositFlow();
            probe.setTokenId(dbTx.getTokenId());
            probe.setTxid(dbTx.getTxid());
            probe.setToAddress(dbTx.getAddress());
            probe.setUserId(dbTx.getUserId());

            log.info("verify head log tx : {}, token id : {}", dbTx.getId(), dbTx.getTokenId());

            depositFlowDAO.findOne(Example.of(probe)).ifPresent(depositFlow -> {
                if (depositFlow.getStatus().equals(DepositStatus.CONFIRMED.getValue())) {

                    if (null == depositFlow.getAmount()) {
                        log.info("flow {} amount is null", depositFlow.getId());
                    } else {
                        if (null == depositFlow.getIndex()) {
                            depositFlow.setIndex(dbTx.getIndex());
                            depositFlowDAO.saveAndFlush(depositFlow);
                        } else {
                            if (depositFlow.getAmount().equals(dbTx.getAmount())) {
                                pendingTxDAO.saveAndFlush(dbTx);

                                depositFlow.setHeight(dbTx.getHeight());
                                depositFlow.setTokenType(TOKEN.getValue());
                                depositFlowDAO.saveAndFlush(depositFlow);
                            }
                        }
                    }
                }
            });
        });
    }

    protected void syncTx(BlockDTO blockDTO, TransactionDTO sinceBlock) {

        String txid = sinceBlock.getTxid();

        TransactionDTO chainTx = sinceBlock;

        if (chainTx == null) {
            return;
        }

        if (!chainTx.isSuccess()) {
            log.info("get txid {} failed, error msg {}", txid, chainTx.getError().getMessage());
            return;
        }

        log.debug("sync tx {} from block {}", chainTx.getTxid(), chainTx.getBlockheight());

        saveTx(blockDTO, chainTx);
    }

    protected TokenType defaultTokenType() {
        return NATIVE;
    }

    protected abstract Collection<String> getAddresses();

    protected abstract void saveTx(BlockDTO blockDTO, TransactionDTO chainTx);

    // protected abstract TransactionService getTxTransactionService();

    protected abstract TransactionService getTransactionService(TokenType tokenType);

    protected abstract void setTxTokenId(ReceiverDTO receiver, PendingTx pendingTx);
}