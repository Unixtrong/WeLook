package com.aloong.welook.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by aloong on 2017/8/6.
 */

public class CodeUtils {
    public static String readIS(InputStream inputStream) {
        byte[] buffer = new byte[1024];
        int len;
        String result = "";
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                result += new String(buffer, 0, len);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
        Tools.closeStream(inputStream);
        }
        return null;
    }
}