package com.lehuipay.leona;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lehuipay.leona.exception.LeonaErrorCodeEnum;
import com.lehuipay.leona.exception.LeonaException;
import com.lehuipay.leona.exception.LeonaRuntimeException;
import com.lehuipay.leona.interceptor.L1Interceptor;
import com.lehuipay.leona.interceptor.L2Interceptor;
import com.lehuipay.leona.interceptor.SignInterceptor;
import com.lehuipay.leona.model.ErrorMessage;
import com.lehuipay.leona.utils.Util;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.Closeable;
import java.io.IOException;

public class SyncHTTPClient implements Closeable {

    private static final String CHARSET = "utf-8";

    private final CloseableHttpClient httpClient;

    public SyncHTTPClient(Options options) {

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(50000)
                .setConnectionRequestTimeout(1000)
                .build();

        // 签名拦截器, 对请求与响应进行签名与验签
        SignInterceptor signInterceptor =
                new SignInterceptor(new HMACSigner(options.getAgentID(), options.getAgentKey()), options.getAgentID());

        final HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .addInterceptorLast((HttpRequestInterceptor) signInterceptor)
                .addInterceptorFirst((HttpResponseInterceptor) signInterceptor);

        if (!Util.isEmpty(options.getEncryptionLevel())) {
            switch (options.getEncryptionLevel()) {
                case Const.HEADER_X_LEHUI_ENCRYPTION_LEVEL_L1:
                    L1Interceptor l1 =
                            new L1Interceptor(
                                    new AESEncryptor(), new RSAEnctryptor(options.getPartnerPriKey(), options.getLhPubKey()), options.getEncryptionAccept());
                    httpClientBuilder.addInterceptorFirst((HttpRequestInterceptor) l1);
                    if (!Util.equals(options.getEncryptionAccept(), Const.HEADER_X_LEHUI_ENCRYPTION_LEVEL_L0)) {
                        httpClientBuilder.addInterceptorLast((HttpResponseInterceptor) l1);
                    }
                    break;
                case Const.HEADER_X_LEHUI_ENCRYPTION_LEVEL_L2:
                    L2Interceptor l2 =
                            new L2Interceptor(
                                    new AESEncryptor(), options.getSecretKey(), options.getEncryptionAccept());
                    httpClientBuilder.addInterceptorFirst((HttpRequestInterceptor) l2);
                    if (!Util.equals(options.getEncryptionAccept(), Const.HEADER_X_LEHUI_ENCRYPTION_LEVEL_L0)) {
                        httpClientBuilder.addInterceptorLast((HttpResponseInterceptor) l2);
                    }
                    break;
            }
        }

        httpClient = httpClientBuilder.build();
    }


    public <T, R> T doPost(final String url, R params, Class<T> clazz) throws LeonaException {

        final HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Connection", "keep-alive");
        httpPost.setHeader("Accept", "*/*");
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");

        StringEntity entity = new StringEntity(JSONObject.toJSONString(params), CHARSET);
        httpPost.setEntity(entity);

        try (
                final CloseableHttpResponse httpResp = httpClient.execute(httpPost);
        ) {
            StatusLine status = httpResp.getStatusLine();
            HttpEntity respEntity = httpResp.getEntity();
            if (respEntity != null) {
                String jsonString = Util.inputStream2String(respEntity.getContent());
                if (status.getStatusCode() == HttpStatus.SC_OK) {
                    return JSON.parseObject(jsonString, clazz);
                } else {
                    final ErrorMessage errMsg = JSON.parseObject(jsonString, ErrorMessage.class);
                    throw new LeonaException(errMsg.getType(), errMsg.getCode(), errMsg.getMessage());
                }
            } else {
                throw new LeonaRuntimeException("empty response entity");
            }
        } catch (IOException e) {
            throw new LeonaRuntimeException(LeonaErrorCodeEnum.HTTP_ERROR, e);
        }
    }

    @Override
    public void close() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
    }
}
