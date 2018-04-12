package com.maclolm.iwaraviewer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetCacheUtils {
    /**
     * 成功
     */
    public static final int SUCCESS = 1;

    /**
     * 失败
     */
    public static final int FAIL = 2;

    private Handler handler;

    /**
     * 手机缓存工具类
     */
    private MemoryCacheUtils memoryCacheUtils;

    /**
     * 手机本地存储
     */
    private LocalCacheUtils localCacheUtils;

    /**
     * 线程池接口
     */
    private ExecutorService service;

    public NetCacheUtils(Handler handler, MemoryCacheUtils memoryCacheUtils, LocalCacheUtils localCacheUtils) {
        this.handler = handler;
        this.memoryCacheUtils = memoryCacheUtils;
        this.localCacheUtils = localCacheUtils;
        service = Executors.newFixedThreadPool(10);
    }

    /**
     * 线程池的使用
     */
    class MyRunnable implements Runnable {
        private String imageUrl;
        private int position;

        public MyRunnable(String imageUrl, int position) {
            this.position = position;
            this.imageUrl = imageUrl;
        }

        @Override
        public void run() {
            Bitmap bitmap = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");//默认就是get，参数必须大写
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(10000);
                conn.connect();
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    inputStream = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    Message msg = new Message();
                    msg.what = SUCCESS;
                    msg.obj = bitmap;
                    msg.arg1 = position;//显示图片的位置
                    handler.sendMessage(msg);
                    //资源获取成功后，加入到一级和二级缓存中
                    //1、缓存到手机缓存中
                    memoryCacheUtils.putBitmap(imageUrl, bitmap);
                    //2、缓存到手机本地存储中
                    localCacheUtils.putBitmap(imageUrl, bitmap);
                }
                //关闭
                conn.disconnect();
            } catch (Exception e) {
                //如果失败了
                handler.sendEmptyMessage(FAIL);
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 根据 图片的网络地址，获取bitmap对象
     *
     * @param imageUrl
     * @param position
     * @return
     */
    public void getBitmapFromNet(final String imageUrl, final int position) {
        service.execute(new MyRunnable(imageUrl, position));
    }
}
