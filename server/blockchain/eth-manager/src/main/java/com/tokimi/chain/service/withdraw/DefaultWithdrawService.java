package com.tokimi.chain.service.withdraw;

import com.tokimi.chain.dao.UtxoDAO;
import com.tokimi.chain.entity.Utxo;
import com.tokimi.chain.entity.WithdrawDetail;
import com.tokimi.chain.entity.WithdrawFlow;
import com.tokimi.chain.entity.WithdrawRequest;
import com.tokimi.chain.service.tx.WithdrawServiceAdapter;
import com.tokimi.common.Utils;
import com.tokimi.common.chain.model.AssetDTO;
import com.tokimi.common.chain.model.FlowDTO;
import com.tokimi.common.chain.model.TrackDTO;
import com.tokimi.common.chain.model.TransactionDTO;
import com.tokimi.common.chain.model.WithdrawRequestDTO;
import com.tokimi.common.chain.service.tx.TransactionService;
import com.tokimi.common.chain.utils.TokenType;
import com.tokimi.common.chain.utils.TrackStatus;
import com.tokimi.common.chain.utils.TxType;
import com.tokimi.common.chain.utils.WithdrawStatus;
import com.tokimi.config.ManagerHub;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.tokimi.common.chain.utils.SweepStatus.PENDING;
import static com.tokimi.common.chain.utils.TxType.FEE;

/**
 * @author william
 */
@Slf4j
@Service
public class DefaultWithdrawService extends WithdrawServiceAdapter {

    @Setter
    @Resource
    private UtxoDAO utxoDAO;

    @Resource
    private TransactionService tokenTransactionService;

    @Override
    protected boolean isAllowingToken(Long tokenId) {

        if (null == tokenId) {
            return false;
        }

        List<AssetDTO> tokens = ManagerHub.getInstance().getAssetDTOs();

        return tokens.stream().map(AssetDTO::getId).collect(Collectors.toList()).contains(tokenId);
    }

    protected TransactionService getTransactionService(TokenType tokenType) {
        if (TokenType.NATIVE.equals(tokenType)) {
            return getTransactionService();
        } else if (TokenType.TOKEN.equals(tokenType)) {
            return tokenTransactionService;
        } else {
            return tokenTransactionService;
        }
    }

    @Override
    protected TransactionDTO generateTransaction(Long tokenId, Integer tokenType, Integer txType,
                                                 List<WithdrawRequestDTO> withdrawRequests) {

        // for now, only contain 1 request in list
        TransactionDTO transactionDTO = getTransactionService(TokenType.forValue(tokenType)).assemble(tokenId,
                tokenType, txType, withdrawRequests);

        if (transactionDTO.isSuccess()) {

            // update withdraw request
            WithdrawRequestDTO withdrawRequestDTO = withdrawRequests.get(0);
            WithdrawRequest withdrawRequest = withdrawRequestDAO.getOne(withdrawRequestDTO.getId());

            withdrawRequest.setState(WithdrawStatus.PENDING.getValue());
            withdrawRequest.setTxid(transactionDTO.getTxid());
            withdrawRequest.setGas(transactionDTO.getFee());
            if (null == withdrawRequest.getAmount() || BigDecimal.ZERO.compareTo(withdrawRequest.getAmount()) == 0) {
                withdrawRequest.setAmount(transactionDTO.getAmount());
            }
            withdrawRequestDAO.saveAndFlush(withdrawRequest);

            // update withdraw flow
            WithdrawFlow withdrawFlow = generateWithdraw(tokenType, withdrawRequests);
            withdrawFlow.setRequestId(withdrawRequest.getRequestId());
            withdrawFlow.setTxid(transactionDTO.getTxid());
            withdrawFlow.setFee(transactionDTO.getFee());
            withdrawFlow.setSignedRawTx(transactionDTO.getSignedRawTx());

            // update withdraw detail
            List<WithdrawDetail> receivers = generateReceiverDetail(withdrawFlow, transactionDTO);
            if (!CollectionUtils.isEmpty(receivers)) {
                withdrawFlow.getDetails().addAll(receivers);
            }

            List<WithdrawDetail> senders = generateSenderDetail(withdrawFlow, transactionDTO);
            if (!CollectionUtils.isEmpty(senders)) {
                withdrawFlow.getDetails().addAll(senders);
            }

            WithdrawDetail fee = generateFeeDetail(withdrawFlow, transactionDTO);
            if (null != fee) {
                withdrawFlow.getDetails().add(fee);
            }

            withdrawFlowDAO.saveAndFlush(withdrawFlow);

            // TOOD: update local utxo
            addLocalUtxos(transactionDTO, receivers, txType);
        }

        return transactionDTO;
    }

