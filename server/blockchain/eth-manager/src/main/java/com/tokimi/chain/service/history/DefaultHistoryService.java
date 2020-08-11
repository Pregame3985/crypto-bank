package com.tokimi.chain.service.history;

import com.tokimi.chain.dao.BlockSyncStatusDAO;
import com.tokimi.chain.dao.FundHistoryDAO;
import com.tokimi.chain.entity.FundHistory;
import com.tokimi.common.Utils;
import com.tokimi.common.chain.model.AssetDTO;
import com.tokimi.common.chain.model.TrackDTO;
import com.tokimi.common.chain.service.AssetService;
import com.tokimi.common.chain.utils.DepositStatus;
import com.tokimi.common.chain.utils.TxStatus;
import com.tokimi.common.chain.utils.TxType;
import com.tokimi.common.chain.utils.WithdrawStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author william
 */
@Slf4j
@Service
public class DefaultHistoryService implements HistoryService {

    @Resource
    private FundHistoryDAO fundHistoryDAO;

    @Resource
    private AssetService assetService;

    @Resource
    private BlockSyncStatusDAO blockSyncStatusDAO;

    @Override
    @Transactional
    public void log(TrackDTO trackDTO) {

        AssetDTO assetDTO = assetService.getManagerAsset();

        FundHistory probe = new FundHistory();

        Optional<FundHistory> any = Optional.empty();
        if (null != trackDTO.getId()) {
            probe.setId(trackDTO.getId());
            any = fundHistoryDAO.findOne(Example.of(probe));
        }

        if (!any.isPresent()) {
            probe = new FundHistory();
            probe.setHash(trackDTO.getTrackHash());
            any = fundHistoryDAO.findOne(Example.of(probe));
        }

        if (!any.isPresent()) {
            probe = new FundHistory();
            probe.setType(trackDTO.getTxType());
            probe.setUserId(trackDTO.getUserId());
            probe.setTokenId(trackDTO.getTokenId());
            probe.setTxid(trackDTO.getTxid());
            if (null != trackDTO.getIndex()) {
                probe.setIndex(trackDTO.getIndex());
            }
            any = fundHistoryDAO.findOne(Example.of(probe));
        }

        FundHistory fundHistory;
        if (any.isPresent()) {
            fundHistory = any.get();
        } else {
            fundHistory = new FundHistory();

            fundHistory.setToAddress(trackDTO.getToAddress());
            fundHistory.setTxid(trackDTO.getTxid());
            fundHistory.setIndex(trackDTO.getIndex());
            fundHistory.setAmount(trackDTO.getAmount());
            fundHistory.setFee(BigDecimal.ZERO);

            fundHistory.setTokenId(trackDTO.getTokenId());
            fundHistory.setTokenName(assetDTO.getShortName());
            fundHistory.setUserId(trackDTO.getUserId());
            fundHistory.setMemo(trackDTO.getMemo());

            fundHistory.setType(trackDTO.getTxType());
            fundHistory.setTypeStr(TxType.forValue(trackDTO.getTxType()).getName());

            if (!Utils.isEmpty(trackDTO.getFromAddress())) {
                fundHistory.setFromAddress(String.join("|", trackDTO.getFromAddress()));
            }

            fundHistory.setToAddress(trackDTO.getToAddress());
            fundHistory.setHash(trackDTO.getTrackHash());
        }

        fundHistory.setConfirmations(trackDTO.getCurrentHeight() - (null != trackDTO.getCommittedHeight() ? trackDTO.getCommittedHeight() : trackDTO.getConfirmedHeight()) + 1);
        fundHistory.setConfirmedHeight(trackDTO.getConfirmedHeight());

        if (!Utils.isEmpty(assetDTO.getTxBaseURL())) {
            fundHistory.setUrlForTx(String.join("/", assetDTO.getTxBaseURL(), trackDTO.getTxid()));
        }

        if (!Utils.isEmpty(assetDTO.getAddressBaseURL())) {
            fundHistory.setUrlForAddress(String.join("/", assetDTO.getAddressBaseURL(), trackDTO.getToAddress()));
        }

        if (TxType.DEPOSIT.getValue().equals(fundHistory.getType())) {

            fundHistory.setDueConfirmations(assetDTO.getDepositConfirmations());

            if (trackDTO.getDepositStatus().equals(DepositStatus.SUCCESS.getValue())) {
                fundHistory.setStatus(TxStatus.SUCCESSFUL.getValue());
            } else if (trackDTO.getDepositStatus().equals(DepositStatus.FAILED.getValue())) {
                fundHistory.setStatus(TxStatus.FAILED.getValue());
            } else {
                fundHistory.setStatus(TxStatus.PENDING.getValue());
            }
        }

        if (TxType.WITHDRAW.getValue().equals(fundHistory.getType())) {

            fundHistory.setDueConfirmations(assetDTO.getWithdrawConfirmations());

            if (trackDTO.getWithdrawStatus().equals(WithdrawStatus.SUCCESS.getValue())) {
                fundHistory.setStatus(TxStatus.SUCCESSFUL.getValue());
            } else if (trackDTO.getWithdrawStatus().equals(WithdrawStatus.FAILED.getValue())) {
                fundHistory.setStatus(TxStatus.FAILED.getValue());
            } else {
                fundHistory.setStatus(TxStatus.PENDING.getValue());
            }

            fundHistory.setCommittedHeight(trackDTO.getCommittedHeight());
        }

        fundHistory.setStatusStr(TxStatus.forValue(fundHistory.getStatus()).getName());

        fundHistory.setFailedReason(trackDTO.getErrorMsg());
        fundHistory.setFailedCode(trackDTO.getErrorCode());

        log.debug("fund history {}", fundHistory);

        fundHistoryDAO.saveAndFlush(fundHistory);
    }
}