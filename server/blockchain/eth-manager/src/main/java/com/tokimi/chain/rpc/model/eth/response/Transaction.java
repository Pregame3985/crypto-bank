package com.tokimi.chain.rpc.model.eth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tokimi.common.chain.model.RawTx;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author william
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction extends RawTx {

    private String blockNumber;

    @JsonProperty("blockHash")
    private String blockhash;

    @JsonProperty("hash")
    private String txid;

    private Long blockHeight;

    private String from;

    private String gas;

    private String gasPrice;

    private String input;

    private String nonce;

    private String to;

    @JsonProperty("transactionIndex")
    private String indexInBlock;

    private String value;

    private String v;

    private String r;

    private String s;

    private String cumulativeGasUsed;

    private String gasUsed;

    private String contractAddress;

    private List<Map> logs;

    private String logsBloom;

    private String root;

    private String status;

    @Override
    public boolean isValid() {
        return null == contractAddress || (null != status && status.equals("0x1"));
    }

    @Override
    public String getMessage() {
        return null;
    }
}
