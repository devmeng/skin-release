package com.devmeng.skinlow.utils;

public class Log {

    private static boolean BUILD_TYPE = true;

    private static String TAG = "SKIN";

    public static void d(String msg) {
        if (BUILD_TYPE) {
            android.util.Log.d(TAG, "msg -> " + msg);
        }
    }

    public static void e(String msg) {
        if (BUILD_TYPE) {
            android.util.Log.e(TAG, "error msg -> " + msg);
        }
    }

    public static void w(String msg) {
        if (BUILD_TYPE) {
            android.util.Log.e(TAG, "warning msg -> " + msg);
        }
    }

}
