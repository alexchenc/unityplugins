package com.cclovers.demo;

import android.content.Intent;
import android.os.Bundle;
import com.unity3d.player.UnityPlayerActivity;

public class MainActivity extends UnityPlayerActivity {
//public class MainActivity extends Activity {  //单独打包调试apk

    public static final String UNITY_GAMEOBJECT = "AndroidManager";

    public static MainActivity Instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //支付宝沙箱调试时启用
//        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);

        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);   //单独打包调试apk时需要设置
        Instance = this;
    }

    /**
     * 调用相机
     */
    public void openCamera() {
        PhotoService.GetInstance().openCamera();
    }

    /**
     * 调用相册
     */
    public void openAlbum() {
        PhotoService.GetInstance().openAlbum();
    }

    /**
     * 调用支付宝API
     */
    public void openAlipay(String orderStr) {
        AlipayService.GetInstance().openAlipay(orderStr);
    }

    /**
     * 调用微信API
     */
    public void openWechatPay(String orderStr) {
        WechatService.GetInstance().openWechatPay(orderStr);
    }

    /**
     * 调用微信登录
     */
    public void openWechatLogin() {
        WechatService.GetInstance().openWechatLogin();
    }

    /**
     * 回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //调用相机/相册
        PhotoService.GetInstance().onActivityResult(requestCode, resultCode, data);
    }

}
