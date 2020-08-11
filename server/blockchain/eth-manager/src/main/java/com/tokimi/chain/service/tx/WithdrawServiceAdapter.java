package com.tokimi.chain.service.tx;

import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import com.tokimi.address.manager.core.WalletEngine;
import com.tokimi.chain.dao.DepositFlowDAO;
import com.tokimi.chain.dao.ExchangeWalletDAO;
import com.tokimi.chain.dao.SweepFlowDAO;
import com.tokimi.chain.dao.WithdrawFlowDAO;
import com.tokimi.chain.dao.WithdrawRequestDAO;
import com.tokimi.chain.entity.DepositFlow;
import com.tokimi.chain.entity.SweepFlow;
import com.tokimi.chain.entity.WithdrawFlow;
import com.tokimi.chain.entity.WithdrawRequest;
import com.tokimi.common.ChainManagerException;
import com.tokimi.common.Utils;
import com.tokimi.common.chain.model.FlowDTO;
import com.tokimi.common.chain.model.TrackDTO;
import com.tokimi.common.chain.model.TransactionDTO;
import com.tokimi.common.chain.model.WithdrawRequestDTO;
import com.tokimi.common.chain.service.AssetService;
import com.tokimi.common.chain.service.ChainService;
import com.tokimi.common.chain.service.tx.TransactionService;
import com.tokimi.common.chain.service.wallet.WalletService;
import com.tokimi.common.chain.service.withdraw.WithdrawService;
import com.tokimi.common.chain.utils.RequestStatus;
import com.tokimi.common.chain.utils.SweepStatus;
import com.tokimi.common.chain.utils.TokenType;
import com.tokimi.common.chain.utils.TrackStatus;
import com.tokimi.common.chain.utils.TxType;
import com.tokimi.common.chain.utils.WithdrawStatus;
import com.tokimi.config.ManagerHub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author william
 */
@Slf4j
public abstract class WithdrawServiceAdapter implements WithdrawService {

    protected final static int DIRECTION_SENDER = 0;
    protected final static int DIRECTION_RECEIVER = 1;

    private final static PageRequest DEFAULT_DESC_PAGE = PageRequest.of(0, 50,
            Sort.by("createdAt").descending());
    protected final static PageRequest DEFAULT_ASC_PAGE = PageRequest.of(0, 50,
            Sort.by("createdAt").ascending());

    @Value("${app.chain.id}")
    private Long assetId;

    @Resource
    private WalletEngine walletEngine;

    @Resource
    protected ChainService chainService;

    @Resource
    protected WithdrawRequestDAO withdrawRequestDAO;

    @Resource
    protected DepositFlowDAO depositFlowDAO;

    @Resource
    protected WithdrawFlowDAO withdrawFlowDAO;

//    @Resource
//    private MessageService rabbitmqMessageService;

    @Resource
    protected List<WalletService> walletServiceList;

//    @Resource
//    private MessagePublisher withdrawTrackPublisher;

    @Resource
    protected SweepFlowDAO sweepFlowDAO;

    @Resource
    protected ExchangeWalletDAO exchangeWalletDAO;

    @Resource
    private AssetService assetService;

    @Resource
    protected TransactionService nativeTransactionService;

    protected TransactionService getTransactionService() {
        return nativeTransactionService;
    }

