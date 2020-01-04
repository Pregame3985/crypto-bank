package com.mcb.address.manager.model.path;

import com.mcb.address.manager.util.Slip44CoinType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.crypto.ChildNumber;

/**
 * @author william
 */
@Slf4j
public abstract class PathGenerator {

    public static PathGenerator create(int addressIndex, Slip44CoinType coinType) {
        return new InternalPathGenerator(addressIndex, coinType);
    }

    public abstract ChildNumber[] generatePath();

    private static class InternalPathGenerator extends PathGenerator {

        @NonNull
        private Integer addressIndex;

        @NonNull
        private Slip44CoinType coinType;

        @Setter
        private Integer account = 0;

        InternalPathGenerator(Integer addressIndex, Slip44CoinType coinType) {
            this.addressIndex = addressIndex;
            this.coinType = coinType;
        }

        @Override
        @JsonIgnore
        public ChildNumber[] generatePath() {

            // https://github.com/satoshilabs/slips/blob/master/slip-0044.md

            // TODO: check gap limit and index overflow
            return new ChildNumber[]{
                    new ChildNumber(44, true), // purpose'
                    new ChildNumber(coinType.getValue(), true), // coin_type'
                    new ChildNumber(account, true), // account'
                    new ChildNumber(0, false), // change
                    new ChildNumber(addressIndex, false), // address_index
            };
        }
    }
}
