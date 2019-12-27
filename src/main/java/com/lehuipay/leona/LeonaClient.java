package com.lehuipay.leona;

import com.lehuipay.leona.contracts.Leona;
import com.lehuipay.leona.exception.LeonaException;
import com.lehuipay.leona.exception.LeonaRuntimeException;
import com.lehuipay.leona.model.GetOrderRequest;
import com.lehuipay.leona.model.GetOrderResponse;
import com.lehuipay.leona.model.GetRefundRequest;
import com.lehuipay.leona.model.GetRefundResponse;
import com.lehuipay.leona.model.MicroPayRequest;
import com.lehuipay.leona.model.MicroPayResponse;
import com.lehuipay.leona.model.QRCodePayRequest;
import com.lehuipay.leona.model.QRCodePayResponse;
import com.lehuipay.leona.model.RefundRequest;
import com.lehuipay.leona.model.RefundResponse;
import com.lehuipay.leona.utils.Util;
import org.apache.http.concurrent.FutureCallback;

import java.io.Closeable;
import java.io.IOException;

/**
 * LeonaClient一个线程安全的,可复用的客户端工具类
 * 多个请求可以使用同一个LeonaClient实例, 避免重复初始化
 *
 * <code>
 *     Leona client = new LeonaClient
 *                 .Builder(agent_id, agent_key)
 *                 .setPartnerPriKey(lhPubKey)
 *                 .setLhPubKey(partnerPriKey)
 * //                .setSecretKey(secret_key)
 *                 .setEncryptionLevel(Const.HEADER_X_LEHUI_ENCRYPTION_LEVEL_L1)
 *                 .setEncryptionAccept(Const.HEADER_X_LEHUI_ENCRYPTION_LEVEL_L0)
 *                 .build();
 *
 *      try {
 *             final QRCodePayRequest req =
 *                     new QRCodePayRequest(merchantID, "2", "xxx", 1, null, null);
 *             final QRCodePayResponse resp = client.qrCodePay(req);
 *             // do somethong with resp
 *             ...
 *         } catch (LeonaException e) {
 *             String type = e.getType();
 *             String code = e.getCode();
 *             String message = e.getMessage();
 *             System.err.printf("type: %s, code: %s, message: %s\n", type, code, message);
 *         }
 *
 *      try {
 *             final GetOrderRequest req = new GetOrderRequest(merchantID, "xxx", null);
 *             final GetOrderResponse resp = client.getOrder(req);
 *             // do somethong with resp
 *             ...
 *         } catch (LeonaException e) {
 *             String type = e.getType();
 *             String code = e.getCode();
 *             String message = e.getMessage();
 *             System.err.printf("type: %s, code: %s, message: %s\n", type, code, message);
 *         }
 *
 *       client.close();
 * </code>
 * client使用完毕后一定要close()以释放网络资源
 */
public class LeonaClient implements Leona, Closeable {

    private final SyncHTTPClient httpClient;

    private LeonaClient(Builder builder) {
        this.options = new Options(builder.agentID,
                builder.agentKey,
                builder.partnerPriKey,
                builder.lhPubKey,
                builder.encryptionLevel,
                builder.encryptionAccept,
                builder.secretKey);

        httpClient = new SyncHTTPClient(this.options);
    }

    private final Options options;

    public static Builder Builder(String agentID, String agentKey) {
        return new Builder(agentID, agentKey);
    }

    public static class Builder {
        private final String agentID;
        private final String agentKey;
        private String partnerPriKey;
        private String lhPubKey;
        private String encryptionLevel;
        private String encryptionAccept;
        private String secretKey;

        public Builder(String agentID, String agentKey) {
            if (Util.isEmpty(agentID)) {
                throw new IllegalArgumentException("agentID should not be empty");
            }
            if (Util.isEmpty(agentKey)) {
                throw new IllegalArgumentException("agentKey should not be empty");
            }
            this.agentID = agentID;
            this.agentKey = agentKey;
        }

        public Builder setPartnerPriKey(String partnerPriKey) {
            if (Util.isEmpty(partnerPriKey)) {
                throw new IllegalArgumentException("partnerPriKey should not be empty");
            }
            this.partnerPriKey = partnerPriKey;
            return this;
        }

        public Builder setLhPubKey(String lhPubKey) {
            if (Util.isEmpty(lhPubKey)) {
                throw new IllegalArgumentException("lhPubKey should not be empty");
            }
            this.lhPubKey = lhPubKey;
            return this;
        }