    @Override
    @Transactional
    public void request(WithdrawRequestDTO withdrawRequestDTO) {

        if (!isAllowingToken(withdrawRequestDTO.getTokenId())) {
            return;
        }

        log.info("request message: {}", withdrawRequestDTO);

        WithdrawRequest probe = new WithdrawRequest();

        if (TxType.SWEEP.getValue().equals(withdrawRequestDTO.getType())
                || TxType.CHARGE.getValue().equals(withdrawRequestDTO.getType())) {
            probe.setType(withdrawRequestDTO.getType());
            probe.setMemo(withdrawRequestDTO.getMemo());
            probe.setTokenId(withdrawRequestDTO.getTokenId());
        } else {
            probe.setType(withdrawRequestDTO.getType());
            probe.setRequestId(withdrawRequestDTO.getId());
        }

        List<WithdrawRequest> data = withdrawRequestDAO.findAll(Example.of(probe));

        if (!CollectionUtils.isEmpty(data)) {
            log.error("request already exist, {}", withdrawRequestDTO);
            return;
        }

        WithdrawRequest request = new WithdrawRequest();

        request.setRequestId(withdrawRequestDTO.getId());
        request.setUserId(withdrawRequestDTO.getUserId());
        request.setToAddress(withdrawRequestDTO.getToAddress());
        request.setFromAddress(withdrawRequestDTO.getFromAddress());
        request.setCommitted(false);
        request.setTokenId(withdrawRequestDTO.getTokenId());
        request.setType(withdrawRequestDTO.getType());
        request.setMemo(withdrawRequestDTO.getMemo());
        request.setState(WithdrawStatus.REVIEWING.getValue());
        request.setStatus(RequestStatus.STATUS_PENDING.getValue());
        request.setAmount(withdrawRequestDTO.getAmount());

        withdrawRequestDAO.save(request);
    }

    public void newAudit() {
        List<Long> tokenIds = assetService.getMyAssetIds();

        PredicateBuilder<WithdrawRequest> predicateBuilder = Specifications.<WithdrawRequest>and()
                .eq("status", RequestStatus.STATUS_PENDING.getValue())
                .eq("state", WithdrawStatus.PENDING.getValue())
                .eq("committed", false)
                .in(!Utils.isEmpty(tokenIds), "tokenId", tokenIds);

        Page<WithdrawRequestDTO> data = withdrawRequestDAO.findAll(predicateBuilder.build(), DEFAULT_ASC_PAGE)
                .map(this::toDTO);

        if (!data.hasContent()) {
            return;
        }

        for (WithdrawRequestDTO dto : data.getContent()) {

            if (!walletEngine.isValid(dto.getToAddress())) {
                // TODO:
                dto.setState(WithdrawStatus.FAILED.getValue());
                dto.setStatus(RequestStatus.STATUS_REJECT.getValue());
                dto.setReason("invalid to address");
//                failedRequests.add(dto);
            } else {
                handleTodoRequest(dto);
            }
        }
    }

