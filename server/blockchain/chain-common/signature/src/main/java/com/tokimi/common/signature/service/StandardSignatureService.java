package com.tokimi.common.signature.service;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import static org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;

/**
 * @author william
 */
@Slf4j
public class StandardSignatureService implements SignatureService {

    private static final String DEFAULT_CURVE_NAME = "secp256k1";

    private final ECDomainParameters domainParams;
    private final ECParameterSpec paramSpec;
    private final ECNamedCurveSpec namedCurveSpec;

    public StandardSignatureService() {
        this(DEFAULT_CURVE_NAME);
    }

    public StandardSignatureService(String curveName) {
        Security.addProvider(new BouncyCastleProvider());

        X9ECParameters params = SECNamedCurves.getByName(curveName);
        this.domainParams = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());
        this.paramSpec = new ECParameterSpec(params.getCurve(), params.getG(), params.getN(), params.getH());
        this.namedCurveSpec = new ECNamedCurveSpec(curveName, params.getCurve(), params.getG(), params.getN());
    }

    @Override
    public byte[] signature(byte[] r, byte[] message) {

        Signature sigGenerator;
        try {
            sigGenerator = Signature.getInstance("NONEwithECDSA", PROVIDER_NAME);
            sigGenerator.initSign(myPrivateKey(r));
            sigGenerator.update(message);
            return sigGenerator.sign();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            log.error("generate sig failed : {}", e);
            return null;
        }
    }

    @Override
    public boolean verify(byte[] u, byte[] message, byte[] signature) {

        Signature sigVerifier;
        try {
            sigVerifier = Signature.getInstance("NONEwithECDSA", PROVIDER_NAME);
            sigVerifier.initVerify(myPublicKey(u));
            sigVerifier.update(message);
            return sigVerifier.verify(signature);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            log.error("verify sig failed : {}", e);
            return false;
        }
    }

    private PrivateKey myPrivateKey(byte[] r) {
        try {
            return KeyFactory.getInstance("ECDSA", PROVIDER_NAME).generatePrivate(new ECPrivateKeySpec(new BigInteger(1, r), this.paramSpec));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
            return null;
        }
    }

    private PublicKey myPublicKey(byte[] u) {
        try {
            return KeyFactory.getInstance("ECDSA", PROVIDER_NAME).generatePublic(new ECPublicKeySpec(this.domainParams.getCurve().decodePoint(u), this.paramSpec));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
            return null;
        }
    }
}