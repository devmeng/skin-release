package com.devmeng.skinlow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.devmeng.skinlow.utils.Log;
import com.devmeng.skinlow.utils.ObservableImpl;
import com.devmeng.skinlow.utils.ObserverImpl;
import com.devmeng.skinlow.utils.SkinThemeUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class SkinFactory implements LayoutInflater.Factory2, ObserverImpl {


    private final Activity activity;

    private HashMap<String, Constructor<? extends View>> viewConMap;
    private SkinAttribute skinAttribute;

    private String[] viewPkgNameArr = new String[]{
            "android.view.",
            "android.widget.",
            "android.webkit.",
    };

    SkinFactory(Activity activity, Typeface skinTypeface) {
        this.activity = activity;
        viewConMap = new HashMap<>();
        skinAttribute = new SkinAttribute(skinTypeface);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View view = createViewFromTag(name, context, attrs);
        if (view == null) {
            view = createView(name, context, attrs);
        }
        skinAttribute.holdView(view, attrs);
        return view;
    }

    private View createViewFromTag(String name, Context context, AttributeSet attrs) {
        View view = null;
        Log.e("view name -> " + name);
        if (!name.contains(".")) {
            for (String pack : viewPkgNameArr) {
                view = createView(pack + name, context, attrs);
                if (view != null) {
                    break;
                }
            }
            return view;
        }
        return createView(name, context, attrs);
    }

    private View createView(String name, Context context, AttributeSet attrs) {
        Log.e("view name2 -> " + name);
        Constructor<? extends View> viewConstructor = viewConMap.get(name);
        try {
            if (viewConstructor == null) {
                Class<? extends View> aClass = context.getClassLoader().loadClass(name).asSubclass(View.class);
                viewConstructor = aClass.getConstructor(Context.class, AttributeSet.class);

                viewConMap.put(name, viewConstructor);
                return viewConstructor.newInstance(context, attrs);
            }
            return viewConstructor.newInstance(context, attrs);

        } catch (Exception ignored) {
        }
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return null;
    }

    @Override
    public void update(ObservableImpl o, Object arg) {
        SkinThemeUtils.updateStatusBarState(activity);
        SkinThemeUtils.updateNavigationBarState(activity);
        skinAttribute.typeface = SkinThemeUtils.getSkinTypeface(activity);
        skinAttribute.applySkin();
    }
}