    @Override
    @Transactional
    public void audit() {

        // Refactor
        newAudit();

//        List<Long> tokenIds = assetService.getMyAssetIds();
//
//        PredicateBuilder<WithdrawRequest> predicateBuilder = Specifications.<WithdrawRequest>and()
//                .eq("status", RequestStatus.STATUS_PENDING.getValue())
//                .eq("state", WithdrawStatus.PENDING.getValue())
//                .eq("committed", false)
//                .in(!Utils.isEmpty(tokenIds), "tokenId", tokenIds);
//
//        Page<WithdrawRequest> data = withdrawRequestDAO.findAll(predicateBuilder.build(), DEFAULT_ASC_PAGE);
//
//        if (!data.hasContent()) {
//            return;
//        }
//
//        List<WithdrawRequest> withdrawRequests = data.getContent();
//
//        List<WithdrawRequest> failedRequests = new ArrayList<>();
//        Map<Long, List<WithdrawRequest>> groupedTodoRequests = new HashMap<>();
//        List<WithdrawRequest> sweepRequests = new ArrayList<>();
//        List<WithdrawRequest> chargeRequests = new ArrayList<>();
//        List<WithdrawRequest> retrieveRequests = new ArrayList<>();
//        List<WithdrawRequest> mergeRequests = new ArrayList<>();
//
//        withdrawRequests.forEach(request -> {
//
//            Long userId = request.getUserId();
//
//            if (!walletEngine.isValid(request.getToAddress())) {
//
//                request.setState(WithdrawStatus.FAILED.getValue());
//                request.setStatus(RequestStatus.STATUS_REJECT.getValue());
//                request.setReason("invalid to address");
//                failedRequests.add(request);
//            } else {
//                if (null != userId) {
//
//                    if (request.getType().equals(TxType.SWEEP.getValue())) {
//                        sweepRequests.add(request);
//                    } else if (request.getType().equals(TxType.CHARGE.getValue())) {
//                        chargeRequests.add(request);
//                    } else if (request.getType().equals(TxType.REFUND.getValue())) {
//                        retrieveRequests.add(request);
//                    } else if (request.getType().equals(TxType.WITHDRAW.getValue())) {
//                        Long tokenId = request.getTokenId();
//
//                        if (!groupedTodoRequests.containsKey(tokenId)) {
//                            groupedTodoRequests.put(tokenId, new ArrayList<>());
//                        }
//
//                        List<WithdrawRequest> todoRequests = groupedTodoRequests.get(tokenId);
//
//                        todoRequests.add(request);
//                    } else if (request.getType().equals(TxType.MERGE.getValue())) {
//                        mergeRequests.add(request);
//                    }
//                }
//            }
//        });
//
//        if (failedRequests.size() > 0) {
//            handleFailedRequests(failedRequests);
//        }
//
//        if (!CollectionUtils.isEmpty(sweepRequests)) {
//            sweepRequests.forEach(request -> handleTodoRequests(request.getTokenId(),
//                    request.getTokenId().equals(assetId) ? TokenType.NATIVE.getValue() : TokenType.TOKEN.getValue(),
//                    TxType.SWEEP.getValue(), Lists.newArrayList(request)));
//        }
//
//        if (!CollectionUtils.isEmpty(chargeRequests)) {
//            chargeRequests.forEach(request -> handleTodoRequests(request.getTokenId(),
//                    request.getTokenId().equals(assetId) ? TokenType.NATIVE.getValue() : TokenType.TOKEN.getValue(),
//                    TxType.CHARGE.getValue(), Lists.newArrayList(request)));
//        }
//
//        if (!CollectionUtils.isEmpty(retrieveRequests)) {
//            retrieveRequests.forEach(request -> handleTodoRequests(request.getTokenId(),
//                    request.getTokenId().equals(assetId) ? TokenType.NATIVE.getValue() : TokenType.TOKEN.getValue(),
//                    TxType.REFUND.getValue(), Lists.newArrayList(request)));
//        }
//
//        if (!CollectionUtils.isEmpty(groupedTodoRequests)) {
//            groupedTodoRequests.keySet().forEach(tokenId -> {
//                if (tokenId.equals(chainService.getAssetId())) {
//                    groupedTodoRequests.get(tokenId).forEach(request -> handleTodoRequests(tokenId,
//                            request.getTokenId().equals(assetId) ? TokenType.NATIVE.getValue() : TokenType.TOKEN.getValue(),
//                            TxType.WITHDRAW.getValue(), Lists.newArrayList(request)));
//                }
//            });
//        }
//
//        if (!CollectionUtils.isEmpty(mergeRequests)) {
//            mergeRequests.forEach(request -> handleTodoRequests(request.getTokenId(), TokenType.NATIVE.getValue(),
//                    TxType.MERGE.getValue(), Lists.newArrayList(request)));
//        }
//
//        withdrawRequestDAO.flush();
    }

    private WithdrawRequestDTO toDTO(WithdrawRequest entity) {

        WithdrawRequestDTO dto = new WithdrawRequestDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setRequestId(entity.getRequestId());
        dto.setTokenId(entity.getTokenId());
        dto.setTxType(TxType.forValue(entity.getType()));
        dto.setTokenType(entity.getTokenId().equals(assetId) ? TokenType.NATIVE : TokenType.TOKEN);
        dto.setAmount(entity.getAmount());
        dto.setFromAddress(entity.getFromAddress());
        dto.setToAddress(entity.getToAddress());
        dto.setMemo(Objects.nonNull(entity.getMemo()) ? entity.getMemo() : dto.getTxType().getName());
        dto.setState(entity.getState());
        dto.setStatus(entity.getStatus());

        return dto;
    }

