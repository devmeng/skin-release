package com.devmeng.skinlow;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.devmeng.skinlow.utils.ObservableImpl;
import com.devmeng.skinlow.utils.SkinPreference;

import java.lang.reflect.Method;
import java.util.List;

public class SkinManager extends ObservableImpl {

    private static volatile SkinManager instance;
    private static Application application;

    private SkinManager() {

    }

    public static SkinManager getInstance() {
        return instance;
    }

    public static SkinManager init(Application application, boolean isAppTypeface, List<String> activities) {
        SkinManager.application = application;
        if (instance == null) {
            synchronized (SkinManager.class) {
                if (instance == null) {
                    instance = new SkinManager();
                    application.registerActivityLifecycleCallbacks(new SkinActivityLifecycle(activities));
                    SkinResources.init(application);
                    SkinPreference.init(application);
                    Constants.IS_APP_TYPEFACE = isAppTypeface;
                }
            }
        }
        return instance;
    }

    @SuppressLint({"DiscouragedPrivateApi", "PrivateApi", "SoonBlockedPrivateApi"})
    public void loadSkin(String skinPath) {
        if (skinPath.isEmpty()) {
            //todo restore skin
            SkinResources.getInstance().restore();
        } else {
            try {

                AssetManager assetManager = AssetManager.class.newInstance();
                /*Class<?> clazzApkAssets = Class.forName("android.content.res.ApkAssets");
                Method loadFromPath = clazzApkAssets.getMethod("loadFromPath", String.class);
                loadFromPath.setAccessible(true);
                loadFromPath.invoke(clazzApkAssets, skinPath);

                Method setApkAssets = AssetManager.class.getDeclaredMethod("setApkAssets", clazzApkAssets, Boolean.class);
                setApkAssets.setAccessible(true);
                setApkAssets.invoke(assetManager, clazzApkAssets, false);*/

                Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
                addAssetPath.setAccessible(true);
                addAssetPath.invoke(assetManager, skinPath);

                Resources resources = application.getResources();

                Resources skinResource = new Resources(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());

                PackageManager packageManager = application.getPackageManager();
                PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES);
                if (packageArchiveInfo == null) {
                    return;
                }
                String skinPkgName = packageArchiveInfo.packageName;
                SkinResources.getInstance().applySkinPackage(skinResource, skinPkgName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setChanged();
        notifyObservers();
        SkinPreference.getInstance().setSkinPath(skinPath);

    }

}
