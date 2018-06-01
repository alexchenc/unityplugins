package com.cclovers.demo;

import android.content.Intent;
import android.os.Bundle;
import com.unity3d.player.UnityPlayerActivity;

public class MainActivity extends UnityPlayerActivity {

    public static final String UNITY_GAMEOBJECT = "AndroidManager";

    public static MainActivity Instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //直接打包apk时需要设置下面这一行
//        setContentView(R.layout.activity_main);
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
    }

    /**
     * 调用微信API
     */
    public void openWechat() {

    }

    /**
     * 微信登录接口
     */
    public void openWechatLogin() {
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

        //是否调用相机/相册
        PhotoService.GetInstance().onActivityResult(requestCode, resultCode, data);
    }

}
