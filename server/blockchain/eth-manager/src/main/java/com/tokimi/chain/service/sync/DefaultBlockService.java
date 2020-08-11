package com.tokimi.chain.service.sync;

import com.tokimi.chain.dao.BlockInfoDAO;
import com.tokimi.chain.dao.BlockSyncStatusDAO;
import com.tokimi.chain.entity.BlockInfo;
import com.tokimi.chain.entity.BlockSyncStatus;
import com.tokimi.chain.model.ChainTxWrapper;
import com.tokimi.chain.rpc.model.eth.request.BlockNumberRequest;
import com.tokimi.chain.rpc.model.eth.request.GetBlockByHashRequest;
import com.tokimi.chain.rpc.model.eth.request.GetBlockByNumberRequest;
import com.tokimi.chain.rpc.model.eth.response.Block;
import com.tokimi.chain.rpc.model.eth.response.BlockNumberResponse;
import com.tokimi.chain.rpc.model.eth.response.GetBlockResponse;
import com.tokimi.chain.rpc.model.eth.response.Transaction;
import com.tokimi.common.ChainManagerException;
import com.tokimi.common.chain.model.BlockDTO;
import com.tokimi.common.chain.model.ChainTransaction;
import com.tokimi.common.chain.model.RawTx;
import com.tokimi.common.chain.service.ChainService;
import com.tokimi.common.chain.service.sync.BlockService;
import com.tokimi.common.chain.utils.BinaryUtils;
import com.tokimi.config.ManagerHub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.tokimi.common.ErrorConstants.BLOCK_FETCH_FAILED;

/**
 * @author william
 */
@Slf4j
@Service
public class DefaultBlockService implements BlockService {

    @Resource
    private BlockInfoDAO blockInfoDAO;

    @Resource
    private BlockSyncStatusDAO blockSyncStatusDAO;

    @Resource
    private ChainService chainService;

    @Override
    public BlockDTO getBestBlock() {

        BlockNumberResponse response = chainService.getJsonRpcAgent().sendToNetwork(new BlockNumberRequest());

        if (null == response || !response.isSuccess()) {
            return null;
        }

        return getBlock(BinaryUtils.hexToLong(response.getResult()));
    }

    @Override
    public BlockDTO getBlock(Long height) {

        GetBlockResponse response = chainService.getJsonRpcAgent().sendToNetwork(new GetBlockByNumberRequest(height));

        if (null == response || !response.isSuccess()) {
            throw new ChainManagerException(BLOCK_FETCH_FAILED, "fetch block failed");
        }

        return parseChainBlock(response);
    }

    @Override
    public BlockDTO getBlock(String hash) {

        GetBlockResponse response = chainService.getJsonRpcAgent().sendToNetwork(new GetBlockByHashRequest(hash));

        if (null == response || !response.isSuccess()) {
            throw new ChainManagerException(BLOCK_FETCH_FAILED, "fetch block failed");
        }

        return parseChainBlock(response);
    }

    private BlockDTO parseChainBlock(GetBlockResponse response) {

        BlockDTO blockDTO = new BlockDTO();

        Block result = response.getResult();

        blockDTO.setHeight(BinaryUtils.hexToLong(result.getNumber()));
        blockDTO.setHash(result.getHash());
        blockDTO.setPreviousblockhash(result.getPreviousblockhash());
        blockDTO.setTxCount(result.getTx().size());
        blockDTO.setTime(Instant.ofEpochSecond(BinaryUtils.hexToLong(result.getTimestamp()))
                .atZone(ZoneId.systemDefault()).toLocalDateTime());

        if (ManagerHub.getInstance().isBlockReady()) {
            List<ChainTransaction<? extends RawTx>> txs = new ArrayList<>();
            List<String> txids = new ArrayList<>();

            for (Transaction tx : result.getTx()) {
                tx.setConfirmations(chainService.getBlockHeight() - blockDTO.getHeight() + 1);
                tx.setBlockHeight(blockDTO.getHeight());
                tx.setBlockhash(blockDTO.getHash());
                txs.add(new ChainTxWrapper(chainService, this, tx));
                txids.add(tx.getTxid());
            }

            blockDTO.setTxs(txs);
            blockDTO.setTxids(txids);
        }

        return blockDTO;
    }

