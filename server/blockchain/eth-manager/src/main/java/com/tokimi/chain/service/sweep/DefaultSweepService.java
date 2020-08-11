package com.tokimi.chain.service.sweep;

import com.tokimi.chain.entity.Address;
import com.tokimi.chain.entity.SweepFlow;
import com.tokimi.chain.entity.WithdrawRequest;
import com.tokimi.common.chain.model.AddressBalanceDTO;
import com.tokimi.common.chain.model.AddressGuardDTO;
import com.tokimi.common.chain.service.wallet.WalletService;
import com.tokimi.common.chain.utils.RequestStatus;
import com.tokimi.common.chain.utils.WithdrawStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.tokimi.common.chain.utils.Constants.MIN_SWEEP_AMOUNTS;
import static com.tokimi.common.chain.utils.Constants.MIN_SWEEP_TIMES;
import static com.tokimi.common.chain.utils.SweepStatus.PENDING;
import static com.tokimi.common.chain.utils.TxType.SWEEP;

/**
 * @author william
 */
@Slf4j
@Service
public class DefaultSweepService extends SweepServiceAdapter {

    @Value("${app.chain.id}")
    private Long assetId;

    @Value("${app.chain.gasId}")
    private Long gasAssetId;

    @Resource
    private WalletService defaultWalletService;

    // @Resource
    // private MessagePublisher withdrawRequestPublisher;

//    @Resource
//    private MessageService rabbitmqMessageService;

    @Override
    protected List<AddressBalanceDTO> toAddressBalances(Long tokenId, List<Address> addressGuards) {

        return addressGuards.stream()
                .filter(distinctByKey(Address::getAddress))
                .map(addressGuard -> {

                    String address = addressGuard.getAddress();
                    BigDecimal balance = defaultWalletService.balance(address, tokenId);

                    if (balance.compareTo(BigDecimal.ZERO) < 1) {
                        return null;
                    }

                    AddressBalanceDTO addressBalance = new AddressBalanceDTO();

                    addressBalance.setAddress(address);
                    addressBalance.setTokenId(tokenId);
                    addressBalance.setBalance(balance.stripTrailingZeros().toPlainString());
                    addressBalance.setUserId(addressGuard.getUserId());

                    return addressBalance;

                }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public void sweepByTokenId(Long tokenId) {

    }

    protected BigDecimal worthToSweep(Long tokenId, BigDecimal balance) {

//        SweepConfig sweepConfig = getSweepConfig(addressGuardDTO.getTokenId().longValue());

        // min sweep amount
//        return balance.compareTo(sweepConfig.getMinSweepAmount()) >= 0;

        BigDecimal minGas = BigDecimal.valueOf(0.002);

        if (tokenId.equals(gasAssetId)) {
            // TOOD: gas min reserve
            return balance.subtract(minGas.multiply(BigDecimal.valueOf(MIN_SWEEP_TIMES)));
        } else {
            if (balance.compareTo(BigDecimal.valueOf(MIN_SWEEP_AMOUNTS)) >= 0) {
                return balance;
            }
        }

        return BigDecimal.ZERO;
    }

    protected void _sweep(List<String> hotWalletAddresses, AddressGuardDTO addressGuardDTO) {

        Long tokenId = addressGuardDTO.getTokenId();
        String sweptAddress = addressGuardDTO.getAddress();
        Long userId = addressGuardDTO.getUserId();

        BigDecimal balance = defaultWalletService.balance(sweptAddress, tokenId);

        log.info("user id {}, address {}, token {}, balance {}", userId, sweptAddress, tokenId, balance.toPlainString());

        BigDecimal sweepAmount = worthToSweep(tokenId, balance);
        if (sweepAmount.compareTo(BigDecimal.ZERO) <= 0) {

            log.info("not worth to sweep, balance is : {}", balance.toPlainString());
            return;
        }

        SweepFlow probe = new SweepFlow();
        probe.setUserId(userId);
        probe.setTokenId(tokenId);
        probe.setType(SWEEP.getValue());
        probe.setStatus(PENDING.getValue());
        long count = sweepFlowDAO.count(Example.of(probe));

        if (count > 0) {
            log.info("sweep is processing");
            return;
        }

        WithdrawRequest withdrawRequest = new WithdrawRequest();

        String memo = UUID.randomUUID().toString();
        withdrawRequest.setType(SWEEP.getValue());
        withdrawRequest.setUserId(userId);
        withdrawRequest.setTokenId(tokenId);
        withdrawRequest.setToAddress(hotWalletAddresses.get(0));
        withdrawRequest.setFromAddress(sweptAddress);
        withdrawRequest.setMemo(memo);
        withdrawRequest.setAmount(sweepAmount);
        withdrawRequest.setStatus(RequestStatus.STATUS_PENDING.getValue());
        withdrawRequest.setState(WithdrawStatus.PENDING.getValue());
        withdrawRequest.setCommitted(false);
        withdrawRequestDAO.saveAndFlush(withdrawRequest);

        SweepFlow sweepFlow = new SweepFlow();
        sweepFlow.setAddress(sweptAddress);
        sweepFlow.setUserId(userId);
        sweepFlow.setAmount(sweepAmount);
        sweepFlow.setTokenId(tokenId);
        sweepFlow.setType(SWEEP.getValue());
        sweepFlow.setStatus(PENDING.getValue());
        sweepFlow.setStatusStr(PENDING.getName());
        sweepFlow.setGasTokenId(Objects.isNull(gasAssetId) ? assetId : gasAssetId);
        sweepFlow.setMemo(memo);
        sweepFlowDAO.saveAndFlush(sweepFlow);
    }

    @Override
    protected WalletService getWalletService() {
        return defaultWalletService;
    }

    @Override
    public boolean isSupport(Long tokenId) {
        return defaultWalletService.isSupport(tokenId);
    }

    @Override
    protected BigDecimal threshold(Integer times) {
        return BigDecimal.ONE.multiply(BigDecimal.valueOf(times.longValue()));
    }
}
