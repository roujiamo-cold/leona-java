package com.lehuipay.leona.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.lehuipay.leona.utils.Util;

public class RefundRequest {

    public RefundRequest(String merchantID, String orderNo, String transactionID, String refundNo, Integer amount) {
        if (Util.isEmpty(merchantID)) {
            throw new IllegalArgumentException("init com.lehuipay.leona.model.RefundRequest, merchantID should not be empty");
        }
        if (Util.isEmpty(orderNo) && Util.isEmpty(transactionID)) {
            throw new IllegalArgumentException("init com.lehuipay.leona.model.RefundRequest, at least one of orderNo, transactionID");
        }
        if (Util.isEmpty(refundNo)) {
            throw new IllegalArgumentException("init com.lehuipay.leona.model.RefundRequest, refundNo should not be empty");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("init com.lehuipay.leona.model.RefundRequest, amount should be greater than zero");
        }
        this.merchantID = merchantID;
        this.orderNo = orderNo;
        this.transactionID = transactionID;
        this.refundNo = refundNo;
        this.amount = amount;
    }

    @JSONField(name = "merchant_id")
    private String merchantID;

    @JSONField(name = "order_no")
    private String orderNo;

    @JSONField(name = "transaction_id")
    private String transactionID;

    @JSONField(name = "refund_no")
    private String refundNo;

    @JSONField(name = "amount")
    private Integer amount;

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

    public Integer getAmount() {
        return amount;
    }
}
