package com.lehuipay.leona.contracts;

public interface Signer {

    String sign(String body, String nonce);

    boolean verify(String body, String nonce, String signature);
}
