package com.lehuipay.leona;

import com.lehuipay.leona.contracts.SymmEncryptor;
import com.lehuipay.leona.utils.AESPKCS7;
import com.lehuipay.leona.utils.Util;

public class AESEncryptor implements SymmEncryptor {

    AESPKCS7 aes = new AESPKCS7();

    @Override
    public String encrypt(byte[] body, String secretKey) {
        final String iv = Util.randomStr(Const.IV_LENGTH);
        return aes.encryptWithIVBase64(body, secretKey.getBytes(), iv.getBytes());
    }

    @Override
    public byte[] decrypt(String body, byte[] secretKey) {
        return aes.decryptWithIVBase64(body, secretKey, Const.IV_LENGTH);
    }

}
