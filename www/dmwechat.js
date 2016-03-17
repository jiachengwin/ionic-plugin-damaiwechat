var exec = require('cordova/exec');

var dmwechat = {

    // 测试输出接口
    echo : function(msg, success, error) {
        exec(success, error, "dmwechat", "echo", [msg]);
    },

    // 初始化APPID KEY 
    // var params = {
    //     wechatKey : "wxa2e2ce8a3180bed1",
    //     wechatSecret : "7395136f4dd56798032be054c681a97d",
    //     sinaKey : "1399888483",
    //     sinaSecret : "441ed24920f1800de4f0703848070f2d",
    //     tecentKey : "1104851815",
    //     tecentSecret : "X7FT5BmwiA5Y7nxp"
    // }
    init: function(params, success, error) {
        exec(success, error, "dmwechat", "init", [params]);
    }

    // login 
    // type:wechat sina tecent
    login: function(type, success, error) {
        exec(success, error, "dmwechat", "login", [type]);
    },

    // 接入支付
    // var params = {
    //     mch_id: '10000100', // merchant id
    //     prepay_id: 'wx201411101639507cbf6ffd8b0779950874', // prepay id returned from server
    //     nonce: '1add1a30ac87aa2db72f57a2375d8fec', // nonce string returned from server
    //     timestamp: '1439531364', // timestamp
    //     sign: '0CB01533B8C1EF103065174F50BCA001', // signed string
    // };
    wechatPay: function(params, success, error) {
        exec(success, error, "dmwechat", "wechatPay", [params]);
    },
    
    aliPay: function(params, success, error) {
        exec(success, error, "dmwechat", "aliPay", [params]);
    }

    // 接入云信

};

window.dmwechat = dmwechat;