    private void handleTodoRequest(WithdrawRequestDTO dto) {

        TransactionDTO transactionDTO = generateTransaction(dto);

        WithdrawRequest request = withdrawRequestDAO.getOne(dto.getId());

        if (transactionDTO.isSuccess()) {
            request.setState(WithdrawStatus.PENDING.getValue());
            request.setStatus(RequestStatus.STATUS_APPROVE.getValue());
            request.setTxid(transactionDTO.getTxid());
            request.setGas(transactionDTO.getFee());
            request.setCommitted(true);
        } else {
            request.setReason(transactionDTO.getError().getMessage());
            // TODO:
            switch (transactionDTO.getError().getCode()) {
                case RPC_NODE_NOT_PREPARED:
                case UTXO_NOT_ENOUGH:
                case BALANCE_NOT_ENOUGH:
                case FEE_BALANCE_NOT_ENOUGH:
                case FEE_RATE_NOT_ENOUGH:
                case FEE_RATE_NOT_READY:
                    request.setState(WithdrawStatus.PENDING.getValue());
                    request.setStatus(RequestStatus.STATUS_PENDING.getValue());
                    request.setReason(transactionDTO.getError().getMessage());
                    request.setCommitted(false);
                    break;
                default:
                    log.info("unexpected error code {}, error msg {}", transactionDTO.getError().getCode().getValue(),
                            transactionDTO.getError().getMessage());
                    request.setState(WithdrawStatus.FAILED.getValue());
                    request.setStatus(RequestStatus.STATUS_REJECT.getValue());
                    request.setReason(transactionDTO.getError().getMessage());
                    request.setCommitted(true);
                    break;
            }
        }

        withdrawRequestDAO.save(request);
    }

    private void handleTodoRequests(Long tokenId, Integer tokenType, Integer txType,
                                    List<WithdrawRequest> todoRequests) {

        List<WithdrawRequestDTO> withdrawRequests = todoRequests.stream().map(withdrawRequest -> {
            WithdrawRequestDTO withdrawRequestDTO = new WithdrawRequestDTO();
            withdrawRequestDTO.setId(withdrawRequest.getId());
            withdrawRequestDTO.setUserId(withdrawRequest.getUserId());
            withdrawRequestDTO.setRequestId(withdrawRequest.getRequestId());
            withdrawRequestDTO.setTokenId(withdrawRequest.getTokenId());
            withdrawRequestDTO.setType(withdrawRequest.getType());
            withdrawRequestDTO.setAmount(withdrawRequest.getAmount());
            withdrawRequestDTO.setMemo(withdrawRequest.getMemo());
            withdrawRequestDTO.setToAddress(withdrawRequest.getToAddress());
            withdrawRequestDTO.setFromAddress(withdrawRequest.getFromAddress());
            withdrawRequestDTO.setCommitted(withdrawRequest.getCommitted());
            withdrawRequestDTO.setStatus(withdrawRequest.getStatus());
            withdrawRequestDTO.setReason(withdrawRequest.getReason());
            withdrawRequestDTO.setTxid(withdrawRequest.getTxid());
            // withdrawRequestDTO.setConfirmed(withdrawRequest.getConfirmed());
            withdrawRequestDTO.setHeight(withdrawRequest.getHeight());
            withdrawRequestDTO.setState(withdrawRequest.getState());
            withdrawRequestDTO.setMineFee(withdrawRequest.getGas());
            return withdrawRequestDTO;
        }).collect(Collectors.toList());

        TransactionDTO transactionDTO = generateTransaction(tokenId, tokenType, txType, withdrawRequests);

        if (transactionDTO.isSuccess()) {
            todoRequests.forEach(request -> {
                request.setState(WithdrawStatus.PENDING.getValue());
                request.setStatus(RequestStatus.STATUS_APPROVE.getValue());
                request.setCommitted(true);
            });
        } else {
            String requestIds = todoRequests.stream().map(r -> String.valueOf(r.getId()))
                    .collect(Collectors.joining("|"));
//            log.info("error code {}, error msg {}, request ids {}", transactionDTO.getError().getError().getValue(),
//                    transactionDTO.getError().getMessage(), requestIds);

            todoRequests.forEach(request -> {
                request.setReason(transactionDTO.getError().getMessage());
                // TODO:
//                switch (transactionDTO.getError().getError()) {
//                    case RPC_NODE_NOT_PREPARED:
//                    case UTXO_NOT_ENOUGH:
//                    case BALANCE_NOT_ENOUGH:
//                    case FEE_BALANCE_NOT_ENOUGH:
//                    case FEE_RATE_NOT_ENOUGH:
//                    case FEE_RATE_NOT_READY:
//                        request.setState(WithdrawStatus.PENDING.getValue());
//                        request.setStatus(RequestStatus.STATUS_PENDING.getValue());
//                        request.setReason(transactionDTO.getError().getMessage());
//                        request.setCommitted(false);
//                        break;
//                    default:
//                        log.info("unexpected error code {}, error msg {}", transactionDTO.getError().getError().getValue(),
//                                transactionDTO.getError().getMessage());
//                        request.setState(WithdrawStatus.FAILED.getValue());
//                        request.setStatus(RequestStatus.STATUS_REJECT.getValue());
//                        request.setReason(transactionDTO.getError().getMessage());
//                        request.setCommitted(true);
//                        break;
//                }
            });
        }

        withdrawRequestDAO.saveAll(todoRequests);
    }

