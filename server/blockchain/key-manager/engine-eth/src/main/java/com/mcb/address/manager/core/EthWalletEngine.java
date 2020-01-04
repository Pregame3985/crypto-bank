package com.mcb.address.manager.core;

import com.mcb.address.manager.util.ChainSettings;
import com.mcb.address.manager.util.Slip44CoinType;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.web3j.crypto.Keys;

import java.util.List;

/**
 * @author william
 */
@Slf4j
public class EthWalletEngine extends AbstractWalletEngine {

    public EthWalletEngine(int network) {

        this.internalWalletEngine = network == 0 ? new InternalWalletEngine() : new TestInternalWalletEngine();
    }

    private static class InternalWalletEngine extends InternalAbstractWalletEngine {

        @Override
        public Integer getTokenId() {
            return ChainSettings.TOKEN_ID_ETH_16;
        }

        @Override
        protected Slip44CoinType slip44CoinType() {
            return Slip44CoinType.ETH;
        }

        @Override
        protected NetworkParameters getNetworkParameters() {
            return MainNetParams.get();
        }

        @Override
        protected String customGenerate(DeterministicKey childDk) {
            ECKey uncompressedChildKey = childDk.decompress();

            String hexK = uncompressedChildKey.getPublicKeyAsHex().substring(2);
            String address = Keys.getAddress(hexK);
            address = Keys.toChecksumAddress(address);

            return address;
        }

        @Override
        public boolean isValidAddress(String address) {

            return true;
        }

        @Override
        protected List<String> myRegExs() {
            return Lists.newArrayList("^0x[0-9a-fA-F]{40}$");
        }

    }

    private final static class TestInternalWalletEngine extends InternalWalletEngine {

        @Override
        protected NetworkParameters getNetworkParameters() {
            return RegTestParams.get();
        }
    }

}
