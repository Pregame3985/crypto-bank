package com.tokimi.tasks;

import com.tokimi.common.chain.service.withdraw.WithdrawService;
import com.tokimi.config.AppFunction;
import com.tokimi.config.ManagerHub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author william
 */
@Slf4j
@Component
public class WithdrawTask {

    @Resource
    private AppFunction appFunction;

    @Resource
    private WithdrawService withdrawService;

    @Scheduled(
            initialDelayString = "${app.tasks.withdrawAudit.schedule.initialDelay:100}",
            fixedDelayString = "${app.tasks.withdrawAudit.schedule.fixedDelay:5000}"
    )
    public void audit() {
        if (ManagerHub.getInstance().isWithdrawReady()) {
            withdrawService.audit();
        }
    }

    @Scheduled(
            initialDelayString = "${app.tasks.withdraw.schedule.initialDelay:100}",
            fixedDelayString = "${app.tasks.withdraw.schedule.fixedDelay:300000}"
    )
    public void withdraw() {

        if (!appFunction.isWithdraw()) {
            return;
        }

        if (ManagerHub.getInstance().isWithdrawReady()) {
            withdrawService.withdraw();
        }
    }

    @Scheduled(
            initialDelayString = "${app.tasks.withdrawVerify.schedule.initialDelay:100}",
            fixedDelayString = "${app.tasks.withdrawVerify.schedule.fixedDelay:10000}"
    )
    public void verify() {
        if (ManagerHub.getInstance().isWithdrawReady()) {
            withdrawService.verify();
        }
    }
}