    @Override
    protected TransactionDTO generateTransaction(WithdrawRequestDTO dto) {
        // for now, only contain 1 request in list
        TransactionDTO transactionDTO = getTransactionService(dto.getTokenType()).assemble(dto);

        if (transactionDTO.isSuccess()) {

            // update withdraw flow
            WithdrawFlow withdrawFlow = generateWithdraw(dto);

            withdrawFlow.setTxid(transactionDTO.getTxid());
            withdrawFlow.setFee(transactionDTO.getFee());
            withdrawFlow.setSignedRawTx(transactionDTO.getSignedRawTx());

            // update withdraw detail
            List<WithdrawDetail> receivers = generateReceiverDetail(withdrawFlow, transactionDTO);
            if (!CollectionUtils.isEmpty(receivers)) {
                withdrawFlow.getDetails().addAll(receivers);
            }

            List<WithdrawDetail> senders = generateSenderDetail(withdrawFlow, transactionDTO);
            if (!CollectionUtils.isEmpty(senders)) {
                withdrawFlow.getDetails().addAll(senders);
            }

            WithdrawDetail fee = generateFeeDetail(withdrawFlow, transactionDTO);
            withdrawFlow.getDetails().add(fee);

            withdrawFlowDAO.saveAndFlush(withdrawFlow);

            // TOOD: update local utxo
//            addLocalUtxos(transactionDTO, receivers, txType);
        }

        return transactionDTO;
    }

    private void addLocalUtxos(TransactionDTO transactionDTO, List<WithdrawDetail> receivers, Integer txType) {
        if (txType.equals(TxType.CHARGE.getValue())) {

            receivers.forEach(receiver -> {
                Utxo utxo = new Utxo();
                utxo.setTokenId(chainService.getAssetId());
                utxo.setIndex(receiver.getIndex());
                utxo.setTxid(transactionDTO.getTxid());
                utxo.setAmount(receiver.getAmount());
                log.info("amount of receiver {}", receiver.getAmount());
                utxo.setAddress(receiver.getAddress());
                utxo.setLocking(Boolean.FALSE);
                utxo.setSpendable(Boolean.TRUE);
                utxo.setSynced(Boolean.FALSE);

                utxoDAO.saveAndFlush(utxo);
            });
        }
    }

    private WithdrawFlow generateWithdraw(WithdrawRequestDTO dto) {

        WithdrawFlow withdrawFlow = new WithdrawFlow();
        withdrawFlow.setRequestId(dto.getRequestId());
        withdrawFlow.setTokenId(dto.getTokenId());
        withdrawFlow.setTokenType(dto.getTokenType().getValue());
        withdrawFlow.setAmount(dto.getAmount());
        withdrawFlow.setMemo(dto.getMemo());
        withdrawFlow.setFailedRetries(0);
        withdrawFlow.setType(dto.getTxType());
        withdrawFlow.setStatus(PENDING.getValue());
        return withdrawFlow;
    }

