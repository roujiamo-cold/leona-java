package com.lehuipay.leona.contracts;

import com.lehuipay.leona.exception.LeonaException;
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
import org.apache.http.concurrent.FutureCallback;

import java.io.Closeable;
import java.io.IOException;

public interface Leona extends Closeable {

    // 二维码支付
    QRCodePayResponse qrCodePay(QRCodePayRequest req) throws LeonaException;
    void qrCodePay(QRCodePayRequest req, FutureCallback<QRCodePayResponse> callback) throws LeonaException;

    // 刷卡支付
    MicroPayResponse microPay(MicroPayRequest req) throws LeonaException;
    void microPay(MicroPayRequest req, FutureCallback<MicroPayResponse> callback) throws LeonaException;

    // 查询交易
    GetOrderResponse getOrder(GetOrderRequest req) throws LeonaException;
    void getOrder(GetOrderRequest req, FutureCallback<GetOrderResponse> callback) throws LeonaException;

    // 退款
    RefundResponse refund(RefundRequest req) throws LeonaException;
    void refund(RefundRequest req, FutureCallback<RefundResponse> callback) throws LeonaException;

    // 查询退款
    GetRefundResponse getRefund(GetRefundRequest req) throws LeonaException;
    void getRefund(GetRefundRequest req, FutureCallback<GetRefundResponse> callback) throws LeonaException;
}
