package com.tokimi.common;

import lombok.Getter;

/**
 * @author william
 */
public enum ErrorConstants {

    CONTROLLER_ERROR(-100, "CONTROLLER_ERROR"),

    RPC_REQUEST_ERROR(-99, "RPC_REQUEST_ERROR"), RPC_NODE_NOT_PREPARED(-98, "RPC_NODE_NOT_PREPARED"),
    RPC_SERVER_RESET_CONNECTION(-101, "RPC_SERVER_RESET_CONNECTION"),

    UTXO_NOT_ENOUGH(-97, "UTXO_NOT_ENOUGH"), BALANCE_NOT_ENOUGH(-96, "BALANCE_NOT_ENOUGH"),
    FEE_BALANCE_NOT_ENOUGH(-95, "FEE_BALANCE_NOT_ENOUGH"), FEE_RATE_NOT_ENOUGH(-94, "FEE_RATE_NOT_ENOUGH"),

    TX_REJECTED(-93, "TX_REJECTED"), TXID_NOT_SAME(-92, "TXID_NOT_SAME"),
    WITHDRAW_AMOUNT_NOT_VALID(-91, "WITHDRAW_AMOUNT_NOT_VALID"), TX_INVALID(-90, "TX_INVALID"),
    TX_NOT_FOUND(-89, "TX_NOT_FOUND"), TX_NOT_CONFIRMED(-88, "TX_NOT_CONFIRMED"),

    USER_NOT_EXIST(-87, "USER_NOT_EXIST"),

    NOT_SUPPORT(-86, "NOT_SUPPORT"),

    PROPERTY_NOT_FOUND(-85, "PROPERTY_NOT_FOUND"), TX_NOT_NEP5(-84, "TX_NOT_NEP5"),
    TX_SHOULD_BE_TOKEN_TX(-83, "TX_SHOULD_BE_TOKEN_TX"), TX_NOT_SUPPORT(-82, "TX_NOT_SUPPORT"),

    TX_NOT_ONCHAIN(-81, "TX_NOT_ONCHAIN"),

    /************ for chain ************/
    CHAIN_PROPERTY_ID_NOT_READY(-78, "CHAIN_PROPERTY_ID_NOT_READY"),
    /************ for chain ************/

    /************ for fee ************/
    FEE_RATE_NOT_READY(-79, "FEE_RATE_NOT_READY"),
    /************ for fee ************/

    /************ for block ************/
    BLOCK_FETCH_FAILED(-80, "BLOCK_FETCH_FAILED"), BLOCK_HASH_EMPTY(-73, "BLOCK_HASH_EMPTY"),
    /************ for block ************/

    /************ for tx ************/
    TX_NOT_VALID(-90, "TX_NOT_VALID"), TX_ONLY_ALLOW_CONTAIN_ONE(-77, "TX_ONLY_ALLOW_CONTAIN_ONE"),
    TX_USER_NOT_FOUND(-76, "TX_USER_NOT_FOUND"), TX_UNKNOWN_TYPE(-75, "TX_UNKNOWN_TYPE"),
    TX_FEE_TOO_LOW(-73, "TX_FEE_TOO_LOW"), TX_INCLUDE_DUST(-72, "TX_INCLUDE_DUST"),
    TX_OTHER_ERROR(-71, "TX_OTHER_ERROR"), TX_SERVICE_NOT_FOUND(-70, "TX_SERVICE_NOT_FOUND"),
    TX_MERGE_CHANGE_ADDRESS_NEED(-69, "TX_MERGE_CHANGE_ADDRESS_NEED"),
    TX_MERGE_REQUEST_NEED(-68, "TX_MERGE_REQUEST_NEED"), TX_WITHDRAW_REQUEST_EXIST(-67, "TX_WITHDRAW_REQUEST_EXIST"),
    TX_ADDRESS_INVALID(-66, "TX_ADDRESS_INVALID"),

    /************ for tx ************/

    /************ for balance ************/
    BALANCE_NOT_SYNC(-74, "BALANCE_NOT_SYNC");
    /************ for balance ************/

    @Getter
    private Integer value;

    @Getter
    private String name;

    ErrorConstants(Integer value, String name) {
        this.value = value;
        this.name = name;
    }
}
