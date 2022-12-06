package com.devmeng.skinlow.utils;

import android.app.Activity;
import android.view.View;

import java.lang.ref.WeakReference;

public class StatusBarUtils {

    private static volatile StatusBarUtils instance;
    private Activity activity;

    private StatusBarUtils() {
    }

    public static StatusBarUtils getInstance() {
        return instance;
    }

    public static StatusBarUtils init(Activity activity) {
        if (instance == null) {
            synchronized (StatusBarUtils.class) {
                if (instance == null) {
                    instance = new StatusBarUtils();
                    Activity weakActivity = new WeakReference<>(activity).get();
                    instance.activity = weakActivity;
                }
            }
        }
        return instance;
    }

    public void setStatusBarState(boolean isLight) {
        if (isLight) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            activity.getWindow().clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    public void setNavigationBarState(boolean isLight) {
        if (isLight) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        } else {
            activity.getWindow().clearFlags(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }


}