    @Override
    @Transactional
    public void saveBlock(BlockDTO blockDTO) {

        BlockInfo blockInfo = new BlockInfo();

        blockInfo.setHeight(blockDTO.getHeight());
        blockInfo.setCurrentHash(blockDTO.getHash());
        blockInfo.setPrevHash(blockDTO.getPreviousblockhash());
        blockInfo.setTxCount(blockDTO.getTxCount());
        blockInfo.setConfirmedTime(blockDTO.getTime());
        blockInfo.setParseSeconds(blockDTO.getParseSeconds());
        blockInfo.setReorg(false);

        blockInfoDAO.saveAndFlush(blockInfo);
    }

    @Override
    @Transactional
    public void updateBlockStatus(Long remoteHeight, Long localHeight) {

        BlockSyncStatus probe = new BlockSyncStatus();

        probe.setTokenId(chainService.getAssetId());

        Optional<BlockSyncStatus> any = blockSyncStatusDAO.findOne(Example.of(probe));

        BlockSyncStatus blockSyncStatus;
        if (any.isPresent()) {
            blockSyncStatus = any.get();
        } else {
            blockSyncStatus = new BlockSyncStatus();
            blockSyncStatus.setTokenId(chainService.getAssetId());
        }

        blockSyncStatus.setSyncedHeight(localHeight);
        blockSyncStatus.setCurrentHeight(remoteHeight);
        blockSyncStatusDAO.saveAndFlush(blockSyncStatus);
    }

    @Override
    public Mono<BlockDTO> getBestBlockReactive() {

        Mono<BlockNumberResponse> response = chainService.getJsonRpcAgent().send(new BlockNumberRequest());

        return response.filter(item -> null != item && item.isSuccess())
                .flatMap(item -> getBlockReactive(BinaryUtils.hexToLong(item.getResult())));
    }

    @Override
    public Mono<BlockDTO> getBlockReactive(Long height) {

        Mono<GetBlockResponse> response = chainService.getJsonRpcAgent().send(new GetBlockByNumberRequest(height));

        return response.filter(item -> null != item && item.isSuccess()).map(this::parseChainBlock);
    }

    @Override
    public Mono<BlockDTO> getBlockReactive(String hash) {

        Mono<GetBlockResponse> response = chainService.getJsonRpcAgent().send(new GetBlockByHashRequest(hash));

        return response.filter(item -> null != item && item.isSuccess()).map(this::parseChainBlock);
    }

    @Override
    @Transactional
    public BlockDTO getLocalBestBlock() {

        BlockInfo blockInfo = blockInfoDAO.findTopByReorgOrderByIdDesc(false);

        if (null != blockInfo) {
            BlockDTO localBlockDTO = new BlockDTO();

            localBlockDTO.setHeight(blockInfo.getHeight());
            localBlockDTO.setHash(blockInfo.getCurrentHash());
            localBlockDTO.setPreviousblockhash(blockInfo.getPrevHash());
            localBlockDTO.setTime(blockInfo.getConfirmedTime());

            return localBlockDTO;
        }

        return null;
    }

    @Override
    @Transactional
    public BlockDTO getLocalBlock(String hash) {

        BlockInfo probe = new BlockInfo();
        probe.setCurrentHash(hash);
        probe.setReorg(false);
        Optional<BlockInfo> any = blockInfoDAO.findOne(Example.of(probe));

        if (any.isPresent()) {

            BlockDTO localBlockDTO = new BlockDTO();

            localBlockDTO.setHeight(any.get().getHeight());
            localBlockDTO.setHash(any.get().getCurrentHash());
            localBlockDTO.setPreviousblockhash(any.get().getPrevHash());
            localBlockDTO.setTime(any.get().getConfirmedTime());

            return localBlockDTO;
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public BlockDTO getLocalBlock(Long height) {

        BlockInfo probe = new BlockInfo();
        probe.setHeight(height);
        probe.setReorg(false);
        Optional<BlockInfo> any = blockInfoDAO.findOne(Example.of(probe));

        if (any.isPresent()) {

            BlockDTO localBlockDTO = new BlockDTO();

            localBlockDTO.setHeight(any.get().getHeight());
            localBlockDTO.setHash(any.get().getCurrentHash());
            localBlockDTO.setPreviousblockhash(any.get().getPrevHash());
            localBlockDTO.setTime(any.get().getConfirmedTime());

            return localBlockDTO;
        } else {
            return null;
        }
    }
}