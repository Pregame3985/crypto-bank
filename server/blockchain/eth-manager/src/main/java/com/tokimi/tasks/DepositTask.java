package com.tokimi.tasks;

import com.tokimi.common.chain.service.deposit.DepositService;
import com.tokimi.config.AppFunction;
import com.tokimi.config.ManagerHub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class DepositTask {

    @Resource
    private AppFunction appFunction;

    @Resource
    private DepositService depositService;

    @Scheduled(
            initialDelayString = "${app.tasks.deposit.schedule.initialDelay}",
            fixedDelayString = "${app.tasks.deposit.schedule.fixedDelay}"
    )
    public void deposit() {

        if (!appFunction.isDeposit()) {
            return;
        }

        try {
            if (ManagerHub.getInstance().isDepositReady()) {
                depositService.deposit();
            }
        } catch (Exception e) {
            log.error("error running deposit task", e);
            throw e;
        }
    }

    @Scheduled(
            initialDelayString = "${app.tasks.depositVerify.schedule.initialDelay}",
            fixedDelayString = "${app.tasks.depositVerify.schedule.fixedDelay}"
    )
    public void verify() {
        if (ManagerHub.getInstance().isDepositReady()) {
            depositService.verify();
        }
    }
}