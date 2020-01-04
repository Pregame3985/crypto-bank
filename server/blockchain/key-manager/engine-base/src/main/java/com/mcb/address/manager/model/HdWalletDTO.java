package com.mcb.address.manager.model;

import com.mcb.address.manager.util.Slip44CoinType;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author william
 */
@Getter
@Setter
public class HdWalletDTO {

    private String publicKey;

    private String publicKeyAsHex;

//    private String privateKeyAsHex; //temp

    private byte[] privateKey;

    private String address;

    private Slip44CoinType slip44CoinType;

    private byte[] rawPrivateKey;

    private byte[] rawPublicKey;

    private byte[] publicKeyHash;

    private String memo;

    private List<Address> addresses = new ArrayList<>();

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class Address {

        // legacy ; cashaddr
        @NonNull
        private String type;

        @NonNull
        private String address;
    }
}
