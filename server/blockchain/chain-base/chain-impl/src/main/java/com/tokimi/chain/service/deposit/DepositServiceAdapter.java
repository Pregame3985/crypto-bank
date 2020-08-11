package com.tokimi.chain.service.deposit;

import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import com.tokimi.chain.dao.DepositFlowDAO;
import com.tokimi.chain.dao.TrackDAO;
import com.tokimi.chain.entity.DepositFlow;
import com.tokimi.chain.entity.Track;
import com.tokimi.common.ChainManagerException;
import com.tokimi.common.Utils;
import com.tokimi.common.chain.model.FlowDTO;
import com.tokimi.common.chain.model.TrackDTO;
import com.tokimi.common.chain.service.AssetService;
import com.tokimi.common.chain.service.ChainService;
import com.tokimi.common.chain.service.deposit.DepositService;
import com.tokimi.common.chain.service.tx.TransactionService;
import com.tokimi.common.chain.utils.DepositStatus;
import com.tokimi.common.chain.utils.TokenType;
import com.tokimi.common.chain.utils.TrackStatus;
import com.tokimi.common.chain.utils.TxStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.util.List;

import static com.tokimi.common.chain.utils.TrackStatus.VERIFY;

/**
 * @author william
 */
@Slf4j
public abstract class DepositServiceAdapter implements DepositService {

    @Resource
    protected DepositFlowDAO depositFlowDAO;

    @Resource
    protected ChainService chainService;
//
//    @Resource
//    protected MessagePublisher depositTrackPublisher;

//    @Resource
//    protected MessageService rabbitmqMessageService;

    @Resource
    protected AssetService assetService;

    @Resource
    private TrackDAO trackDAO;

    // @Resource
    // private List<TransactionService> transactionServices;

    @Override
    @Transactional
    public void verify() {

        List<Long> tokenIds = assetService.getMyAssetIds();

        PageRequest page = PageRequest.of(0, 20);

        PredicateBuilder<DepositFlow> pendingPredicateBuilder = Specifications.<DepositFlow>and()
                .eq("status", DepositStatus.PENDING.getValue())
                .in(!Utils.isEmpty(tokenIds), "tokenId", tokenIds);

        Page<DepositFlow> pendingData = depositFlowDAO.findAll(pendingPredicateBuilder.build(), page);

        // change status to verifying
        Flux.fromIterable(pendingData.getContent()).map(depositFlow -> {
            depositFlow.setStatus(DepositStatus.VERIFIED.getValue());
            depositFlowDAO.saveAndFlush(depositFlow);

            return buildFlow(depositFlow, VERIFY);
        }).subscribe(this::done);

        // check status verifying
        PredicateBuilder<DepositFlow> verifyingPredicateBuilder = Specifications.<DepositFlow>and()
                .eq("status", DepositStatus.VERIFIED.getValue())
                .in(!Utils.isEmpty(tokenIds), "tokenId", tokenIds);

        Page<DepositFlow> verifyingData = depositFlowDAO.findAll(verifyingPredicateBuilder.build(), page);

        FlowDTO emptyFlow = new FlowDTO();

        Flux.fromIterable(verifyingData.getContent())
                .map(depositFlow -> getTransactionService(getAllowingTokenType()).get(depositFlow.getTxid())).cache()
                .zipWith(Flux.fromIterable(verifyingData.getContent()), (chainTx, depositFlow) -> {

                    String txid = depositFlow.getTxid();

                    if (!chainTx.isSuccess()) {
                        log.error("txid error {}, message {}", txid, chainTx.getError().getMessage());
                        depositFlow.setStatus(chainTx.getError().getCode().getValue());
                        depositFlowDAO.saveAndFlush(depositFlow);
                        throw new ChainManagerException(chainTx.getError().getCode(), chainTx.getError().getMessage(),
                                chainTx.getError().getMessage(), buildFlow(depositFlow, TrackStatus.FAILED));
                    }

                    if (!chainTx.isConfirm()) {
                        log.info("tx {} not confirmed", txid);
                        return emptyFlow;
                    }

                    Integer confirmations = chainTx.getConfirmations().intValue();
                    if (confirmations < chainService.getDepositConfirmations()) {
                        return emptyFlow;
                    }

                    if (!chainTx.isValid()) {
                        log.info("tx {} invalid, message {}", txid, "Invalid, missing or duplicate parameter");
                        depositFlow.setStatus(chainTx.getError().getCode().getValue());
                        depositFlowDAO.saveAndFlush(depositFlow);
                        throw new ChainManagerException(chainTx.getError().getCode(), chainTx.getError().getMessage(),
                                chainTx.getError().getMessage(), buildFlow(depositFlow, TrackStatus.FAILED));
                    }

                    depositFlow.setStatus(DepositStatus.SUCCESS.getValue());
                    depositFlowDAO.saveAndFlush(depositFlow);

                    return buildFlow(depositFlow, TrackStatus.SUCCESS);

                }).subscribe(flow -> {
            done(flow);

            afterDone(flow);
        }, error -> {
            if (error instanceof ChainManagerException) {
                failed((ChainManagerException) error);
            } else {
                log.error("err msg {}", error.getMessage());
            }
        });
    }

    protected abstract TokenType getAllowingTokenType();

    protected abstract FlowDTO buildFlow(DepositFlow deposit, TrackStatus status);

    protected abstract TrackDTO buildTrack(FlowDTO flow);

    protected abstract void afterDone(FlowDTO flow);

    protected abstract TransactionService getTransactionService(TokenType tokenType);
    // protected TransactionService getTransactionService() {

    // Optional<TransactionService> anyTxService = transactionServices.stream()
    // .filter(txService ->
    // txService.getTransactionType().equals(getAllowingTokenType())).findAny();

    // if (!anyTxService.isPresent()) {
    // log.error("transaction service not found");
    // throw new ChainManagerException(ErrorConstants.TX_SERVICE_NOT_FOUND,
    // "transaction service not found");
    // }

    // return anyTxService.get();
    // }

    protected void done(FlowDTO flow) {

        if (null != flow && !Utils.isEmpty(flow.getTxid())) {

            TrackDTO track = buildTrack(flow);

            send(track);
        }
    }

    private void failed(ChainManagerException error) {

        FlowDTO flow = (FlowDTO) error.getAttached();

        if (null != flow) {
            TrackDTO track = buildTrack(flow);

            track.setErrorCode(error.getError().getName());
            track.setErrorMsg(error.getReason());

            send(track);
        }
    }

    private void send(TrackDTO track) {

        log.info("track dto {}", track.toString());

        if (null != track.getUserId() && track.getUserId() > 0) {

//            rabbitmqMessageService.send(depositTrackPublisher, track);

            if (track.getDepositStatus().equals(DepositStatus.SUCCESS.getValue())) {
                Track entity = new Track();
                BeanUtils.copyProperties(track, entity, "id");
                entity.setStatus(TxStatus.SUCCESSFUL.getValue());
                entity.setResult("notOk");
                trackDAO.save(entity);
            }
        }
    }
}