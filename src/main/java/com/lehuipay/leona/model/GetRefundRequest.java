package com.lehuipay.leona.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.lehuipay.leona.utils.CommonUtil;

public class GetRefundRequest {

    public GetRefundRequest(String merchantID, String orderNo, String transactionID, String refundNo, String refundID) {
        if (CommonUtil.isEmpty(merchantID)) {
            throw new IllegalArgumentException("init com.lehuipay.leona.model.GetRefundRequest, merchantID should not be empty");
        }
        if (CommonUtil.isEmpty(orderNo) && CommonUtil.isEmpty(transactionID) &&
                CommonUtil.isEmpty(refundNo) && CommonUtil.isEmpty(refundID)) {
            throw new IllegalArgumentException("init com.lehuipay.leona.model.GetRefundRequest, at least one of orderNo, transactionID, refundNo, refundID");
        }
        this.merchantID = merchantID;
        this.orderNo = orderNo;
        this.transactionID = transactionID;
        this.refundNo = refundNo;
        this.refundID = refundID;
    }

    @JSONField(name = "merchant_id")
    private String merchantID;

    @JSONField(name = "order_no")
    private String orderNo;

    @JSONField(name = "transaction_id")
    private String transactionID;

    @JSONField(name = "refund_no")
    private String refundNo;

    @JSONField(name = "refund_id")
    private String refundID;

    public String getMerchantID() {
        return merchantID;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public String getRefundNo() {
        return refundNo;
    }

    public String getRefundID() {
        return refundID;
    }
}
