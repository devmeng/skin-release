package com.devmeng.skinlow;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.devmeng.skinlow.entities.Skin;
import com.devmeng.skinlow.utils.ObservableImpl;
import com.devmeng.skinlow.utils.SkinPreference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.devmeng.skinlow.utils.MD5Utils.md5ForFile;

public class SkinManager extends ObservableImpl {

    private static volatile SkinManager instance;
    private static Application application;
    private ICallBack skinDownloadCallback;

    private SkinManager() {

    }

    public static SkinManager getInstance() {
        return instance;
    }

    public static SkinManager init(Application application) {
        return init(application, false, null, true);
    }

    public static SkinManager init(Application application, boolean isAppTypeface) {
        return init(application, isAppTypeface, null, true);
    }

    public static SkinManager init(Application application, boolean isAppTypeface, boolean isDebug) {
        return init(application, isAppTypeface, null, isDebug);
    }

    public static SkinManager init(Application application, boolean isAppTypeface, List<String> activities, boolean isDebug) {
        SkinManager.application = application;
        if (instance == null) {
            synchronized (SkinManager.class) {
                if (instance == null) {
                    instance = new SkinManager();
                    application.registerActivityLifecycleCallbacks(new SkinActivityLifecycle(activities));
                    SkinResources.init(application);
                    SkinPreference.init(application);
                    Constants.IS_DEBUG = isDebug;
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

    public void loadSkin(Skin skin) {
        loadSkin(skin, Constants.EMPTY);
    }

    public void loadSkin(Skin skin, String md5) {
        File skins = new File(application.getApplicationContext().getFilesDir(), "skins");
        if (skins.exists() && skins.isFile()) {
            skins.delete();
        }
        skins.mkdir();
        File skinFile = skin.getSkinFile(skins);
        String cmd5 = md5;
        if (skin.md5.length() > 0 && cmd5.isEmpty()) {
            cmd5 = md5ForFile(skinFile);
        }
        if (skinFile.exists()) {
            if (skin.md5.equals(cmd5)) {
                loadSkin(skin.skinPath);
            } else {
                throw new IllegalArgumentException("皮肤包已存在，但 MD5 值不匹配，请检查 MD5 计算规则是否相同");
            }
            return;
        }
        File tempFile = new File(skinFile.getParentFile(), skin.name + ".temp");

        Request request = new Request.Builder().url(skin.skinUrl).build();
        skinDownloadCallback = new ICallBack(SkinManager.this, skinFile, tempFile, skin, cmd5);
        new OkHttpClient.Builder().build().newCall(request).enqueue(skinDownloadCallback);

    }

    static class ICallBack implements Callback {
        private SkinManager skinManager;
        private File skinFile;
        private File tempFile;
        private final Skin skin;
        private final String cmd5;

        public ICallBack(SkinManager skinManager, File skinFile, File tempFile, Skin skin, String cmd5) {
            this.skinManager = skinManager;
            this.skinFile = skinFile;
            this.tempFile = tempFile;
            this.skin = skin;
            this.cmd5 = cmd5;
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            e.printStackTrace();
            skinManager.skinDownloadCallback = null;
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) {
            InputStream is = Objects.requireNonNull(response.body()).byteStream();
            FileOutputStream fileOut = null;
            try {
                fileOut = new FileOutputStream(tempFile);
                int length = 0;
                byte[] bytes = new byte[1024];
                while ((length = is.read(bytes)) > 0) {
                    fileOut.write(bytes, 0, length);
                }

                if (skin.md5.equals(cmd5)) {
                    tempFile.renameTo(skinFile);
                } else {
                    throw new IllegalArgumentException("MD5 值不匹配，请检查 MD5 计算规则是否相同");
                }
                skinManager.loadSkin(skin.skinPath);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileOut != null) {
                        fileOut.close();
                    }
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                skinManager.skinDownloadCallback = null;
            }
        }

    }

}
