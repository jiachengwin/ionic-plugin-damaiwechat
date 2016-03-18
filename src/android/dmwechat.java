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
        if (action.equals("init")) {
            this.init(args, callbackContext);
            return true;
        }

        // login
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
    private void init(JSONObject args, CallbackContext callbackContext) {
        // 这里进行初始化处理
        PlatformConfig.setWeixin(args.wechatKey, args.wechatSecret);
        PlatformConfig.setSinaWeibo(args.sinaKey, args.sinaSecret);
        PlatformConfig.setQQZone(args.tecentKey, args.tecentSecret); 
    }

    // 认证登录，目前需要接入微博，QQ，微信 type: sina wechat tecent
    private void login(String type, CallbackContext callbackContext) {
        if (type == "sina") {
            SHARE_MEDIA platform = SHARE_MEDIA.SINA; 
        } else if (type == "tecent") {

        } else if (type == "wechat") {

        } else {
            return；
        }
        mShareAPI.doOauthVerify(this, platform, umAuthListener)
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Toast.makeText( getApplicationContext(), "Authorize succeed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText( getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText( getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };

    // 各大平台分享
    private void share(JSONArray args, CallbackContext callbackContext) {

        // 判断有没有type，有的话直接根据type来进行分享，没有的话，直接分享

        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                        {
                            SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.SINA,
                            SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,SHARE_MEDIA.DOUBAN
                        };
                new ShareAction(this).setDisplayList( displaylist )
                        .withText( "呵呵" )
                        .withTitle("title")
                        .withTargetUrl("http://www.baidu.com")
                        .withMedia( image )
                        .setListenerList(umShareListener,umShareListener)
                        .setShareboardclickCallback(shareBoardlistener)
                        .open();
    }

    new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(ShareActivity.this,platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ShareActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(ShareActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    // 接入支付
    private void wechatPay(JSONArray args, CallbackContext callbackContext) {

    }

    private void aliPay(JSONArray args, CallbackContext callbackContext) {

    }

    // 云信


}
