package com.damai.damaiwechat.pay.alipay;

public interface AlipayResultListener {

    void onSuccess(String orderNo);

    void onFailed(String code);
}
