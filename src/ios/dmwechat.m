/********* dmwechat.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import "UMSocial.h"
#import "UMSocialWechatHandler.h"
#import "UMSocialQQHandler.h"
#import "UMSocialSinaSSOHandler.h"

// pay
#import <AlipaySDK/AlipaySDK.h>
#import "Order.h"
#import "WXApi.h"
#import "WXApiObject.h"

@interface dmwechat : CDVPlugin <UMSocialUIDelegate, WXApiDelegate>

@property (nonatomic, strong) NSString *payCallBackId;

- (void)echo:(CDVInvokedUrlCommand*)command;
- (void)init:(CDVInvokedUrlCommand*)command;
- (void)login:(CDVInvokedUrlCommand*)command;
- (void)wechatPay:(CDVInvokedUrlCommand*)command;
- (void)aliPay:(CDVInvokedUrlCommand*)command;

@end

@implementation dmwechat

- (void)echo:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* message = [command.arguments objectAtIndex:0];

    if (message != nil && [message length] > 0) {
        message = @"来自iOS的消息......";
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:message];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

// 目前只接入了三种平台的认证登陆
#define UMKey @"56ea4f1867e58e4242001361"
- (void)init:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"success"];
    NSDictionary* params = [command.arguments objectAtIndex:0];
    if (params) {
        //设置友盟社会化组件appkey,如果要修改的话，手动改插件...包括下面的回调URL...
        [UMSocialData setAppKey:UMKey];
        
        //设置微信AppId，设置分享url，默认使用友盟的网址
        if (params[@"wechatKey"] && params[@"wechatSecret"]) {
            [UMSocialWechatHandler setWXAppId:params[@"wechatKey"] appSecret:params[@"wechatSecret"] url:@"http://www.umeng.com/social"];
        }
        
        // 打开新浪微博的SSO开关
        // 将在新浪微博注册的应用appkey、redirectURL替换下面参数，并在info.plist的URL Scheme中相应添加wb+appkey，如"wb3921700954"，详情请参考官方文档。
        if (params[@"sinaKey"] && params[@"sinaSecret"]) {
            [UMSocialSinaSSOHandler openNewSinaSSOWithAppKey:params[@"sinaKey"]
                                                      secret:params[@"sinaSecret"]
                                                 RedirectURL:@"http://sns.whalecloud.com/sina2/callback"];
        }
        
        //    //设置分享到QQ空间的应用Id，和分享url 链接
        if (params[@"tecentKey"] && params[@"tecentSecret"]) {
            [UMSocialQQHandler setQQWithAppId:params[@"tecentKey"]
                                       appKey:params[@"tecentKey"]
                                          url:@"http://www.umeng.com/social"];
            [UMSocialQQHandler setSupportWebView:YES];
        }

    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)login:(CDVInvokedUrlCommand*)command
{
    NSString* platformName = [command.arguments objectAtIndex:0];
    if ([platformName isEqualToString:@"wechat"]) {
        platformName = @"wxsession";
    } else if ([platformName isEqualToString:@"tencent"]) {
        platformName = @"qq";
    } else if ([platformName isEqualToString:@"sina"]) {
        platformName = @"sina";
    } else {
        platformName = @"";
    }
    if (![platformName isEqualToString:@""]) {
        [UMSocialControllerService defaultControllerService].socialUIDelegate = self;
        UMSocialSnsPlatform *snsPlatform = [UMSocialSnsPlatformManager getSocialPlatformWithName:platformName];
        snsPlatform.loginClickHandler(self.viewController, [UMSocialControllerService defaultControllerService],YES,^(UMSocialResponseEntity *response){
            // 获取微博用户名、uid、token等
            if (response.responseCode == UMSResponseCodeSuccess) {
                UMSocialAccountEntity *snsAccount = [[UMSocialAccountManager socialAccountDictionary] valueForKey:platformName];
                NSDictionary *res = @{@"access_token":snsAccount.accessToken,
                        @"userid":snsAccount.usid,
                        @"username":snsAccount.userName,
                        @"icon":snsAccount.iconURL};
                CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:res];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }
            // 这里可以获取到腾讯微博openid,Qzone的token等
            if ([platformName isEqualToString:UMShareToTencent]) {
                [[UMSocialDataService defaultDataService] requestSnsInformation:UMShareToTencent completion:^(UMSocialResponseEntity *respose){
                    NSDictionary *res = @{@"access_token": response};
                    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:res];
                    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                }];
            }
        });
        
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"选择平台出错了"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void)share:(CDVInvokedUrlCommand*)command
{
    NSString* platformName = [command.arguments objectAtIndex:0];
    NSDictionary* data = [command.arguments objectAtIndex:1];
    
    NSString *shareText = data[@"content"];
    NSURL *url = [NSURL URLWithString:data[@"imgUrl"]];
    UIImage *shareImage = [UIImage imageWithData: [NSData dataWithContentsOfURL:url]];
    if ([platformName isEqualToString:@""]) {
        // 调用快速分享接口
        [UMSocialSnsService presentSnsIconSheetView:self.viewController
                                             appKey:UMKey
                                          shareText:shareText
                                         shareImage:shareImage
                                    shareToSnsNames:@[UMShareToSina, UMShareToTencent, UMShareToWechatSession, UMShareToWechatTimeline, UMShareToQQ]
                                           delegate:self];
    } else {
        // 直接调用制定分享平台
        if ([platformName isEqualToString:@"wechat"]) {
            platformName = @"wxsession";
        } else if ([platformName isEqualToString:@"tencent"]) {
            platformName = @"qq";
        } else if ([platformName isEqualToString:@"sina"]) {
            platformName = @"sina";
        } else {
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"选择平台出错了"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            return;
        }
        [[UMSocialDataService defaultDataService] postSNSWithTypes:@[platformName] content:shareText image:shareImage location:nil urlResource:nil presentedController:self.viewController completion:^(UMSocialResponseEntity * response){
            if (response.responseCode == UMSResponseCodeSuccess) {
                CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"分享成功了"];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            } else if(response.responseCode != UMSResponseCodeCancel) {
                CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"分享失败了"];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }
        }];
    }
}

- (void)aliPay:(CDVInvokedUrlCommand*)command
{
    // 支付宝，支付宝需要添加修改配置APP info type
    NSString* requestParams = [command.arguments objectAtIndex:0];
    NSString *appScheme = @"com.github.doctor";
    
    [[AlipaySDK defaultService] payOrder:requestParams fromScheme:appScheme callback:^(NSDictionary *resultDic) {
        if ([resultDic[@"result"] componentsSeparatedByString:@"TRADE_SUCCESS"].count > 1) {
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"支付成功了"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        } else {
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:resultDic];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }
    }];
}

#pragma mark wechat pay delegate

- (void)wechatPay:(CDVInvokedUrlCommand*)command
{
    // 微信支付
    NSDictionary* dict = [command.arguments objectAtIndex:0];
    PayReq* req             = [[PayReq alloc] init];
    req.partnerId           = [dict objectForKey:@"partnerid"];
    req.prepayId            = [dict objectForKey:@"prepayid"];
    req.nonceStr            = [dict objectForKey:@"noncestr"];
    req.timeStamp           = [[dict objectForKey:@"timestamp"] intValue];
    req.package             = [dict objectForKey:@"package"];
    req.sign                = [dict objectForKey:@"sign"];
    [WXApi sendReq:req];
    self.payCallBackId = command.callbackId;
}

- (void)onResp:(BaseResp *)resp {
    if ([resp isKindOfClass:[PayResp class]]) {
        if (self.payCallBackId) {
            if (resp.errCode == WXSuccess) {
                // 支付成功
                CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"支付成功了"];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:self.payCallBackId];
            } else {
                // 支付失败
                CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"支付失败了"];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:self.payCallBackId];
            }
        }
    }
}


#pragma mark overwrite the openURL from ionic

- (void)handleOpenURL:(NSNotification *)notification {
    BOOL result = [UMSocialSnsService handleOpenURL:notification.object];
    if (result == FALSE) {
        //跳转支付宝钱包进行支付，处理支付结果
        [[AlipaySDK defaultService] processOrderWithPaymentResult:notification.object standbyCallback:^(NSDictionary *resultDic) {}];
    }
    
    [WXApi handleOpenURL:notification.object delegate:self];
}

@end
