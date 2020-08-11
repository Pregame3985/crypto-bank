package com.tokimi.chain.service.sequence;

import com.tokimi.chain.dao.SequenceDAO;
import com.tokimi.chain.rpc.model.eth.request.GetTransactionCountRequest;
import com.tokimi.chain.rpc.model.eth.response.GetTransactionCountResponse;
import com.tokimi.common.chain.service.ChainService;
import com.tokimi.common.chain.service.sequence.SequenceServiceAdapter;
import com.tokimi.common.chain.utils.BinaryUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author william
 */
@Service
public class DefaultSequenceService extends SequenceServiceAdapter {

    @Resource
    private SequenceDAO sequenceDAO;

    @Resource
    private ChainService chainService;

    @Override
    public Long sync(String address) {

        GetTransactionCountResponse response = getTransactionCount(address);

        if (response.isSuccess()) {
            return BinaryUtils.hexToLong(response.getResult());
        }

        return -1L;
    }

    private GetTransactionCountResponse getTransactionCount(String address) {

        GetTransactionCountRequest request = new GetTransactionCountRequest(address);
        return chainService.getJsonRpcAgent().sendToNetwork(request);
    }
}