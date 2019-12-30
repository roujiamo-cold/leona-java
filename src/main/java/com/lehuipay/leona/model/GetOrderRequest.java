package com.lehuipay.leona.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.lehuipay.leona.utils.CommonUtil;

public class GetOrderRequest {

    public GetOrderRequest(String merchantID, String orderNo, String transactionID) {
        if (CommonUtil.isEmpty(merchantID)) {
            throw new IllegalArgumentException("init com.lehuipay.leona.model.GetOrderRequest, merchantID should not be empty");
        }
        if (CommonUtil.isEmpty(orderNo) && CommonUtil.isEmpty(transactionID)) {
            throw new NumberFormatException("init com.lehuipay.leona.model.GetOrderRequest, at least one of orderNo and transactionID");
        }
        this.merchantID = merchantID;
        this.orderNo = orderNo;
        this.transactionID = transactionID;
    }

    @JSONField(name = "merchant_id")
    private String merchantID;

    @JSONField(name = "order_no")
    private String orderNo;

    @JSONField(name = "transaction_id")
    private String transactionID;

    public String getMerchantID() {
        return merchantID;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getTransactionID() {
        return transactionID;
    }

}
