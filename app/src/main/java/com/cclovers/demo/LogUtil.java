package com.cclovers.demo;

import com.unity3d.player.UnityPlayer;

/**
 * Created by Alex on 18/5/30.
 */
public class LogUtil {

    public static void logError(String message) {
        try {
            UnityPlayer.UnitySendMessage(MainActivity.UNITY_GAMEOBJECT, "LogError", message);
        }
        catch (Exception e) {
        }
    }

    public static void logDebug(String message) {
        try {
            UnityPlayer.UnitySendMessage(MainActivity.UNITY_GAMEOBJECT, "LogDebug", message);
        }
        catch (Exception e) {
        }
    }
}
