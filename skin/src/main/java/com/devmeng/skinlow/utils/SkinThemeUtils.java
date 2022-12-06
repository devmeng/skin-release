package com.devmeng.skinlow.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;

import com.devmeng.skinlow.R;
import com.devmeng.skinlow.SkinResources;

import java.lang.ref.WeakReference;

public class SkinThemeUtils {

    private static int[] attrArr = new int[]{R.attr.skinTypeface};
    private static int[] SYSTEM_BAR_ATTRS = new int[]{android.R.attr.statusBarColor, android.R.attr.navigationBarColor};
    private static int[] SYSTEM_BAR_LIGHT_MODE = new int[]{android.R.attr.windowLightStatusBar, android.R.attr.windowLightNavigationBar};

    public static Typeface getSkinTypeface(Activity activity) {
        int typeface = getResId(activity, attrArr)[0];
        return SkinResources.getInstance().getTypeface(typeface);
    }

    public static int[] getResId(Context context, int[] attrs) {
        Context wkContext = new WeakReference<>(context).get();
        int[] resArr = new int[attrs.length];
        TypedArray typedArray = wkContext.obtainStyledAttributes(attrs);
        for (int i = 0; i < typedArray.length(); i++) {
            resArr[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
        return resArr;
    }

    public static void updateStatusBarState(Activity activity) {
        int[] systemBarAttrs = getResId(activity, SYSTEM_BAR_ATTRS);
        int[] systemBarLightMode = getResId(activity, SYSTEM_BAR_LIGHT_MODE);
        if (systemBarAttrs[0] == 0) {
            return;
        }
        activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(systemBarAttrs[0]));
        if (systemBarLightMode[0] == 0) {
            return;
        }
        boolean isLight = SkinResources.getInstance().getBoolean(systemBarLightMode[0]);
        StatusBarUtils.init(activity).setStatusBarState(isLight);
    }

    public static void updateNavigationBarState(Activity activity) {
        int[] systemBarAttrs = getResId(activity, SYSTEM_BAR_ATTRS);
        int[] systemBarLightMode = getResId(activity, SYSTEM_BAR_LIGHT_MODE);
        if (systemBarAttrs[1] == 0) {
            return;
        }
        activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(systemBarAttrs[1]));
        if (systemBarLightMode[1] == 0) {
            return;
        }
        boolean isLight = SkinResources.getInstance().getBoolean(systemBarLightMode[1]);
        StatusBarUtils.init(activity).setNavigationBarState(isLight);
    }
}
