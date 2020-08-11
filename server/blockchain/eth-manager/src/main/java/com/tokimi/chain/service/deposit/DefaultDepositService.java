package com.tokimi.chain.service.deposit;

import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import com.tokimi.chain.dao.PendingTxDAO;
import com.tokimi.chain.entity.DepositFlow;
import com.tokimi.chain.entity.PendingTx;
import com.tokimi.common.ChainManagerException;
import com.tokimi.common.ErrorConstants;
import com.tokimi.common.Utils;
import com.tokimi.common.chain.model.FlowDTO;
import com.tokimi.common.chain.model.ReceiverDTO;
import com.tokimi.common.chain.model.SenderDTO;
import com.tokimi.common.chain.model.TrackDTO;
import com.tokimi.common.chain.service.sweep.SweepService;
import com.tokimi.common.chain.service.tx.TransactionService;
import com.tokimi.common.chain.service.wallet.WalletService;
import com.tokimi.common.chain.utils.DepositStatus;
import com.tokimi.common.chain.utils.TokenType;
import com.tokimi.common.chain.utils.TrackStatus;
import com.tokimi.common.chain.utils.TxType;
import com.tokimi.config.ManagerHub;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DefaultDepositService extends DepositServiceAdapter {

    @Resource
    private PendingTxDAO pendingTxDAO;

    @Resource
    private WalletService walletService;

    @Resource
    private SweepService tokenSweepService;

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
    @Transactional
    public void deposit() {

        List<Long> assetIds = assetService.getMyAssetIds();

        PageRequest page = PageRequest.of(0, 20);

        PredicateBuilder<PendingTx> predicateBuilder = Specifications.<PendingTx>and()
                .eq("processed", false)
                .in(!Utils.isEmpty(assetIds), "tokenId", assetIds);

        Page<PendingTx> dbTxData = pendingTxDAO.findAll(predicateBuilder.build(), page);

        FlowDTO emptyFlow = new FlowDTO();

        Flux.fromIterable(dbTxData.getContent()).filter(dbTx -> null != dbTx.getTokenId())
                .map(dbTx -> getTransactionService(TokenType.forValue(dbTx.getTokenType())).get(dbTx.getTxid())).cache()
                .filter(Objects::nonNull)
                .zipWith(Flux.fromIterable(dbTxData.getContent()), (chainTx, dbTx) -> {

                    if (null == chainTx) {
                        // TODO: maybe tx not exist
                        return emptyFlow;
                    }

                    if (!chainTx.isValid() && !chainTx.isSuccess()) {
                        return emptyFlow;
                    }

                    String senderAddresses = chainTx.getSenders().stream().map(SenderDTO::getAddress)
                            .collect(Collectors.joining("|"));
                    ReceiverDTO receiver = chainTx.getReceivers().get(dbTx.getIndex());

                    if (dbTx.getAmount().compareTo(receiver.getAmount()) != 0
                            || !dbTx.getAddress().equals(receiver.getAddress())) {
                        log.info("not find valid receiver, [addr:{}, index:{}, amount:{}]", dbTx.getAddress(),
                                dbTx.getIndex(), dbTx.getAmount());
                        return emptyFlow;
                    }

                    DepositFlow depositFlow = new DepositFlow();
                    depositFlow.setTokenType(dbTx.getTokenType());
                    depositFlow.setTokenId(dbTx.getTokenId());
                    depositFlow.setTxid(dbTx.getTxid());
                    depositFlow.setIndex(dbTx.getIndex());
                    depositFlow.setHeight(dbTx.getHeight());
                    depositFlow.setFromAddress(senderAddresses);
                    depositFlow.setToAddress(receiver.getAddress());
                    depositFlow.setAmount(receiver.getAmount());
                    depositFlow.setUserId(dbTx.getUserId());
                    depositFlow.setStatus(DepositStatus.PENDING.getValue());
                    depositFlowDAO.saveAndFlush(depositFlow);

                    dbTx.setProcessed(true);
                    pendingTxDAO.saveAndFlush(dbTx);

                    FlowDTO flow = buildFlow(depositFlow, TrackStatus.RECEIVE);
                    flow.setFromAddresses(
                            chainTx.getSenders().stream().map(SenderDTO::getAddress).collect(Collectors.toList()));

                    return flow;
                }).subscribe(this::done);
    }

    @Override
    protected TokenType getAllowingTokenType() {
        return TokenType.NATIVE;
    }

    @Override
    protected FlowDTO buildFlow(DepositFlow deposit, TrackStatus status) {

        FlowDTO flow = new FlowDTO();
        flow.setId(deposit.getId());
        flow.setToAddress(deposit.getToAddress());
        flow.setTxid(deposit.getTxid());
        flow.setIndex(deposit.getIndex());
        flow.setAmount(deposit.getAmount());

        flow.setTokenId(deposit.getTokenId());
        flow.setUserId(deposit.getUserId());
        // TODO: memo
        flow.setMemo(null);

        flow.setTxType(TxType.DEPOSIT);
        flow.setDepositStatus(DepositStatus.forValue(deposit.getStatus()));
        flow.setTrackStatus(status);
        flow.setConfirmedHeight(deposit.getHeight());

        flow.setErrorCode(flow.getErrorCode());
        flow.setErrorMsg(flow.getErrorMsg());
        return flow;
    }

    @Override
    protected TrackDTO buildTrack(FlowDTO flow) {

        TrackDTO track = new TrackDTO();
        track.setId(flow.getId());
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
        track.setDepositStatus(flow.getDepositStatus().getValue());
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

        hashSeed.add(flow.getDepositStatus().getName());

        String trackHash = DigestUtils.sha256Hex(String.join("-", hashSeed));
        track.setTrackHash(trackHash);

        return track;
    }

    @Override
    protected void afterDone(FlowDTO flow) {

        // TODO: below logic should be in caller

        if (null != flow && !Utils.isEmpty(flow.getTxid())) {
            // TODO: to be refactor
            walletService.add(flow.getTokenId(), flow.getUserId(), flow.getToAddress(), flow.getAmount());

            // TODO: to be refactor, logic for omni
            if (flow.getAmount().compareTo(BigDecimal.valueOf(500L)) >= 1 && flow.getUserId() > 0) {
                tokenSweepService.sweepByAddress(flow.getToAddress());
            }
        }
    }
}
