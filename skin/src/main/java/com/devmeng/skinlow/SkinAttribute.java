package com.devmeng.skinlow;

import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.devmeng.skinlow.entities.SkinPair;
import com.devmeng.skinlow.entities.SkinView;
import com.devmeng.skinlow.utils.SkinThemeUtils;

import java.util.ArrayList;
import java.util.List;

public class SkinAttribute {

    Typeface typeface;

    private List<String> attributeList = new ArrayList<>();
    private List<SkinView> skinViews = new ArrayList<>();
    private List<String> widgetAttrList = new ArrayList<>();

    SkinAttribute(Typeface typeface) {
        this.typeface = typeface;
        initSkinAttrs();
    }

    private void initSkinAttrs() {
        attributeList.add("background");
        attributeList.add("backgroundTint");
        attributeList.add("src");
        attributeList.add("textColor");
        attributeList.add("tint");
        attributeList.add("drawableLeft");
        attributeList.add("drawableStart");
        attributeList.add("drawableRight");
        attributeList.add("drawableEnd");
        attributeList.add("drawableTop");
        attributeList.add("drawableBottom");
        attributeList.add("drawableLeftCompat");
        attributeList.add("drawableStartCompat");
        attributeList.add("drawableRightCompat");
        attributeList.add("drawableEndCompat");
        attributeList.add("drawableTopCompat");
        attributeList.add("drawableBottomCompat");
        attributeList.add("drawableTint");
        //更改字体时需要在 strings.xml 中定义响应的字体文件 例: font/xx.ttf , 并且在 themes.xml 中为 skinTypeface 赋值
        attributeList.add("skinTypeface");

        /* 注意: attributeList 在增加属性个体时需要对
         #SkinView 中的 applySkin() 方法的 switch 增加 case
         */
    }


    void holdView(View view, AttributeSet attrs) {
        List<SkinPair> skinPairList = new ArrayList<>();

        if (view instanceof SkinWidgetSupport) {
            List<String> attrsList = ((SkinWidgetSupport) view).attrsList;
            widgetAttrList = attrsList;
            attributeList.addAll(attrsList);
        }

        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            String attrName = attrs.getAttributeName(i);
            if (attributeList.contains(attrName)) {
                String attrValue = attrs.getAttributeValue(i);
                if (attrValue.startsWith("#") || (attrValue.contains("x"))
                        || (!attrValue.startsWith("@"))
                ) {
                    //忽略值设置为"#"开头的属性
                    continue;
                }
                int resId = Integer.parseInt(attrValue.substring(1));
                if (attrValue.startsWith("?")) {
                    int attrId = Integer.parseInt(attrValue.substring(1));
                    resId = SkinThemeUtils.getResId(view.getContext(), new int[]{attrId})[0];
                }
                skinPairList.add(new SkinPair(attrName, resId));
            }
        }

        if (skinPairList.size() > 0 || view instanceof TextView || view instanceof SkinWidgetSupport) {
            SkinView skinView = new SkinView(view, skinPairList);
            skinView.applySkin(typeface);
            skinViews.add(skinView);

        }
        if (widgetAttrList.size() > 0) {
            attributeList.removeAll(widgetAttrList);
        }
    }

    void applySkin() {
        for (SkinView skinView : skinViews) {
            skinView.applySkin(typeface);
        }
    }


}
