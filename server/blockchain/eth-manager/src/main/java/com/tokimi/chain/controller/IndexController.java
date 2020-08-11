package com.tokimi.chain.controller;

import com.tokimi.chain.dao.DepositFlowDAO;
import com.tokimi.common.GenericResponse;
import com.tokimi.common.chain.service.ChainService;
import com.tokimi.common.chain.service.sync.BlockSyncService;
import com.tokimi.common.chain.service.sync.IndexService;
import com.tokimi.common.chain.service.wallet.WalletService;
import com.tokimi.config.ManagerHub;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author william
 */
@Slf4j
@RestController
@RequestMapping(value = "/index")
public class IndexController extends GenericHandler<Void> {

    @Setter
    @Resource
    private IndexService indexService;

    @Setter
    @Resource
    private BlockSyncService blockSyncService;

    @Setter
    @Resource
    private DepositFlowDAO depositFlowDAO;

    @Resource
    private WalletService walletService;

    @Setter
    @Resource
    private ChainService chainService;

    @GetMapping("/addresses")
    public GenericResponse<List<String>> addresses() throws Exception {
        return ControllerTemplate.call(response -> {

            response.setData(new ArrayList<>(ManagerHub.getInstance().getAddress().keySet()));
            response.setSuccess(true);
        });
    }

    @GetMapping("/update/address")
    public GenericResponse<Boolean> updateAddress() throws Exception {
        return ControllerTemplate.call(response -> {

            walletService.importAll();

            response.setData(true);
            response.setSuccess(true);
        });
    }

    //    @GetMapping("/resync")
//    public GenericResponse<Void> resync() throws Exception {
//        return ControllerTemplate.call(response -> {
//
//            depositFlowDAO.findAll().forEach(depositFlow -> {
//                if (!Utils.isEmpty(depositFlow.getTxid())) {
//                    log.info("sync tx: {}", depositFlow.getTxid());
//                    blockSyncService.resyncTx(depositFlow.getTxid());
//                }
//            });
//            response.setSuccess(true);
//        });
//    }
    @GetMapping("/resync/native/tx/{txid}")
    public GenericResponse<Boolean> resyncNativeTx(@PathVariable("txid") String txid) throws Exception {
        return ControllerTemplate.call(response -> {

            blockSyncService.resyncNativeTx(txid);
            response.setData(Boolean.TRUE);
            response.setSuccess(true);
        });
    }

    @GetMapping("/resync/token/tx/{txid}")
    public GenericResponse<Boolean> resyncTokenTx(@PathVariable("txid") String txid) throws Exception {
        return ControllerTemplate.call(response -> {

            blockSyncService.resyncTokenTx(txid);
            response.setData(Boolean.TRUE);
            response.setSuccess(true);
        });
    }

    @GetMapping("/verify")
    public GenericResponse<Void> verify() throws Exception {
        return ControllerTemplate.call(response -> {

            indexService.verify();

            response.setSuccess(true);
        });
    }
}
