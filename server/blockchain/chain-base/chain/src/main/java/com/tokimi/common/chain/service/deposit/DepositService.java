package com.tokimi.common.chain.service.deposit;

import com.tokimi.common.chain.service.ConfirmationService;

/**
 * @author william
 */
public interface DepositService extends ConfirmationService {

    void deposit();
}
