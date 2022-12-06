package com.devmeng.skinlow;

import com.devmeng.skinlow.entities.SkinPair;

import java.util.ArrayList;
import java.util.List;

public interface SkinWidgetSupport {

    List<String> attrsList = new ArrayList<>();

    void applyWidgetSkin(SkinResources skinResources, List<SkinPair> skinPairList);

}
