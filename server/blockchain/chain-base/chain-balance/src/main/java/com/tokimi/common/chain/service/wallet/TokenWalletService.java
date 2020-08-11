package com.tokimi.common.chain.service.wallet;

import java.math.BigDecimal;

/**
 * @author william
 */
public interface TokenWalletService extends WalletService {

    Long decimal(Long tokenId);

    Long decimal(String contract);

    String getContract(Long tokenId);

    BigDecimal precision(Long tokenId);

    BigDecimal precision(String contract);
}