    private void handleFailedRequests(List<WithdrawRequest> failedRequests) {

        failedRequests.forEach(request -> {
            if (request.getState() > 100 && TxType.WITHDRAW.getValue().equals(request.getType())) {
                TrackDTO track = new TrackDTO();
                track.setAmount(request.getAmount());
                track.setToAddress(request.getToAddress());
                track.setTokenId(request.getTokenId());
                track.setTxType(request.getType());
                track.setTxTypeStr(TxType.forValue(request.getType()).getName());
                track.setId(request.getRequestId());
                track.setStatus(TrackStatus.FAILED.getValue());
                track.setStatusStr(TrackStatus.FAILED.getName());
                track.setErrorMsg(request.getReason());
//                rabbitmqMessageService.send(withdrawTrackPublisher, track);
            }

            log.info("failed request, error msg {}", request.getReason());

            request.setStatus(RequestStatus.STATUS_REJECT.getValue());
            request.setCommitted(true);
        });

        withdrawRequestDAO.saveAll(failedRequests);
    }

    @Override
    @Transactional
    public void withdraw() {

        PageRequest page = PageRequest.of(0, 20);
        List<Long> tokenIds = assetService.getMyAssetIds();
        Page<WithdrawFlow> pendingData = withdrawFlowDAO.findAllByStatusAndTokenIdIn(WithdrawStatus.PENDING.getValue(),
                tokenIds, page);

        Flux.fromIterable(pendingData.getContent()).map(withdrawFlow -> {

            String txid = withdrawFlow.getTxid();

            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setTxid(txid);
            transactionDTO.setSignedRawTx(withdrawFlow.getSignedRawTx());

            getTransactionService().send(transactionDTO);

            // get request id first
            WithdrawRequest probe = new WithdrawRequest();
            probe.setTxid(txid);
            Optional<WithdrawRequest> any = withdrawRequestDAO.findOne(Example.of(probe));
            WithdrawRequest withdrawRequest = null;

            if (any.isPresent()) {

                withdrawRequest = any.get();
                withdrawFlow.setRequestId(withdrawRequest.getRequestId());
                withdrawFlow.setMemo(withdrawRequest.getMemo());
                withdrawFlow.setType(TxType.forValue(withdrawRequest.getType()));
                withdrawFlow.setToAddress(withdrawRequest.getToAddress());
                withdrawFlow.setUserId(withdrawRequest.getUserId());
            }

            if (transactionDTO.isSuccess()) {

                withdrawFlow.setCommittedHeight(chainService.getBlockHeight());
                withdrawFlow.setStatus(WithdrawStatus.SENT.getValue());
                withdrawFlow.setStatusStr(WithdrawStatus.SENT.getName());
                withdrawFlowDAO.saveAndFlush(withdrawFlow);

                if (null != withdrawRequest) {
                    withdrawRequest.setState(WithdrawStatus.SENT.getValue());
                    withdrawRequest.setHeight(withdrawFlow.getCommittedHeight());
                    withdrawRequestDAO.saveAndFlush(withdrawRequest);
                }

                return buildFlow(withdrawFlow, TrackStatus.SENT);
            } else {

                String reason = transactionDTO.getError().getMessage();

                withdrawFlow.setStatus(WithdrawStatus.FAILED.getValue());
                withdrawFlow.setStatusStr(WithdrawStatus.FAILED.getName());
                withdrawFlow.setFailedReason(reason);
                withdrawFlowDAO.saveAndFlush(withdrawFlow);

                if (null != withdrawRequest) {
                    withdrawRequest.setState(WithdrawStatus.FAILED.getValue());
                    withdrawRequest.setReason(reason);
                    withdrawRequestDAO.saveAndFlush(withdrawRequest);
                }

                throw new RuntimeException();
                // TODO:
//                throw new ChainManagerException(transactionDTO.getError().getError(),
//                        transactionDTO.getError().getMessage(), transactionDTO.getError().getDesc(),
//                        buildFlow(withdrawFlow, TrackStatus.FAILED));
            }
        }).subscribe(this::withdrawDone, error -> {
            if (error instanceof ChainManagerException) {
                failed((ChainManagerException) error);

                FlowDTO flow = (FlowDTO) ((ChainManagerException) error).getAttached();
                // TODO: sweep notify
                sweepDone(flow, SweepStatus.FAILED);
            } else {
                log.error("err msg {}", error.getMessage());
            }
        });
    }

