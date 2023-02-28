package com.devmeng.skinlow;

import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.devmeng.skinlow.utils.Log;
import com.devmeng.skinlow.utils.SkinPreference;
import com.devmeng.skinlow.utils.SkinThemeUtils;

import java.util.HashMap;
import java.util.List;

public class SkinActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    private HashMap<Activity, SkinFactory> factoryMap = new HashMap<>();
    private List<String> activities;

    SkinActivityLifecycle(List<String> activities) {
        this.activities = activities;
    }

    @Override
    public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Log.d("activity getShortClassName -> " + activity.getComponentName().getShortClassName());
        if (skinForSingle(activity)) {
            return;
        }
        Typeface skinTypeface = null;
        LayoutInflater inflater = LayoutInflater.from(activity);
        if (Constants.IS_APP_TYPEFACE) {
            try {
                skinTypeface = SkinThemeUtils.getSkinTypeface(activity);
            } catch (Exception e) {
                Log.e(e.getMessage());
            }
        }
        SkinFactory skinFactory = new SkinFactory(activity, skinTypeface);
        inflater.setFactory2(skinFactory);
        SkinManager.getInstance().addObserver(skinFactory);
        factoryMap.put(activity, skinFactory);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (skinForSingle(activity)) {
            return;
        }
        //防止重启状态还原
        SkinThemeUtils.updateStatusBarState(activity);

        SkinManager.getInstance().loadSkin(SkinPreference.getInstance().getSkinPath());

        Log.d("执行 -> onActivityResumed");
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (skinForSingle(activity)) {
            return;
        }
        SkinFactory skinFactory = factoryMap.remove(activity);
        SkinManager.getInstance().deleteObserver(skinFactory);
    }

    private boolean skinForSingle(Activity activity) {
        if (activities == null) {
            return false;
        }
        if (activities.size() > 0 && !activities.contains(activity.getComponentName().getShortClassName().substring(1))) {
            Log.d("注销");
            return true;
        }
        return false;
    }

}
