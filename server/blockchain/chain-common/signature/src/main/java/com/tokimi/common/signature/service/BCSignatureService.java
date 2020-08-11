package com.tokimi.common.signature.service;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Security;
import java.util.Arrays;

/**
 * @author william
 */
@Slf4j
public class BCSignatureService implements SignatureService {

    private static final String DEFAULT_CURVE_NAME = "secp256k1";

    private final ECDomainParameters domainParams;

    public BCSignatureService() {
        this(DEFAULT_CURVE_NAME);
    }

    public BCSignatureService(String curveName) {
        Security.addProvider(new BouncyCastleProvider());

        X9ECParameters params = SECNamedCurves.getByName(curveName);
        this.domainParams = new ECDomainParameters(params.getCurve(),
                params.getG(), params.getN(), params.getH(), params.getSeed());
    }

    @Override
    public byte[] signature(byte[] r, byte[] message) {

        BigInteger s = new BigInteger(1, r);
        Signature signature = _signature(s, message);
        return signature.encodeToDER();
    }

    private Signature _signature(BigInteger s, byte[] message) {
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
        ECPrivateKeyParameters privateKey = new ECPrivateKeyParameters(s, domainParams);
        signer.init(true, privateKey);
        BigInteger[] components = signer.generateSignature(message);
        return new Signature(domainParams.getN(), components[0], components[1]).halfCut();
    }

    @Override
    public boolean verify(byte[] u, byte[] message, byte[] signature) {

        return _verify(u, message, decodeFromDER(signature));
    }

    private boolean _verify(byte[] u, byte[] message, Signature signature) {

        ECDSASigner signer = new ECDSASigner();
//        ECPoint point = domainParams.getG().multiply(s);

        ECPublicKeyParameters params = new ECPublicKeyParameters(domainParams.getCurve().decodePoint(u), domainParams);
        signer.init(false, params);
        try {
            return signer.verifySignature(message, signature.r, signature.s);
        } catch (NullPointerException e) {
            log.error("Caught NPE inside bouncy castle", e);
            return false;
        }
    }

    private Signature decodeFromDER(byte[] bytes) {
        ASN1InputStream decoder = null;
        try {
            decoder = new ASN1InputStream(bytes);
            DLSequence seq = (DLSequence) decoder.readObject();
            if (seq == null) {
                throw new RuntimeException("Reached past end of ASN.1 stream.");
            }
            ASN1Integer r, s;
            try {
                r = (ASN1Integer) seq.getObjectAt(0);
                s = (ASN1Integer) seq.getObjectAt(1);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException(e);
            }
            return new Signature(domainParams.getN(), r.getPositiveValue(), s.getPositiveValue());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (decoder != null) {
                try {
                    decoder.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    class Signature {

        private BigInteger n, r, s;
        private BigInteger h;

        Signature(BigInteger n, BigInteger r, BigInteger s) {
            this.n = n;
            this.r = r;
            this.s = s;
            this.h = domainParams.getN().shiftRight(1);
        }

        Signature halfCut() {
            return s.compareTo(h) <= 0 ? this : new Signature(n, r, this.n.subtract(s));
        }

        byte[] encodeToDER() {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream(72);
                DERSequenceGenerator seq = new DERSequenceGenerator(bos);
                seq.addObject(new ASN1Integer(r));
                seq.addObject(new ASN1Integer(s));
                seq.close();
                return bos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);  // Cannot happen.
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Signature other = (Signature) o;
            return r.equals(other.r) && s.equals(other.s);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[]{r, s});
        }
    }
}