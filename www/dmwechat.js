var exec = require('cordova/exec');

var dmwechat = {

    // 测试输出接口
    echo : function(msg, success, error) {
        exec(success, error, "dmwechat", "echo", [msg]);
    },

    // 接入认证登录,输入登录类型,返回登录结果
    login: function(logintype, success, error) {
        exec(success, error, "dmwechat", "login", [logintype]);
    },

    // 接入支付
    /**
     * var params = {
     *     mch_id: '10000100', // merchant id
     *     prepay_id: 'wx201411101639507cbf6ffd8b0779950874', // prepay id returned from server
     *     nonce: '1add1a30ac87aa2db72f57a2375d8fec', // nonce string returned from server
     *     timestamp: '1439531364', // timestamp
     *     sign: '0CB01533B8C1EF103065174F50BCA001', // signed string
     * };
     **/
    wechatPay: function(params, success, error) {
        exec(success, error, "dmwechat", "wechatPay", [arg0]);
    },
    aliPay: function(arg0, success, error) {
        exec(success, error, "dmwechat", "aliPay", [arg0]);
    }

    // 接入云信

};

window.dmwechat = dmwechat;
