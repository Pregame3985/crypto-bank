package com.tokimi.common.signature.service;

/**
 * @author william
 */
public interface SignatureService {

    byte[] signature(byte[] r, byte[] message);

    boolean verify(byte[] u, byte[] message, byte[] signature);
}
