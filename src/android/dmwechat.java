package com.damai.damaiwechat;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class dmwechat extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        // echo
        if (action.equals("echo")) {
            String message = args.getString(0);
            this.echo(message, callbackContext);
            return true;
        }

        // login
        if (action.equals("login")) {
            String message = args.getString(0);
            this.login(message, callbackContext);
            return true;
        }

        // pay
        if (action.equals("wechatPay")) {
            String message = args.getString(0);         // message是字典
            this.wechatPay(message, callbackContext);
            return true;
        }
        if (action.equals("aliPay")) {
            String message = args.getString(0);
            this.aliPay(message, callbackContext);
            return true;
        }

        // 云信


        return false;
    }

    // 输出测试
    private void echo(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    // 认证登录，目前需要接入微博，QQ，微信
    private void login(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    // 接入支付
    private void wechatPay(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void aliPay(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    // 云信

}
