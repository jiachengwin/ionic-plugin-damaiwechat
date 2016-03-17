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

        // init
        // var params = {
        //     wechatKey : "wxa2e2ce8a3180bed1",
        //     wechatSecret : "7395136f4dd56798032be054c681a97d",
        //     sinaKey : "1399888483",
        //     sinaSecret : "441ed24920f1800de4f0703848070f2d",
        //     tecentKey : "1104851815",
        //     tecentSecret : "X7FT5BmwiA5Y7nxp"
        // }
        if (action.equals("init")) {
            this.init(args, callbackContext);
            return true;
        }

        // login
        // type:wechat sina tecent
        // return: args.access_token args.userid
        if (action.equals("login")) {
            String type = args.getString(0);
            this.login(type, callbackContext);
            return true;
        }

        // pay
        if (action.equals("wechatPay")) {
            this.wechatPay(args, callbackContext);
            return true;
        }
        if (action.equals("aliPay")) {
            this.aliPay(args, callbackContext);
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

    // 初始化APPID KEY
    private void init(JSONArray args, CallbackContext callbackContext) {
        // 这里进行初始化处理

    }

    // 认证登录，目前需要接入微博，QQ，微信 type: sina wechat tecent
    private void login(String type, CallbackContext callbackContext) {

    }

    // 接入支付
    private void wechatPay(JSONArray args, CallbackContext callbackContext) {

    }

    private void aliPay(JSONArray args, CallbackContext callbackContext) {

    }

    // 云信

}
