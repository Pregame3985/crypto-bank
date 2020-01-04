package com.mcb.address.manager.core;

import com.mcb.address.manager.model.HdWalletDTO;

import java.util.List;

/**
 * @author william
 */
public interface WalletEngine {

    boolean isSupportEngine(Integer realTokenId);

    HdWalletDTO generateAddress(SeedRule seedRule, int index, List<String> hotWalletAddresses);

    boolean isValid(String address);

    interface InternalWalletEngine {

        Integer getTokenId();

        boolean isValidAddress(String address);
    }
}
