package com.cclovers.demo;

import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.unity3d.player.UnityPlayer;

public class WechatService {

    public static final String APP_ID = "wx88888888";

    private static WechatService instance;
    private WechatService() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(MainActivity.Instance, APP_ID, false);
        api.registerApp(APP_ID);// 将该app注册到微信
    }

    public static WechatService GetInstance() {
        if (instance == null) {
            instance = new WechatService();
        }
        return instance;
    }

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    /**
     * 打开微信登录接口
     */
    public void openWechatLogin() {
        // send oauth request
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk";
        api.sendReq(req);
    }

    /**
     * 拉起微信支付界面
     */
    public void openWechatPay(String orderStr) {
        try {
            JSONObject obj = (JSONObject)JSON.parse(orderStr);
            if (obj != null) {
                //获取参数，根据服务器返回json数据解析
                PayReq request = new PayReq();
                request.appId = obj.getString("appid");
                request.partnerId = obj.getString("mch_id");    //商户ID
                request.prepayId = obj.getString("prepay_id");  //预支付订单
                request.nonceStr = obj.getString("nonce_str");  //异步通知url
                request.timeStamp = obj.getString("timestamp");
                request.packageValue = "Sign=WXPay";
                request.sign = obj.getString("wxSign"); //签名由服务器加签后返回，无需客户端处理

                //发起支付请求
                api.sendReq(request);
            }
        }
        catch (Exception e) {
            LogUtil.logError(e.getMessage());
        }
    }

    /**
     * 授权回调,获取code后发往服务端，获取access_token
     * @param resp
     */
    public void onSendAuthResp(BaseResp resp) {

        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
            SendAuth.Resp authResp = (SendAuth.Resp) resp;
            UnityPlayer.UnitySendMessage(MainActivity.UNITY_GAMEOBJECT, "ReturnWechatAuth", authResp.code);
        }
        else {
            Toast.makeText(MainActivity.Instance, "授权失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 微信支付结果回调
     * @param resp
     */
    public void onSendPayResp(BaseResp resp) {
        //这里返回的结果只用于通知，实际是否支付成功取决于服务器
        boolean success = resp.errCode == BaseResp.ErrCode.ERR_OK;
        if (success) {
            Toast.makeText(MainActivity.Instance, "支付成功", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(MainActivity.Instance, "支付失败", Toast.LENGTH_SHORT).show();
        }
        UnityPlayer.UnitySendMessage(MainActivity.UNITY_GAMEOBJECT, "ReturnWechatPay", success ? "1" : "0");
    }



}
