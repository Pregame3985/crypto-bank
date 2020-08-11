package com.tokimi.common.chain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tokimi.common.chain.utils.TokenType;
import com.tokimi.common.ErrorDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author william
 */
@Getter
@Setter
@ToString
public class TransactionDTO {

    private ErrorDTO error;

    private Long tokenId;

    private String unsignedRawTx;

    private String signedRawTx;

    private String txid;

    private BigDecimal fee;

    private BigDecimal amount;

    private List<SenderDTO> senders;

    private List<ReceiverDTO> receivers;

    private List<ReceiverDTO> mergedReceivers;

    private List<SenderDTO> utxoSenders;

    private List<ReceiverDTO> utxoReceivers;

    private List<WitnessDTO> witnesses;

    private Long confirmations;

    private Long blockheight;

    private String blockhash;

    private String typeString;

    private boolean valid;

    // property id or contract address
    private String propertyId;

    private int type;

    private int version;

    private String senderAddress;

    // TODO: start - need to be refactor
    private Long time;

    private String category;

    private Integer vout;

    private String address;
    // TODO: end - need to be refactor

    private boolean needFillBlockInfo;

    private TokenType tokenType = TokenType.UNKNOWN;

    @JsonIgnore
    public boolean isSuccess() {
        return null == this.error;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isConfirm() {
        return null != confirmations && confirmations > 0;
    }
}
