package com.mcb.address.manager.core;

import com.mcb.address.manager.util.ChainSettings;
import com.mcb.address.manager.util.Slip44CoinType;
import com.google.common.collect.Lists;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.spongycastle.util.encoders.Hex;

import java.util.List;

/**
 * @author william
 */
@Slf4j
public class BtcWalletEngine extends AbstractWalletEngine {

    public BtcWalletEngine(int network) {

        this.internalWalletEngine = network == 0 ? new InternalWalletEngine() : new TestInternalWalletEngine();
    }

    private static class InternalWalletEngine extends InternalAbstractWalletEngine {

        @Setter
        protected boolean usingSegwit = true;

        @Override
        public Integer getTokenId() {
            return ChainSettings.TOKEN_ID_BTC_2;
        }

        @Override
        public Slip44CoinType slip44CoinType() {
            return Slip44CoinType.BTC;
        }

        @Override
        protected NetworkParameters getNetworkParameters() {
            return MainNetParams.get();
        }

        @Override
        protected String customGenerate(DeterministicKey childDk) {
            if (usingSegwit) {
                return segwitCustomGenerate(childDk);
            } else {
                return defaultCustomGenerate(childDk);
            }
        }

        private String defaultCustomGenerate(DeterministicKey childDk) {
            return childDk.toAddress(getNetworkParameters()).toBase58();
        }

        byte segwitAddressPrefix() {
            return (byte) 5;
        }

        private String segwitCustomGenerate(DeterministicKey childDk) {
            return convertToSegwitAddress(defaultCustomGenerate(childDk), segwitAddressPrefix());
        }

        private String convertToSegwitAddress(String legacyAddress, byte prefix) {
            byte[] decoded = Utils.parseAsHexOrBase58(legacyAddress);
            // We should throw off header byte that is 0 for Bitcoin (Main)
            byte[] pureBytes = new byte[20];
            System.arraycopy(decoded, 1, pureBytes, 0, 20);
            // Than we should prepend the following bytes:
            byte[] scriptSig = new byte[pureBytes.length + 2];
            scriptSig[0] = 0x00;
            scriptSig[1] = 0x14;
            System.arraycopy(pureBytes, 0, scriptSig, 2, pureBytes.length);
            log.info("redeem script: {}", Hex.toHexString(scriptSig));
            byte[] addressBytes = Utils.sha256hash160(scriptSig);
            log.info("script hash: {}", Hex.toHexString(addressBytes));
            // Here are the address bytes
            byte[] readyForAddress = new byte[addressBytes.length + 1 + 4];
            // prepending p2sh header:
            readyForAddress[0] = prefix;
            System.arraycopy(addressBytes, 0, readyForAddress, 1, addressBytes.length);
            // But we should also append check sum:
            byte[] checkSum = Sha256Hash.hashTwice(readyForAddress, 0, addressBytes.length + 1);
            System.arraycopy(checkSum, 0, readyForAddress, addressBytes.length + 1, 4);
            // To get the final address:
            return Base58.encode(readyForAddress);
        }

        @Override
        public boolean isValidAddress(String address) {
            boolean isValid = isValidBase58(address);

            if (!isValid) {
                log.warn("address {} checksum failed", address);
            }

            return isValid;
        }

        @Override
        protected List<String> myRegExs() {
            return Lists.newArrayList("^[13][1-9A-HJ-NP-Za-km-z]{30,34}$");
        }
    }

    private static final class TestInternalWalletEngine extends InternalWalletEngine {

        @Override
        protected NetworkParameters getNetworkParameters() {
            return RegTestParams.get();
        }

        @Override
        public Slip44CoinType slip44CoinType() {
            return Slip44CoinType.BTCTEST;
        }

        @Override
        byte segwitAddressPrefix() {
            return (byte) 196;
        }

        @Override
        protected List<String> myRegExs() {
            return Lists.newArrayList("^[mn2][1-9A-HJ-NP-Za-km-z]{30,34}$");
        }
    }
}
