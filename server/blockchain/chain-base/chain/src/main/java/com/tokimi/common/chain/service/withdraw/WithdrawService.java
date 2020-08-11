package com.tokimi.common.chain.service.withdraw;

import com.tokimi.common.chain.model.WithdrawRequestDTO;
import com.tokimi.common.chain.service.ConfirmationService;

/**
 * @author william
 */
public interface WithdrawService extends ConfirmationService {

    void request(WithdrawRequestDTO withdrawRequestDTO);

    void audit();

    void withdraw();

    boolean failRequest(Long requestId);
}
