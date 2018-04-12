package com.maclolm.iwaraviewer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

public class BitmapCacheUtils {
    public final Context context;
    private final Handler handler;
    /**
     * 手机缓存工具类
     */
    public MemoryCacheUtils memoryCacheUtils;

    /**
     * 网络获取资源工具类
     */
    public NetCacheUtils netCacheUtils;

    public LocalCacheUtils localCacheUtils;

    public BitmapCacheUtils(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        memoryCacheUtils = new MemoryCacheUtils(context);
        localCacheUtils = new LocalCacheUtils(memoryCacheUtils);
        netCacheUtils = new NetCacheUtils(handler, memoryCacheUtils, localCacheUtils);
    }

    /**
     * 根据图片的网络地址获取为内存的bitmap对象
     *
     * @param imageUrl
     * @return
     */
    public Bitmap getBitmap(String imageUrl, int position) {
        //1、内存中获取，最快
        Bitmap bitmap = memoryCacheUtils.getBitmap(imageUrl);
        if (bitmap != null) {
            return bitmap;
        }
        //2、本地存储中，次之
        bitmap = localCacheUtils.getBitmap(imageUrl);
        if (bitmap != null) {
            return bitmap;
        }
        //3、联网获取，最后
        netCacheUtils.getBitmapFromNet(imageUrl, position);
        return null;
    }
}
