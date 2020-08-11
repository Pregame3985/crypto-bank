package com.tokimi.common.chain.service.wallet;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import com.tokimi.common.chain.service.tokenize.TokenizeService;
import com.tokimi.common.chain.model.AddressGuardDTO;

/**
 * @author william
 */
public interface WalletService extends TokenizeService {

    void listAvailable(Long tokenId);

    BigDecimal balance(String address, Long tokenId);

    boolean lock(TimeUnit timeUnit);

    boolean unlock(byte[] passphrase);

    void setInsufficientLimit(Long tokenId, BigDecimal amount);

    void setWithdrawLimit(Long tokenId, BigDecimal amount);

    BigDecimal getInsufficientLimit(Long tokenId);

    BigDecimal getWithdrawLimit(Long tokenId);

    void updateBalance(Long tokenId);

    byte[] generateR(String address);

    byte[] generateU(String address);

    int scale(Long tokenId);

    BigDecimal precision(Long tokenId);

    /******** Local balance ********/
    BigDecimal add(Long tokenId, Long userId, String address, BigDecimal amount);

    BigDecimal sync(Long tokenId, Long userId, String address);

    void syncAll(Long tokenId);

    void importOne(AddressGuardDTO addressGuardDTO);

    void importAll();
}
