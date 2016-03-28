package com.damai.damaiwechat;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * This class echoes a string called from JavaScript.
 */
public class dmwechat extends CordovaPlugin {

    private UMShareAPI mShareAPI = null;
    private CallbackContext mCallbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        // echo
        if (action.equals("echo")) {
            String message = args.getString(0);
            this.echo(message, callbackContext);
            return true;
        }

        this.mCallbackContext = callbackContext;

        // init
        if (action.equals("init")) {
            //Log.e("MyDemo", "process the req");
            this.init(args, callbackContext);
            return true;
        }

        // login
        if (action.equals("login")) {
            String type = args.getString(0);
            //this.init(args, callbackContext);
            this.login(type, callbackContext);
            return true;
        }

        // share
        if (action.equals("share")) {
            this.share(args, callbackContext);
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
    private void init(JSONArray args, CallbackContext callbackContext) throws JSONException {
        mShareAPI = UMShareAPI.get(cordova.getActivity());
        JSONObject jsonObject = args.getJSONObject(0);
        String sinaKey = String.valueOf(jsonObject.get("sinaKey"));
        String sinaSecret = String.valueOf(jsonObject.get("sinaSecret"));
        String tecentKey = String.valueOf(jsonObject.get("tecentKey"));
        String tecentSecret = String.valueOf(jsonObject.get("tecentSecret"));
        String wechatKey = String.valueOf(jsonObject.get("wechatKey"));
        String wechatSecret = String.valueOf(jsonObject.get("wechatSecret"));
        PlatformConfig.setSinaWeibo(sinaKey, sinaSecret);
        PlatformConfig.setWeixin(wechatKey, wechatSecret);
        PlatformConfig.setQQZone(tecentKey, tecentSecret);
//
//        PlatformConfig.setSinaWeibo("1399888483", "441ed24920f1800de4f0703848070f2d");
//        PlatformConfig.setWeixin("wxa2e2ce8a3180bed1", "7395136f4dd56798032be054c681a97d");
//        PlatformConfig.setQQZone("1105290894", "SbGPuyXmOw4IUGXQ");

    }

    // 认证登录，目前需要接入微博，QQ，微信 type: sina wechat tencent
    private void login(final String type, CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SHARE_MEDIA platform = null;

                if (type.equals("sina")) {
                    platform = SHARE_MEDIA.SINA;
                } else if (type.equals("tencent")) {
                    platform = SHARE_MEDIA.QQ;
                } else if (type.equals("wechat")) {
                    platform = SHARE_MEDIA.WEIXIN;
                } else {
                    return;
                }
                mShareAPI.doOauthVerify(cordova.getActivity(), platform, umAuthListener);
            }
        });
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            //echo(data.toString(), mCallbackContext);
            //Toast.makeText(cordova.getActivity(), "Authorize succeed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            ///echo(t.toString() + "--" + action, mCallbackContext);
            //Toast.makeText(cordova.getActivity(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            //echo("--" + action, mCallbackContext);
            //Toast.makeText(cordova.getActivity(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };


    /**
     * 各大平台分享
     *
     * @param args            分享参数
     * @param callbackContext 回调
     */
    private void share(JSONArray args, CallbackContext callbackContext) {

        // 判断有没有type，有的话直接根据type来进行分享，没有的话，直接分享

        try {
            final String type = args.getString(0);
            JSONObject jsonObject = args.getJSONObject(1);
            final String title = String.valueOf(jsonObject.get("title"));
            final String content = String.valueOf(jsonObject.get("content"));
            final String imgUrl = String.valueOf(jsonObject.get("imgUrl"));
            final String targetUrl = String.valueOf(jsonObject.get("targetUrl"));

            if (TextUtils.isEmpty(type)) {
                cordova.getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        UMImage image = new UMImage(cordova.getActivity(), imgUrl);

                        SHARE_MEDIA platform = null;
                        if (type.equals("sina")) {
                            platform = SHARE_MEDIA.SINA;
                        } else if (type.equals("wx")) {
                            platform = SHARE_MEDIA.WEIXIN;
                        } else if (type.equals("wx_circle")) {
                            platform = SHARE_MEDIA.WEIXIN_CIRCLE;
                        } else if (type.equals("qq")) {
                            platform = SHARE_MEDIA.QQ;
                        } else if (type.equals("qzone")) {
                            platform = SHARE_MEDIA.QZONE;
                        }
                        ShareAction shareAction = new ShareAction(cordova.getActivity());
                        shareAction.setPlatform(platform)
                                .setCallback(umShareListener)
                                .withTitle(title)
                                .withText(content)
                                .withMedia(image);
                        if (TextUtils.isEmpty(targetUrl))
                            shareAction.withTargetUrl(targetUrl);
                        shareAction
                                .share();
                    }
                });

            } else {
                cordova.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]{
                                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA,
                                SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
                        };
                        UMImage image = new UMImage(cordova.getActivity(), "http://www.umeng.com/images/pic/social/integrated_3.png");
                        ShareAction shareAction = new ShareAction(cordova.getActivity());
                        shareAction.setDisplayList(displaylist)
                                .setListenerList(umShareListener)
                                        //.setCallback(umShareListener)
                                .withTitle(title)
                                .withText(content)
                                .withMedia(image);
                        if (TextUtils.isEmpty(targetUrl))
                            shareAction.withTargetUrl(targetUrl);
                        //.setShareboardclickCallback(shareBoardlistener)
                        shareAction.open();

                    }
                });

            }
        } catch (JSONException e) {
            showToast("json参数出错");
            e.printStackTrace();
        }
    }

    private void showToast(final String toastMessage) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(cordova.getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            showToast("分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            showToast("分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            showToast("取消分享");
        }
    };

    /**
     * 高级功能分享 自定义分享按钮
     */
    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            new ShareAction(cordova.getActivity()).setPlatform(share_media).setCallback(umShareListener)
                    .withText("多平台分享")
                    .share();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 微信支付
     */
    private void wechatPay(JSONArray args, CallbackContext callbackContext) {

    }

    /**
     * 支付宝支付
     */
    private void aliPay(JSONArray args, CallbackContext callbackContext) {

    }

    // 云信

}
