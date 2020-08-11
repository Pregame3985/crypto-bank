package com.tokimi.chain.service.sync;

import com.tokimi.chain.dao.BlockInfoDAO;
import com.tokimi.chain.entity.PendingTx;
import com.tokimi.chain.model.ChainDTO;
import com.tokimi.common.ChainManagerException;
import com.tokimi.common.ErrorConstants;
import com.tokimi.common.chain.model.BlockDTO;
import com.tokimi.common.chain.model.ReceiverDTO;
import com.tokimi.common.chain.model.TransactionDTO;
import com.tokimi.common.chain.service.tx.TransactionService;
import com.tokimi.common.chain.utils.TokenType;
import com.tokimi.config.ManagerHub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * @author william
 */
@Slf4j
@Service
public class DefaultBlockSyncService extends BlockSyncServiceAdapter {

    @Resource
    private ChainDTO chainDTO;

    @Resource
    private BlockInfoDAO blockInfoDAO;

    @Resource
    private TransactionService nativeTransactionService;

    @Resource
    private TransactionService tokenTransactionService;

    @Override
    protected TransactionService getTransactionService(TokenType tokenType) {
        if (tokenType.equals(TokenType.NATIVE)) {
            return nativeTransactionService;
        } else if (tokenType.equals(TokenType.TOKEN)) {
            return tokenTransactionService;
        }

        throw new ChainManagerException(ErrorConstants.TX_SERVICE_NOT_FOUND);
    }

    @Override
    protected void setTxTokenId(ReceiverDTO receiver, PendingTx pendingTx) {
        if (pendingTx.getTokenType().equals(TokenType.NATIVE.getValue())) {
            pendingTx.setTokenId(chainService.getAssetId());
        } else {

        }
    }

    @Override
    protected Collection<String> getAddresses() {
        return ManagerHub.getInstance().getAddress().keySet();
    }

    protected void saveTx(BlockDTO blockDTO, TransactionDTO chainTx) {

        List<ReceiverDTO> receivers = chainTx.getReceivers();

        if (!CollectionUtils.isEmpty(receivers)) {

            receivers.forEach(receiver -> {

                log.debug("save tx {} index {} block {}", chainTx.getTxid(), receiver.getIndex(), blockDTO.getHeight());

                PendingTx pendingTx = new PendingTx();

                pendingTx.setHeight(blockDTO.getHeight());
                pendingTx.setBlockHash(blockDTO.getHash());
                pendingTx.setTxid(chainTx.getTxid());
                pendingTx.setIndex(receiver.getIndex());
                pendingTx.setAmount(receiver.getAmount());
                pendingTx.setAddress(receiver.getAddress());
                pendingTx.setUserId(ManagerHub.getInstance().getAddress().get(receiver.getAddress()));
                pendingTx.setTokenId(chainTx.getTokenId());

                if (pendingTxDAO.count(Example.of(pendingTx)) == 0) {

                    pendingTx.setTokenType(chainTx.getTokenType().getValue());

                    pendingTx.setReorg(false);
                    pendingTx.setProcessed(false);

                    pendingTxDAO.saveAndFlush(pendingTx);
                }
            });
        }
    }

}