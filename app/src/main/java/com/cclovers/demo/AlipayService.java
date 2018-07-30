package com.cclovers.demo;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;
import com.alipay.sdk.app.PayTask;
import com.unity3d.player.UnityPlayer;

import java.util.Map;

public class AlipayService {

    private static final int SDK_PAY_FLAG = 1;

    private static AlipayService instance;
    private AlipayService() {}

    public static AlipayService GetInstance() {
        if (instance == null) {
            instance = new AlipayService();
        }
        return instance;
    }

    public void openAlipay(String orderStr) {
        final String orderInfo = orderStr;

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(MainActivity.Instance);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        //必须异步执行
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */

                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    boolean success = TextUtils.equals(resultStatus, "9000");

                    // 判断resultStatus 为9000则代表支付成功
                    if (success) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(MainActivity.Instance, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(MainActivity.Instance, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    UnityPlayer.UnitySendMessage(MainActivity.UNITY_GAMEOBJECT, "ReturnAlipay", success ? "1" : "0");

                    break;
                }
                default:
                    break;
            }
        };
    };
}