    private WithdrawFlow generateWithdraw(Integer tokenType, List<WithdrawRequestDTO> requests) {

        BigDecimal withdrawAmount = requests.stream().map(WithdrawRequestDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        WithdrawFlow withdrawFlow = new WithdrawFlow();

        if (requests.size() == 1) {
            WithdrawRequestDTO request = requests.get(0);
            withdrawFlow.setTokenId(request.getTokenId());
        } else {
            withdrawFlow.setTokenId(chainService.getAssetId());
        }
        withdrawFlow.setTokenType(tokenType);
        withdrawFlow.setAmount(withdrawAmount);
        withdrawFlow.setFailedRetries(0);
        withdrawFlow.setStatus(PENDING.getValue());

        return withdrawFlow;
    }

    private List<WithdrawDetail> generateReceiverDetail(WithdrawFlow withdrawFlow, TransactionDTO transactionDTO) {

        if (CollectionUtils.isEmpty(transactionDTO.getReceivers())) {
            return null;
        }

        return transactionDTO.getReceivers().stream().map(receiver -> {

            WithdrawDetail detail = new WithdrawDetail();
            detail.setAddress(receiver.getAddress());
            detail.setStatus(withdrawFlow.getStatus());
            detail.setWithdrawFlow(withdrawFlow);
            detail.setWithdrawRequestId(receiver.getRequestId());

            detail.setTokenId(withdrawFlow.getTokenId());
            detail.setType(withdrawFlow.getType().getValue());
            detail.setMemo(withdrawFlow.getMemo());

            detail.setAmount(receiver.getAmount());
            detail.setDirection(DIRECTION_RECEIVER);
            detail.setTxid(transactionDTO.getTxid());
            detail.setIndex(receiver.getIndex());

            detail.setUserId(receiver.getUserId());

            return detail;
        }).collect(Collectors.toList());
    }

    private List<WithdrawDetail> generateSenderDetail(WithdrawFlow withdrawFlow, TransactionDTO transactionDTO) {

        if (CollectionUtils.isEmpty(transactionDTO.getSenders())) {
            return null;
        }

        return transactionDTO.getSenders().stream().map(sender -> {

            WithdrawDetail detail = new WithdrawDetail();

            detail.setAddress(sender.getAddress());
            detail.setStatus(withdrawFlow.getStatus());
            detail.setWithdrawFlow(withdrawFlow);

            detail.setTokenId(withdrawFlow.getTokenId());
            detail.setType(withdrawFlow.getType().getValue());
            detail.setMemo(withdrawFlow.getMemo());

            detail.setAmount(sender.getAmount());
            detail.setDirection(DIRECTION_SENDER);

            detail.setTxid(sender.getTxid());
            detail.setIndex(sender.getIndex());
            detail.setUtxoId(sender.getUtxoId());

            return detail;
        }).collect(Collectors.toList());
    }

    private WithdrawDetail generateFeeDetail(WithdrawFlow withdrawFlow, TransactionDTO transactionDTO) {
        WithdrawDetail detail = new WithdrawDetail();

        detail.setAddress(transactionDTO.getSenderAddress());
        detail.setStatus(withdrawFlow.getStatus());
        detail.setWithdrawFlow(withdrawFlow);

        detail.setAmount(transactionDTO.getFee().negate());

        detail.setTokenId(withdrawFlow.getTokenId());
        detail.setType(FEE.getValue());
        detail.setMemo(withdrawFlow.getMemo());

        return detail;
    }

    @Override
    protected FlowDTO buildFlow(WithdrawFlow withdraw, TrackStatus status) {

        FlowDTO flow = new FlowDTO();
        flow.setRequestId(withdraw.getRequestId());
        flow.setToAddress(withdraw.getToAddress());
        flow.setTxid(withdraw.getTxid());
        flow.setIndex(null);
        flow.setAmount(withdraw.getAmount());

        flow.setTokenId(withdraw.getTokenId());
        flow.setUserId(withdraw.getUserId());
        // TODO: memo
        flow.setMemo(null);

        flow.setTxType(TxType.WITHDRAW);
        flow.setWithdrawStatus(WithdrawStatus.forValue(withdraw.getStatus()));
        flow.setTrackStatus(status);
        flow.setCommittedHeight(withdraw.getCommittedHeight());
        flow.setConfirmedHeight(withdraw.getConfirmedHeight());

        flow.setTrackStatus(status);

        flow.setErrorCode(flow.getErrorCode());
        flow.setErrorMsg(flow.getErrorMsg());
        return flow;
    }

    @Override
    protected TrackDTO buildTrack(FlowDTO flow) {

        TrackDTO track = new TrackDTO();
        track.setId(flow.getRequestId());
        track.setToAddress(flow.getToAddress());
        track.setFromAddress(flow.getFromAddresses());
        track.setTxid(flow.getTxid());
        track.setIndex(flow.getIndex());
        track.setAmount(flow.getAmount());

        track.setTokenId(flow.getTokenId());
        track.setUserId(flow.getUserId());
        track.setMemo(flow.getMemo());

        track.setTxType(flow.getTxType().getValue());
        track.setTxTypeStr(TxType.forValue(flow.getTxType().getValue()).getName());
        track.setWithdrawStatus(flow.getWithdrawStatus().getValue());
        track.setCommittedHeight(flow.getCommittedHeight());
        track.setConfirmedHeight(flow.getConfirmedHeight());
        track.setCurrentHeight(ManagerHub.getInstance().getLocalBestBlock().getHeight());

        List<String> hashSeed = new ArrayList<>();

        if (Objects.nonNull(flow.getTokenId())) {
            hashSeed.add(flow.getTokenId().toString());
        }

        if (Objects.nonNull(flow.getUserId())) {
            hashSeed.add(flow.getUserId().toString());
        }

        if (!Utils.isEmpty(flow.getTxid())) {
            hashSeed.add(flow.getTxid());
        }

        if (Objects.nonNull(flow.getIndex())) {
            hashSeed.add(flow.getIndex().toString());
        }

        hashSeed.add(flow.getWithdrawStatus().getName());

        String trackHash = DigestUtils.sha256Hex(String.join("-", hashSeed));
        track.setTrackHash(trackHash);

        return track;
    }

    @Override
    protected void withdrawDone(FlowDTO flow) {
        super.withdrawDone(flow);

        if (null != flow) {

        }
    }
}