    @Override
    @Transactional
    public void verify() {

        PageRequest page = PageRequest.of(0, 20);
        List<Long> tokenIds = assetService.getMyAssetIds();
        Page<WithdrawFlow> pendingData = withdrawFlowDAO.findAllByStatusAndTokenIdIn(WithdrawStatus.SENT.getValue(),
                tokenIds, page);

        // change status to verifying
        Flux.fromIterable(pendingData.getContent()).map(withdrawFlow -> {

            withdrawFlow.setStatus(WithdrawStatus.VERIFIED.getValue());
            withdrawFlow.setStatusStr(WithdrawStatus.VERIFIED.getName());
            withdrawFlowDAO.saveAndFlush(withdrawFlow);

            String txid = withdrawFlow.getTxid();

            // get request id first
            WithdrawRequest probe = new WithdrawRequest();
            probe.setTxid(txid);
            Optional<WithdrawRequest> any = withdrawRequestDAO.findOne(Example.of(probe));
            WithdrawRequest withdrawRequest = null;
            if (any.isPresent()) {
                withdrawRequest = any.get();
                withdrawRequest.setState(WithdrawStatus.VERIFIED.getValue());
                withdrawRequestDAO.saveAndFlush(withdrawRequest);
            }

            return buildFlow(withdrawFlow, TrackStatus.VERIFY);
        }).subscribe(this::withdrawDone);

        // check status verifying
        Page<WithdrawFlow> verifyingData = withdrawFlowDAO
                .findAllByStatusAndTokenIdIn(WithdrawStatus.VERIFIED.getValue(), tokenIds, page);

        FlowDTO emptyFlow = new FlowDTO();

        Flux.fromIterable(verifyingData.getContent())
                .filter(withdrawFlow -> chainService.getBlockHeight()
                        - withdrawFlow.getCommittedHeight() >= chainService.getWithdrawConfirmations()) // to save I/O
                // which
                // confirmations
                // not reach
                // threshold
                .map(withdrawFlow -> {

                    String txid = withdrawFlow.getTxid();

                    TransactionDTO chainTx = getTransactionService().get(txid);

                    // get request id first
                    WithdrawRequest probe = new WithdrawRequest();
                    probe.setTxid(txid);
                    Optional<WithdrawRequest> any = withdrawRequestDAO.findOne(Example.of(probe));
                    WithdrawRequest withdrawRequest = null;
                    if (any.isPresent()) {
                        withdrawRequest = any.get();
                        withdrawFlow.setMemo(withdrawRequest.getMemo());
                        withdrawFlow.setRequestId(withdrawRequest.getRequestId());
                        withdrawFlow.setType(TxType.forValue(withdrawRequest.getType()));
                        withdrawFlow.setToAddress(withdrawRequest.getToAddress());
                        withdrawFlow.setUserId(withdrawRequest.getUserId());
                    }

                    if (!chainTx.isSuccess()) {
                        log.error("txid error {}, message {}", txid, chainTx.getError().getMessage());

                        withdrawFlow.setStatus(WithdrawStatus.FAILED.getValue());
                        withdrawFlow.setStatusStr(WithdrawStatus.FAILED.getName());
                        withdrawFlow.setFailedReason(chainTx.getError().getMessage());
                        withdrawFlowDAO.saveAndFlush(withdrawFlow);

                        if (null != withdrawRequest) {
                            withdrawRequest.setState(WithdrawStatus.FAILED.getValue());
                            withdrawRequest.setReason(chainTx.getError().getMessage());
                            withdrawRequestDAO.saveAndFlush(withdrawRequest);
                        }
// TODO:
//                        throw new ChainManagerException(chainTx.getError().getError(), chainTx.getError().getMessage(),
//                                chainTx.getError().getDesc(), buildFlow(withdrawFlow, TrackStatus.FAILED));
                        throw new RuntimeException();
                    }

                    if (!chainTx.isConfirm()) {
                        log.info("tx {} not confirmed", txid);
                        return emptyFlow;
                    }

                    Integer confirmations = chainTx.getConfirmations().intValue();
                    if (confirmations < chainService.getWithdrawConfirmations()) {
                        return emptyFlow;
                    }

                    if (!chainTx.isValid()) {
                        log.info("tx {} invalid, message {}", txid, "Invalid, missing or duplicate parameter");

                        withdrawFlow.setStatus(WithdrawStatus.FAILED.getValue());
                        withdrawFlow.setStatusStr(WithdrawStatus.FAILED.getName());
                        withdrawFlow.setFailedReason(chainTx.getError().getMessage());
                        withdrawFlowDAO.saveAndFlush(withdrawFlow);

                        if (null != withdrawRequest) {
                            withdrawRequest.setState(WithdrawStatus.FAILED.getValue());
                            withdrawRequest.setReason(chainTx.getError().getMessage());
                            withdrawRequestDAO.saveAndFlush(withdrawRequest);
                        }
// TODO:
//                        throw new ChainManagerException(chainTx.getError().getError(), chainTx.getError().getMessage(),
//                                chainTx.getError().getDesc(), buildFlow(withdrawFlow, TrackStatus.FAILED));
                        throw new RuntimeException();
                    }

                    int compare = chainTx.getConfirmations()
                            .compareTo(Long.valueOf(ManagerHub.getInstance().getAssetDTO().getWithdrawConfirmations()));

                    if (compare < 0) {
                        return emptyFlow;
                    }

                    withdrawFlow.setConfirmedHeight(chainTx.getBlockheight());
                    withdrawFlow.setStatus(WithdrawStatus.SUCCESS.getValue());
                    withdrawFlow.setStatusStr(WithdrawStatus.SUCCESS.getName());
                    withdrawFlowDAO.saveAndFlush(withdrawFlow);

                    if (null != withdrawRequest) {
                        withdrawRequest.setState(WithdrawStatus.SUCCESS.getValue());
                        withdrawRequest.setHeight(chainTx.getBlockheight());
                        withdrawRequestDAO.saveAndFlush(withdrawRequest);
                    }

                    return buildFlow(withdrawFlow, TrackStatus.SUCCESS);

                }).subscribe(flow -> {
            withdrawDone(flow);

            // TODO: sync local balance
            for (WalletService walletService : walletServiceList) {
                walletService.updateBalance(flow.getTokenId());
            }

            // TODO: sweep notify
            sweepDone(flow, SweepStatus.SUCCESS);

        }, error -> {
            if (error instanceof ChainManagerException) {
                failed((ChainManagerException) error);

                FlowDTO flow = (FlowDTO) ((ChainManagerException) error).getAttached();
                // TODO: sweep notify
                sweepDone(flow, SweepStatus.FAILED);
            } else {
                log.error("err msg {}", error.getMessage());
            }
        });
    }

