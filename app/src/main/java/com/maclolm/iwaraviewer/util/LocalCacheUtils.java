package com.maclolm.iwaraviewer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LocalCacheUtils {
    /**
     * 手机缓存工具类
     */
    private MemoryCacheUtils memoryCacheUtils;

    public LocalCacheUtils(MemoryCacheUtils memoryCacheUtils) {
        this.memoryCacheUtils = memoryCacheUtils;
    }

    /**
     * 获取bitmap文件
     *
     * @param imageUrl
     * @return
     */
    public Bitmap getBitmap(String imageUrl) {
        String fileName = md5(imageUrl);
        File file = new File(Environment.getExternalStorageDirectory() + "/xinxinnews", fileName);
        try {
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                fis.close();
                //添加到手机缓存中
                memoryCacheUtils.putBitmap(imageUrl, bitmap);
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存一个bitmap对象到手机本地存储中
     *
     * @param imageUrl
     * @param bitmap
     */
    public void putBitmap(String imageUrl, Bitmap bitmap) {
        //对图片路径进行md5加密，作为文件名，因为这样，长度一样切唯一
        String fileName = md5(imageUrl);
        File file = new File(Environment.getExternalStorageDirectory() + "/xinxinnews", fileName);
        try {
            File parentFile = file.getParentFile();//获取图片路径
            if (!parentFile.exists()) {//如果该文件路径不存在
                parentFile.mkdirs();//创建
            }
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();//刷新
            fos.close();//关闭
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
