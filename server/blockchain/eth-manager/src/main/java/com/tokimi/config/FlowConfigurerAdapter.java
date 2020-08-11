package com.tokimi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokimi.chain.service.history.HistoryService;
import com.tokimi.common.chain.model.AddressGuardDTO;
import com.tokimi.common.chain.model.TrackDTO;
import com.tokimi.common.chain.model.WithdrawRequestDTO;
import com.tokimi.common.chain.service.wallet.WalletService;
import com.tokimi.common.chain.service.withdraw.WithdrawService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import javax.annotation.Resource;

/**
 * @author william
 */
@Slf4j
public abstract class FlowConfigurerAdapter {

    @Resource
    protected ObjectMapper objectMapper;

    @Resource
    private WithdrawService withdrawService;

    @Resource
    private WalletService defaultWalletService;

    @Resource
    private HistoryService historyService;

    @RabbitListener(queues = "#{withdrawQueue.name}")
    public void withdraw(byte[] raw) {
        WithdrawRequestDTO withdrawRequestDTO;
        try {
            withdrawRequestDTO = objectMapper.readValue(raw, WithdrawRequestDTO.class);
            withdrawService.request(withdrawRequestDTO);
        } catch (Exception e) {
            log.error("withdraw request exception: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "#{addAddressQueue.name}")
    public void addAddress(String raw) {
        AddressGuardDTO addressGuardDTO;
        try {
            addressGuardDTO = objectMapper.readValue(raw, AddressGuardDTO.class);
            if (null != addressGuardDTO.getTokenId() && null != addressGuardDTO.getUserId()
                    && null != addressGuardDTO.getAddress()) {

                log.info("prepare to import address guard token id : {}, user id : {}, address : {}",
                        addressGuardDTO.getTokenId(), addressGuardDTO.getUserId(), addressGuardDTO.getAddress());

                defaultWalletService.importOne(addressGuardDTO);
            }
        } catch (Exception e) {
            log.error("address guard exception: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "#{chainTrackQueue.name}")
    public void logHistory(byte[] raw) {
        TrackDTO trackDTO;

        try {
            trackDTO = objectMapper.readValue(raw, TrackDTO.class);

            historyService.log(trackDTO);
        } catch (Exception e) {
            log.error("key import exception: {}", e.getMessage());
        }
    }

}