    @Override
    @Transactional
    public boolean failRequest(Long requestId) {
        WithdrawRequest withdrawRequest = withdrawRequestDAO.getOne(requestId);

        withdrawRequest.setCommitted(true);
        withdrawRequest.setStatus(RequestStatus.STATUS_FAILED.getValue());
        withdrawRequest.setReason("[manual fail]" + withdrawRequest.getReason());

        WithdrawRequest result = withdrawRequestDAO.save(withdrawRequest);

        if (TxType.WITHDRAW.getValue().equals(withdrawRequest.getType())) {
            TrackDTO track = new TrackDTO();
            track.setAmount(withdrawRequest.getAmount());
            track.setToAddress(withdrawRequest.getToAddress());
            track.setTokenId(withdrawRequest.getTokenId());
            track.setTxType(withdrawRequest.getType());
            track.setTxTypeStr(TxType.forValue(withdrawRequest.getType()).getName());
            track.setId(requestId);
            track.setStatus(TrackStatus.FAILED.getValue());
            track.setStatusStr(TrackStatus.FAILED.getName());
            track.setErrorCode("MANUAL FAILED");
            track.setErrorMsg("[manually failed]");

//            rabbitmqMessageService.send(withdrawTrackPublisher, track);
        }

        return result.getStatus().equals(RequestStatus.STATUS_FAILED.getValue());
    }

