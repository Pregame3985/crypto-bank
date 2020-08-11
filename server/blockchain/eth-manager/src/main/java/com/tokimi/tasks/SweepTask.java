package com.tokimi.tasks;

import com.tokimi.common.chain.service.sweep.SweepService;
import com.tokimi.config.AppFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author william
 */
@Slf4j
@Component
public class SweepTask {

    @Resource
    private AppFunction appFunction;

    @Resource
    private List<SweepService> sweepServices;

    @Scheduled(cron = "${app.tasks.sweep.schedule.cron:0 0 1 * * *}")
    public void task() {

        if (!appFunction.isSweep()) {
            return;
        }

        try {
            sweepServices.forEach(SweepService::sweepAll);
        } catch (Exception e) {
            log.error("error running sync task", e);
            throw e;
        }
    }
}