        public Builder setEncryptionLevel(String encryptionLevel) {
            if (Util.isEmpty(encryptionLevel)) {
                throw new IllegalArgumentException("encryptionLevel should not be empty");
            }
            this.encryptionLevel = encryptionLevel;
            return this;
        }

        public Builder setEncryptionAccept(String encryptionAccept) {
            if (Util.isEmpty(encryptionAccept)) {
                throw new IllegalArgumentException("encryptionAccept should not be empty");
            }
            this.encryptionAccept = encryptionAccept;
            return this;
        }

        public Builder setSecretKey(String secretKey) {
            if (Util.isEmpty(secretKey)) {
                throw new IllegalArgumentException("secretKey should not be empty");
            }
            this.secretKey = secretKey;
            return this;
        }

        public LeonaClient build() {
            return new LeonaClient(this);
        }
    }

    /**
     * 二维码支付
     *
     * @param req
     * @return
     * @throws LeonaRuntimeException
     */
    public QRCodePayResponse qrCodePay(QRCodePayRequest req) throws LeonaException {
        try {
            return httpClient.doPost(Const.LEHUI_QRCODE_URL, req, QRCodePayResponse.class);
        } catch (LeonaRuntimeException e) {
            throw new LeonaException(e);
        }
    }

    public void qrCodePay(QRCodePayRequest req, FutureCallback<QRCodePayResponse> callback) throws LeonaException {
//        httpClient.doPost(Const.LEHUI_QRCODE_URL, req, QRCodePayResponse.class, callback);
    }

    /**
     * 刷卡交易
     *
     * @param req
     * @return
     * @throws Exception
     */
    public MicroPayResponse microPay(MicroPayRequest req) throws LeonaException {
        try {
            return httpClient.doPost(Const.LEHUI_MICROPAY_URL, req, MicroPayResponse.class);
        } catch (LeonaRuntimeException e) {
            throw new LeonaException(e);
        }
    }

    public void microPay(MicroPayRequest req, FutureCallback<MicroPayResponse> callback) throws LeonaException {
//        httpClient.doPost(Const.LEHUI_MICROPAY_URL, req, MicroPayResponse.class, callback);
    }

    /**
     * 查询交易
     *
     * @param req
     * @return
     * @throws Exception
     */
    public GetOrderResponse getOrder(GetOrderRequest req) throws LeonaException {
        try {
            return httpClient.doPost(Const.LEHUI_GET_ORDER_URL, req, GetOrderResponse.class);
        } catch (LeonaRuntimeException e) {
            throw new LeonaException(e);
        }

    }

    public void getOrder(GetOrderRequest req, FutureCallback<GetOrderResponse> callback) throws LeonaException {
        try {
//            httpClient.doPost(Const.LEHUI_GET_ORDER_URL, req, GetOrderResponse.class, callback);
        } catch (LeonaRuntimeException e) {
            throw new LeonaException(e);
        }
    }

    /**
     * 退款
     *
     * @param req
     * @return
     * @throws Exception
     */
    public RefundResponse refund(RefundRequest req) throws LeonaException {
        try {
            return httpClient.doPost(Const.LEHUI_REFUND_URL, req, RefundResponse.class);
        } catch (LeonaRuntimeException e) {
            throw new LeonaException(e);
        }
    }

    public void refund(RefundRequest req, FutureCallback<RefundResponse> callback) throws LeonaException {
//        httpClient.doPost(Const.LEHUI_REFUND_URL, req, RefundResponse.class, callback);
    }

    /**
     * 查询退款
     *
     * @param req
     * @return
     * @throws Exception
     */
    public GetRefundResponse getRefund(GetRefundRequest req) throws LeonaException {
        try {
            return httpClient.doPost(Const.LEHUI_GET_REFUND_URL, req, GetRefundResponse.class);
        } catch (LeonaRuntimeException e) {
            throw new LeonaException(e);
        }
    }

    public void getRefund(GetRefundRequest req, FutureCallback<GetRefundResponse> callback) throws LeonaException {
//        httpClient.doPost(Const.LEHUI_GET_REFUND_URL, req, GetRefundResponse.class, callback);
    }

    @Override
    public void close() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
    }

    // ******************setter & getter********************

    public Options getOptions() {
        return options;
    }
}
