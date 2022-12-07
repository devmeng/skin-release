package com.devmeng.skinlow.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.devmeng.skinlow.Constants.EMPTY;
import static com.devmeng.skinlow.Constants.MD5;

public class MD5Utils {

    public static String getMD5Code(String info) {
        String md5 = EMPTY;
        try {
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(info.getBytes());
            byte[] encryption = digest.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : encryption) {
                if (Integer.toHexString(0xff & b).length() == 1) {
                    stringBuilder.append("0").append(Integer.toHexString(0xff & b));
                } else {
                    stringBuilder.append(Integer.toHexString(0xff & b));
                }
            }
            md5 = stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static String md5ForFile(File file) {
        int bufferSize = 10240;
        FileInputStream fis = null;
        byte[] bytes = new byte[bufferSize];
        try {
            MessageDigest digest = MessageDigest.getInstance(MD5);
            fis = new FileInputStream(file);
            int length = 0;
            while ((length = fis.read(bytes)) != -1) {
                digest.update(bytes, 0, length);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert fis != null;
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return byte2HexString(bytes);
    }

    private static String byte2HexString(byte[] bytes) {
        if (bytes.length == 0) {
            return null;
        }
        StringBuilder hexStr = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xff);
            if (hex.length() == 1) {
                hexStr.append(0);
            }
            hexStr.append(hex);
        }
        return hexStr.substring(0, 16);
    }

}
