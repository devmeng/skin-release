package com.devmeng.skinlow.entities;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.core.widget.TextViewCompat;

import com.devmeng.skinlow.SkinResources;
import com.devmeng.skinlow.SkinWidgetSupport;

import java.util.List;

public class SkinView {

    private View view;
    private List<SkinPair> skinPairList;

    public SkinView(View view, List<SkinPair> skinPairList) {
        this.view = view;
        this.skinPairList = skinPairList;
    }

    public void applySkin(Typeface typeface) {
        applyTypeface(typeface == null ? Typeface.DEFAULT : typeface);
        SkinResources skinResources = SkinResources.getInstance();
        applyWidgetSkin(skinResources);
        for (SkinPair skinPair : skinPairList) {
            String attrName = skinPair.attrName;
            int resId = skinPair.resId;
            Drawable left = null;
            Drawable right = null;
            Drawable top = null;
            Drawable bottom = null;
            switch (attrName) {
                case "background":
                    Object background = skinResources.getBackground(resId);
                    if (background instanceof Integer) {
                        view.setBackgroundColor((Integer) background);
                    } else {
                        ViewCompat.setBackground(view, (Drawable) background);
                    }
                    break;
                case "backgroundTint":
                    ColorStateList colorStateList = skinResources.getColorStateList(resId);
                    ViewCompat.setBackgroundTintList(view, colorStateList);
                    break;
                case "src":
                    Drawable drawable = skinResources.getDrawable(resId);
                    ((ImageView) view).setImageDrawable(drawable);
                    break;
                case "textColor":
                    ((TextView) view).setTextColor(skinResources.getColorStateList(resId));
                    break;
                case "tint":
                    ((ImageView) view).setImageTintList(skinResources.getColorStateList(resId));
                    break;
                case "drawableLeft":
                case "drawableLeftCompat":
                case "drawableStart":
                case "drawableStartCompat":
                    left = skinResources.getDrawable(resId);
                    break;
                case "drawableRight":
                case "drawableRightCompat":
                case "drawableEndCompat":
                case "drawableEnd":
                    right = skinResources.getDrawable(resId);
                    break;
                case "drawableTop":
                case "drawableTopCompat":
                    top = skinResources.getDrawable(resId);
                    break;
                case "drawableBottom":
                case "drawableBottomCompat":
                    bottom = skinResources.getDrawable(resId);
                    break;
                case "drawableTint":
                    TextViewCompat.setCompoundDrawableTintList((TextView) view, skinResources.getColorStateList(resId));
                    break;
                case "skinTypeface":
                    applyTypeface(skinResources.getTypeface(resId));
                    break;
            }
            if (view instanceof TextView) {
                ((TextView) view).setCompoundDrawablesRelativeWithIntrinsicBounds(left, top, right, bottom);
            }

        }

    }

    private void applyTypeface(Typeface typeface) {
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(typeface);
        }
    }

    private void applyWidgetSkin(SkinResources skinResources) {
        if (view instanceof SkinWidgetSupport) {
            ((SkinWidgetSupport) view).applyWidgetSkin(skinResources, skinPairList);
        }
    }

}
