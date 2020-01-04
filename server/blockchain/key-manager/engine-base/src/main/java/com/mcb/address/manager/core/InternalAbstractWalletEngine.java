package com.mcb.address.manager.core;

import com.mcb.address.manager.util.AddressUtils;
import com.mcb.address.manager.util.Slip44CoinType;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.MainNetParams;

import java.util.List;

/**
 * @author william
 */
@Slf4j
public abstract class InternalAbstractWalletEngine implements WalletEngine.InternalWalletEngine {

    @Override
    public boolean isValidAddress(String address) {
        return false;
    }

    protected boolean isValidBase58(String address) {
        return AddressUtils.isValidAddress(address);
    }

    protected List<String> myRegExs() {
        return Lists.newArrayList();
    }

    protected NetworkParameters getNetworkParameters() {
        return MainNetParams.get();
    }

    protected String customGenerate(DeterministicKey childDk) {
        return childDk.toAddress(getNetworkParameters()).toBase58();
    }

    protected abstract Slip44CoinType slip44CoinType();

    protected boolean isCompressed() {
        return true;
    }

    protected String prefix() {
        return null;
    }
}