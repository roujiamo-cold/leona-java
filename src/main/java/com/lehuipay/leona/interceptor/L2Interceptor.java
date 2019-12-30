package com.lehuipay.leona.interceptor;

import com.lehuipay.leona.Const;
import com.lehuipay.leona.contracts.SymmEncryptor;
import com.lehuipay.leona.utils.CommonUtil;
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

public class L2Interceptor implements HttpRequestInterceptor, HttpResponseInterceptor {

    private static final String CHARSET = "utf-8";

    public L2Interceptor(SymmEncryptor symmEncryptor, String secretKey, String encryptionAccept) {
        if (CommonUtil.isEmpty(secretKey)) {
            throw new IllegalArgumentException("init com.lehuipay.leona.L2Interceptor, secretKey should not be empty");
        }
        this.symmEncryptor = symmEncryptor;
        this.secretKey = secretKey;
        this.encryptionAccept = encryptionAccept;
    }

    private final SymmEncryptor symmEncryptor;

    private final String secretKey;

    private final String encryptionAccept;

    @Override
    public void process(HttpRequest httpRequest, HttpContext httpContext) throws IOException {
        if (httpRequest instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) httpRequest;
            HttpEntity entity = entityRequest.getEntity();
            final byte[] body = CommonUtil.inputStream2ByteArray(entity.getContent());
            final String encrypted = symmEncryptor.encrypt(body, secretKey);
            entityRequest.setEntity(new StringEntity(encrypted, CHARSET));
        }

        httpRequest.setHeader(Const.HEADER_X_LEHUI_ENCRYPTION_LEVEL, Const.HEADER_X_LEHUI_ENCRYPTION_LEVEL_L2);
        httpRequest.setHeader(Const.HEADER_X_LEHUI_ENCRYPTION_ACCEPT, encryptionAccept);
    }

    @Override
    public void process(HttpResponse httpResponse, HttpContext httpContext) throws IOException {
        final HttpEntity entity = httpResponse.getEntity();
        String body = CommonUtil.inputStream2String(entity.getContent());
        final byte[] decryptBody = symmEncryptor.decrypt(body, secretKey.getBytes());
        httpResponse.setEntity(new ByteArrayEntity(decryptBody));
    }
}
