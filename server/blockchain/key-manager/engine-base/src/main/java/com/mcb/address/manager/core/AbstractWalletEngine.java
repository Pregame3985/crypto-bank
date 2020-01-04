package com.mcb.address.manager.core;

import com.mcb.address.manager.model.HdWalletDTO;
import com.mcb.address.manager.model.path.PathGenerator;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bitcoinj.core.Utils.HEX;

/**
 * @author william
 */
@Slf4j
public abstract class AbstractWalletEngine implements WalletEngine {

    public InternalAbstractWalletEngine internalWalletEngine;

    @Override
    public boolean isSupportEngine(Integer realTokenId) {
        return this.internalWalletEngine.getTokenId().equals(realTokenId);
    }

    @Override
    public HdWalletDTO generateAddress(SeedRule seedRule, int index, List<String> hotWalletAddresses) {

        DeterministicKey childDk = generateDeterministicKey(seedRule, index);

        boolean isCompressed = this.internalWalletEngine.isCompressed();

        byte[] rawPrivateKey = isCompressed ? childDk.getPrivKeyBytes() : childDk.decompress().getPrivKeyBytes();
        byte[] rawPublicKey = isCompressed ? childDk.getPubKey() : childDk.decompress().getPubKey();
        byte[] publicKeyHash = isCompressed ? childDk.getPubKeyHash() : childDk.decompress().getPubKeyHash();

        byte[] b58PrivateKey = isCompressed
                ? childDk.getPrivateKeyEncoded(this.internalWalletEngine.getNetworkParameters()).toBase58().getBytes()
                : childDk.decompress().getPrivateKeyEncoded(this.internalWalletEngine.getNetworkParameters()).toBase58()
                .getBytes();
        String b58PublicKey = childDk.serializePubB58(this.internalWalletEngine.getNetworkParameters());

        String publicKeyAsHex = isCompressed ? childDk.getPublicKeyAsHex() : childDk.decompress().getPublicKeyAsHex();

        String address = this.internalWalletEngine.customGenerate(childDk);
        log.debug("address {}", address);

        HdWalletDTO walletDTO = new HdWalletDTO();

        walletDTO.setRawPrivateKey(rawPrivateKey);
        walletDTO.setRawPublicKey(rawPublicKey);
        walletDTO.setPublicKeyHash(publicKeyHash);

        walletDTO.setPrivateKey(b58PrivateKey);
        walletDTO.setPublicKey(b58PublicKey);

        walletDTO.setPublicKeyAsHex(publicKeyAsHex);

        walletDTO.setAddress(address);
        walletDTO.setSlip44CoinType(this.internalWalletEngine.slip44CoinType());
        walletDTO.setAddresses(extractAllAddress(childDk));

        return walletDTO;
    }

    protected List<HdWalletDTO.Address> extractAllAddress(DeterministicKey childDk) {
        return new ArrayList<>();
    }

    public DeterministicKey generateDeterministicKey(SeedRule seedRule, int index) {
        // generate root private key, the root private key can generate other child
        // private keys.
        MasterKeyGenerate masterKeyGenerate = () -> HDKeyDerivation
                .createMasterPrivateKey(HEX.decode(seedRule.performRule()));
        // generate a hierarchy tree by root private key
        DeterministicHierarchy masterDh = new DeterministicHierarchy(masterKeyGenerate.generateMaster());
        // create a childNumber[] by the index
        ChildNumber[] path = getPathGenerator(index).generatePath();
        int depth = path.length - 1;
        // generate the index th child private key
        return masterDh.deriveChild(Arrays.asList(path).subList(0, depth), false, true, path[depth]);
    }

    protected PathGenerator getPathGenerator(int index) {
        return PathGenerator.create(index, this.internalWalletEngine.slip44CoinType());
    }

    protected interface MasterKeyGenerate {
        DeterministicKey generateMaster();
    }

    @Override
    public boolean isValid(String address) {
        return this.internalWalletEngine.isValidAddress(address) && this.isMatchRegEx(address);
    }

    private boolean isMatchRegEx(String address) {

        List<String> regExs = this.internalWalletEngine.myRegExs();

        if (null != regExs && !regExs.isEmpty()) {

            for (String regEx : regExs) {
                Pattern pattern = Pattern.compile(regEx);
                Matcher matcher = pattern.matcher(address);

                if (matcher.matches()) {
                    return true;
                }
            }

            log.warn("address {} reg ex failed", address);
            return false;
        } else {
            log.warn("reg ex not specify for address {}", address);
            return false;
        }
    }
}
