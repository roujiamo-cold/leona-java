package com.lehuipay.leona.interceptor;

import com.lehuipay.leona.Const;
import com.lehuipay.leona.contracts.Signer;
import com.lehuipay.leona.exception.LeonaErrorCodeEnum;
import com.lehuipay.leona.exception.LeonaRuntimeException;
import com.lehuipay.leona.utils.Util;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class SignInterceptor implements HttpRequestInterceptor, HttpResponseInterceptor {

    private static final String CHARSET = "utf-8";

    public SignInterceptor(Signer signer, String agentID) {
        this.signer = signer;
        this.agentID = agentID;
    }

    private Signer signer;

    private String agentID;

    @Override
    public void process(HttpRequest httpRequest, HttpContext httpContext) throws IOException {
        // https://www.programcreek.com/java-api-examples/?api=org.apache.http.HttpEntity
        HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) httpRequest;
        HttpEntity entity = entityRequest.getEntity();
        final String body = Util.inputStream2String(entity.getContent());

        String nonce = Util.randomStr(Const.NONCE_MIN_LENGTH, Const.NONCE_MAX_LENTH);
        final String sign = signer.sign(body, nonce);

        httpRequest.setHeader(Const.HEADER_X_LEHUI_AGENTID, agentID);
        httpRequest.setHeader(Const.HEADER_X_LEHUI_NONCE, nonce);
        httpRequest.setHeader(Const.HEADER_X_LEHUI_SIGNATURE, sign);

    }

    @Override
    public void process(HttpResponse httpResponse, HttpContext httpContext) throws IOException {
        if (httpResponse.containsHeader(Const.HEADER_X_LEHUI_NONCE) &&
                httpResponse.containsHeader(Const.HEADER_X_LEHUI_SIGNATURE)) {
            final String nonce = httpResponse.getFirstHeader(Const.HEADER_X_LEHUI_NONCE).getValue();
            final String signature = httpResponse.getFirstHeader(Const.HEADER_X_LEHUI_SIGNATURE).getValue();
            final HttpEntity entity = httpResponse.getEntity();
            String body = Util.inputStream2String(entity.getContent());
            httpResponse.setEntity(new StringEntity(body, CHARSET));
            if (!signer.verify(body, nonce, signature)) {
                throw new LeonaRuntimeException(LeonaErrorCodeEnum.VERIFY_FAIL);
            }
        }
    }
}
