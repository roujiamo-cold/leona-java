package com.lehuipay.leona.interceptor;

import com.lehuipay.leona.Const;
import com.lehuipay.leona.contracts.AsymEncryptor;
import com.lehuipay.leona.contracts.SymmEncryptor;
import com.lehuipay.leona.exception.LeonaErrorCodeEnum;
import com.lehuipay.leona.exception.LeonaRuntimeException;
import com.lehuipay.leona.utils.Util;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class L1Interceptor implements HttpRequestInterceptor, HttpResponseInterceptor {
    private static final String CHARSET = "utf-8";

    private final SymmEncryptor symmEncryptor;

    private final AsymEncryptor asymEncryptor;

    private final String encryptionAccept;

    public L1Interceptor(SymmEncryptor symmEncryptor,AsymEncryptor asymEncryptor, String encryptionAccept){
        this.symmEncryptor = symmEncryptor;
        this.asymEncryptor = asymEncryptor;
        this.encryptionAccept = encryptionAccept;
    }

    @Override
    public void process(HttpRequest httpRequest, HttpContext httpContext) throws IOException {
        HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) httpRequest;
        HttpEntity entity = entityRequest.getEntity();
        final String body = Util.inputStream2String(entity.getContent());
        /**
         * 加密requestBody并写回request
         */
        final String aesKey = Util.randomStr(Const.SECRET_KEY_LENGTH);
        final String encrypted = symmEncryptor.encrypt(body.getBytes(), aesKey);
        entityRequest.setEntity(new StringEntity(encrypted, CHARSET));

        // 加密secretKey
        final byte[] encryptAESKey = asymEncryptor.pubEncode(aesKey);
        // secretKey签名
        byte[] encryptAESKeySignature = asymEncryptor.sign(encryptAESKey);

        httpRequest.setHeader(Const.HEADER_X_LEHUI_ENCRYPTION_LEVEL, Const.HEADER_X_LEHUI_ENCRYPTION_LEVEL_L1);
        httpRequest.setHeader(Const.HEADER_X_LEHUI_ENCRYPTION_ACCEPT, encryptionAccept);
        httpRequest.setHeader(Const.HEADER_X_LEHUI_ENCRYPTION_KEY, Util.base64Encode(encryptAESKey));
        httpRequest.setHeader(Const.HEADER_X_LEHUI_ENCRYPTION_SIGN, Util.base64Encode(encryptAESKeySignature));
    }

    @Override
    public void process(HttpResponse httpResponse, HttpContext httpContext) throws IOException {
        if (httpResponse.containsHeader(Const.HEADER_X_LEHUI_ENCRYPTION_KEY) &&
                httpResponse.containsHeader(Const.HEADER_X_LEHUI_ENCRYPTION_SIGN)) {
            final byte[] key = Util.base64Decode(httpResponse.getFirstHeader(Const.HEADER_X_LEHUI_ENCRYPTION_KEY).getValue());
            final byte[] sign = Util.base64Decode(httpResponse.getFirstHeader(Const.HEADER_X_LEHUI_ENCRYPTION_SIGN).getValue());

            if (!asymEncryptor.verify(key, sign)) {
                throw new LeonaRuntimeException(LeonaErrorCodeEnum.RSA_ENCRYPTION_VERIFY_FAIL);
            }

            final byte[] secretKey = asymEncryptor.priDecode(key);

            final HttpEntity entity = httpResponse.getEntity();
            String body = Util.inputStream2String(entity.getContent());
            final byte[] decryptBody = symmEncryptor.decrypt(body, secretKey);
            httpResponse.setEntity(new ByteArrayEntity(decryptBody));
        }
    }
}
