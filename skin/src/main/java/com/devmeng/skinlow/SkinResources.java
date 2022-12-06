package com.devmeng.skinlow;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.devmeng.skinlow.utils.Log;

public class SkinResources {

    private static volatile SkinResources instance;
    public Resources skinResources;
    private Context context;
    private String pkgName;
    private boolean isDefaultSkin = true;

    private SkinResources() {
    }

    private Context getContext() {
        return getInstance().context;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    public static SkinResources getInstance() {
        return instance;
    }

    public static SkinResources init(Context context) {
        if (instance == null) {
            synchronized (SkinResources.class) {
                if (instance == null) {
                    instance = new SkinResources();
                    instance.setContext(context);
                }
            }
        }
        return instance;
    }

    public void applySkinPackage(Resources skinResources, String packageName) {
        this.skinResources = skinResources;
        pkgName = packageName;
        isDefaultSkin = packageName.isEmpty();
    }

    public void restore() {
        skinResources = null;
        pkgName = Constants.EMPTY;
        isDefaultSkin = true;
    }

    public int getColor(int resId) {
        return getColor(context.getResources(), resId);
    }

    public int getColor(Resources resources, int resId) {
        int skinRes = getIdentifierFromRes(resources, resId);
        if (isDefaultSkin || skinRes == 0) {
            return context.getColor(resId);
        }
        int color;

        try {
            color = skinResources.getColor(skinRes, null);
        } catch (Exception e) {
            color = context.getColor(skinRes);
        }
        return color;
    }

    public ColorStateList getColorStateList(int resId) {
        return getColorStateList(context.getResources(), resId);
    }

    public ColorStateList getColorStateList(Resources resources, int resId) {
        int skinRes = getIdentifierFromRes(resources, resId);
        if (isDefaultSkin || skinRes == 0) {
            return context.getColorStateList(resId);
        }
        ColorStateList colorStateList;
        try {
            colorStateList = skinResources.getColorStateList(skinRes, null);
        } catch (Exception e) {
            colorStateList = context.getColorStateList(skinRes);
        }
        return colorStateList;
    }

    public Drawable getDrawable(int resId) {
        return getDrawable(context.getResources(), resId);
    }

    public Drawable getDrawable(Resources resources, int resId) {
        int skinRes = getIdentifierFromRes(resources, resId);

        Drawable drawable;
        try {
            drawable = skinResources.getDrawable(skinRes, null);
        } catch (Exception e) {
            drawable = context.getDrawable(skinRes);
        }
        return drawable;

    }

    public int getDrawableId(int resId) {
        return getDrawableId(context.getResources(), resId);
    }

    public int getDrawableId(Resources resources, int resId) {
        int skinRes = getIdentifierFromRes(resources, resId);
        if (isDefaultSkin || skinRes == 0) {
            return resId;
        }
        return skinRes;
    }

    public Object getBackground(int resId) {
        return getBackground(context.getResources(), resId);
    }

    public Object getBackground(Resources resources, int resId) {
        String typeName = resources.getResourceTypeName(resId);
        Object background;
        if ("color".equals(typeName)) {
            background = getColor(resources, resId);
        } else {
            background = getDrawable(resources, resId);
        }
        return background;
    }

    public boolean getBoolean(int resId) {
        return getBoolean(context.getResources(), resId);
    }

    public boolean getBoolean(Resources resources, int resId) {
        int skinRes = getIdentifierFromRes(resources, resId);
        if (isDefaultSkin || skinRes == 0) {
            return context.getResources().getBoolean(resId);
        }
        boolean bool;
        try {
            bool = skinResources.getBoolean(skinRes);
        } catch (Exception e) {
            bool = context.getResources().getBoolean(skinRes);
        }
        return bool;
    }

    public Typeface getTypeface(int resId) {
        return getTypeface(context.getResources(), resId);
    }

    public Typeface getTypeface(Resources resources, int resId) {
        String typefacePath = getTypefaceString(resources, resId);
        if (typefacePath.isEmpty()) {
            return Typeface.DEFAULT;
        }
        try {
            if (isDefaultSkin) {
                //如果是默认皮肤，且字体路径不为空时创建字体
                // （即：app -> value.xml / value-night.xml 中 skin_ttf_* 的参数不为空）
                return Typeface.createFromAsset(context.getResources().getAssets(), typefacePath);
            }
            return Typeface.createFromAsset(skinResources.getAssets(), typefacePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Typeface.DEFAULT;
    }

    private String getTypefaceString(Resources resources, int resId) {
        if (resId == 0) {
            throw new Resources.NotFoundException("请在 themes 中以 strings 资源的方式引用字体文件");
        }
        if (isDefaultSkin) {
            return context.getResources().getString(resId);
        }
        int skinRes = getIdentifierFromRes(resources, resId);
        String strTypeface;
        if (skinRes == 0) {
            return context.getResources().getString(resId);
        }
        try {
            strTypeface = skinResources.getString(skinRes);
        } catch (Exception e) {
            strTypeface = context.getString(skinRes);
        }
        return strTypeface;
    }

    private int getIdentifierFromRes(Resources resources, int resId) {
        if (isDefaultSkin) {
            return resId;
        }
        String typeName = resources.getResourceTypeName(resId);
        String entryName = resources.getResourceEntryName(resId);

        String defaultTypeName = context.getResources().getResourceTypeName(resId);
        String defaultEntryName = context.getResources().getResourceEntryName(resId);
        Log.d("defaultTypeName -> " + defaultTypeName);
        Log.d("defaultEntryName -> " + defaultEntryName);


        int skinIdentifier = skinResources.getIdentifier(entryName, typeName, pkgName);
        Log.d("skin identifier -> " + skinIdentifier);
        return skinIdentifier;
    }


}
