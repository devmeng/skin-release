package com.devmeng.skinlow.entities;

import java.io.File;

public class Skin {

    private File file;

    public String skinPath;

    public String md5;

    public String name;

    public String skinUrl;

    public Skin(String md5, String name, String skinUrl) {
        this.md5 = md5;
        this.name = name;
        this.skinUrl = skinUrl;
    }

    public File getSkinFile(File file) {
        file = new File(file, name);
        skinPath = file.getAbsolutePath();
        return file;
    }
}
