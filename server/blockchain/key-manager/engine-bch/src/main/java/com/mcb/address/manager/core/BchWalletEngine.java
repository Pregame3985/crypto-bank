package com.mcb.address.manager.core;

import com.mcb.address.manager.model.HdWalletDTO;
import com.mcb.address.manager.util.ChainSettings;
import com.mcb.address.manager.util.Slip44CoinType;
import com.google.common.collect.Lists;
import de.tobibrandt.MoneyNetwork;
import de.tobibrandt.bitcoincash.BitcoinCashAddressFormatter;
import de.tobibrandt.bitcoincash.BitcoinCashAddressType;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;

import java.util.ArrayList;
import java.util.List;

/**
 * @author william
 */
@Slf4j
public class BchWalletEngine extends AbstractWalletEngine {

    private int network;

    public BchWalletEngine(int network) {

        this.network = network;
        this.internalWalletEngine = network == 0 ? new InternalWalletEngine() : new TestInternalWalletEngine();
    }

    @Override
    protected List<HdWalletDTO.Address> extractAllAddress(DeterministicKey childDk) {

        List<HdWalletDTO.Address> addresses = new ArrayList<>();

        addresses.add(new HdWalletDTO.Address("LEGACY", childDk.toAddress(this.internalWalletEngine.getNetworkParameters()).toBase58()));
        addresses.add(new HdWalletDTO.Address("CASHADDR",
                BitcoinCashAddressFormatter.toCashAddress(BitcoinCashAddressType.P2PKH, childDk.getPubKeyHash(), network == 0 ? MoneyNetwork.MAIN : MoneyNetwork.TEST)));

        return addresses;
    }

    private static class InternalWalletEngine extends InternalAbstractWalletEngine {

        @Override
        public Integer getTokenId() {
            return ChainSettings.TOKEN_ID_BCH_54;
        }

        @Override
        protected Slip44CoinType slip44CoinType() {
            return Slip44CoinType.BCH;
        }

        @Override
        protected NetworkParameters getNetworkParameters() {
            return MainNetParams.get();
        }

        @Override
        public boolean isValidAddress(String address) {
            if (address.startsWith("1") || address.startsWith("3")) {
                return isValidBase58(address);
            } else {
                return BitcoinCashAddressFormatter.isValidCashAddress(address, MoneyNetwork.MAIN);
            }
        }

        @Override
        protected List<String> myRegExs() {
            return Lists.newArrayList("^[13][1-9A-HJ-NP-Za-km-z]{30,34}$", "^(bitcoincash:)?(q|p)[a-z0-9]{41}$");
        }
    }

    private static final class TestInternalWalletEngine extends InternalWalletEngine {

        @Override
        protected NetworkParameters getNetworkParameters() {
            return RegTestParams.get();
        }

        @Override
        public boolean isValidAddress(String address) {
            if (address.startsWith("m") || address.startsWith("n") || address.startsWith("2")) {
                return isValidBase58(address);
            } else {
                return BitcoinCashAddressFormatter.isValidCashAddress(address, MoneyNetwork.TEST);
            }
        }

        @Override
        protected List<String> myRegExs() {
            return Lists.newArrayList("^[mn2][1-9A-HJ-NP-Za-km-z]{30,34}$", "^((bchreg|bchtest):)?(q|p)[a-z0-9]{41}$");
        }
    }
}
