package com.tokimi.common.chain.service.sweep;

import java.math.BigDecimal;

import javax.annotation.Resource;

import com.tokimi.chain.service.sweep.SweepServiceAdapter;
import com.tokimi.common.chain.service.wallet.UtxoWalletService;

/**
 * @author william
 */
public abstract class UtxoSweepServiceAdapter extends SweepServiceAdapter {

    @Resource
    private UtxoWalletService nativeWalletService;

    @Override
    protected BigDecimal threshold(Integer times) {

        int estimateTxSize = 100 + 1 * 150;
        BigDecimal singleTxFee = nativeWalletService.estimateFee(estimateTxSize);

        return singleTxFee.multiply(new BigDecimal(times));
    }
}