    protected abstract boolean isAllowingToken(Long tokenId);

    protected abstract TransactionDTO generateTransaction(Long tokenId, Integer tokenType, Integer txType,
                                                          List<WithdrawRequestDTO> withdrawRequest);

    protected abstract TransactionDTO generateTransaction(WithdrawRequestDTO dto);

    protected abstract FlowDTO buildFlow(WithdrawFlow withdraw, TrackStatus status);

    protected abstract TrackDTO buildTrack(FlowDTO flow);

    protected void withdrawDone(FlowDTO flow) {

        if (null != flow && TxType.WITHDRAW.equals(flow.getTxType()) && null != flow.getRequestId()
                && !Utils.isEmpty(flow.getTxid())) {

            TrackDTO track = buildTrack(flow);

            send(track);
        }
    }

    private void sweepDone(FlowDTO flow, SweepStatus sweepStatus) {

        if (null != flow && (TxType.SWEEP.equals(flow.getTxType()) || TxType.CHARGE.equals(flow.getTxType()))
                && !Utils.isEmpty(flow.getMemo())) {

            String memo = flow.getMemo();
            if (!Utils.isEmpty(memo)) {
                SweepFlow sfProbe = new SweepFlow();
                sfProbe.setMemo(memo);
                Optional<SweepFlow> sweepFlowOptional = sweepFlowDAO.findOne(Example.of(sfProbe));

                if (sweepFlowOptional.isPresent()) {
                    sweepFlowOptional.get().setStatus(sweepStatus.getValue());
                    sweepFlowOptional.get().setStatusStr(sweepStatus.getName());
                    sweepFlowDAO.saveAndFlush(sweepFlowOptional.get());

                    DepositFlow probe = new DepositFlow();
                    probe.setUserId(flow.getUserId());
                    List<DepositFlow> flows = depositFlowDAO.findAll(Example.of(probe));

                    if (!CollectionUtils.isEmpty(flows)) {
                        flows.forEach(depositFlow -> {
                            depositFlow.setSwept(Boolean.TRUE);
                            depositFlow.setSweptAt(LocalDateTime.now());
                        });
                        depositFlowDAO.saveAll(flows);
                    }
                }
            }
        }
    }

    private void failed(ChainManagerException error) {

        FlowDTO flow = (FlowDTO) error.getAttached();

        if (null != flow && null != flow.getRequestId()) {
            TrackDTO track = buildTrack(flow);

            track.setErrorCode(error.getError().getName());
            track.setErrorMsg(error.getReason());

            send(track);
        }
    }

    private void send(TrackDTO track) {

        log.info("track dto {}", track.toString());

        if (null != track.getUserId() && track.getUserId() > 0) {

//            rabbitmqMessageService.send(withdrawTrackPublisher, track);
        }
    }
}