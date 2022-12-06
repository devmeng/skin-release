package com.devmeng.skinlow.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.devmeng.skinlow.Constants;

import java.lang.ref.WeakReference;

public class SkinPreference {

    private Context context;
    private final String SP_SKIN = "sp_skins";
    private final String SP_SKIN_PATH = "skins_path";

    private SkinPreference() {
    }

    private static SkinPreference instance;

    public static SkinPreference getInstance() {
        return instance;
    }

    public Context getContext() {
        return context;
    }

    private void setContext(Context context) {
        this.context = new WeakReference<>(context).get();
    }

    public static SkinPreference init(Context context) {
        if (instance == null) {
            synchronized (SkinPreference.class) {
                if (instance == null) {
                    instance = new SkinPreference();
                    instance.setContext(context);
                }
            }
        }
        return instance;
    }

    public String getSkinPath() {
        SharedPreferences sp = context.getSharedPreferences(SP_SKIN, Context.MODE_PRIVATE);
        return sp.getString(SP_SKIN_PATH, Constants.EMPTY);
    }

    public void setSkinPath(String skinPath) {
        SharedPreferences sp = context.getSharedPreferences(SP_SKIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SP_SKIN_PATH, skinPath);
        editor.apply();
    }
}
