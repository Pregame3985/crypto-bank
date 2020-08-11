package com.tokimi.chain.rpc.model.eth.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Block {

    private String number;

    private String hash;

    @JsonProperty("parentHash")
    private String previousblockhash;

    private String nonce;

    private String size;

    private String gasLimit;

    private String gasUsed;

    private String timestamp;

    private List<String> txids;

    @JsonProperty("transactions")
    private List<Transaction> tx;

}