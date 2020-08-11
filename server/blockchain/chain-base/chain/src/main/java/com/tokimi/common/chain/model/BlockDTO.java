package com.tokimi.common.chain.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tokimi.common.Utils;

import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Setter
@Getter
public class BlockDTO {

    private String hash;

    private String nextblockhash;

    private String previousblockhash;

    private Long confirmations;

    private Long height;

    private List<String> txids;

    private List<ChainTransaction<? extends RawTx>> txs;

    private LocalDateTime time;

    private Integer txCount;

    private Integer parseSeconds;

    @JsonIgnore
    public boolean hasNextBlock() {
        return !Utils.isEmpty(nextblockhash);
    }
}