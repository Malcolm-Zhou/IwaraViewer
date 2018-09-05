package com.maclolm.iwaraviewer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.maclolm.iwaraviewer.R;
import com.maclolm.iwaraviewer.bean.VideoInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MyAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<VideoInfo> VideoInfos;
    private Context context;
    private ListView mListView;
    private LruCache<String, Bitmap> mMemoryCache;

    public MyAdapter(ArrayList<VideoInfo> VideoInfos, Context mContext, ListView lv, LruCache<String, Bitmap> mMemoryCache) {
        this.VideoInfos = VideoInfos;
        this.context = mContext;
        this.mListView = lv;
        this.mMemoryCache = mMemoryCache;
    }

    @Override
    public int getCount() {
        return this.VideoInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return this.VideoInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_layout, null);
        }
        VideoInfo videoInfo = VideoInfos.get(position);//把newsBeanArrayList中特定位置的NewsBean对象获得
        String http = "http:" + videoInfo.getImgSrc();
        ImageView iv_img = convertView.findViewById(R.id.img);
        iv_img.setImageResource(R.mipmap.test);
        //启动异步任务，加载网络图片
//        loadBitmap(http, iv_img);
        TextView tv_title = convertView.findViewById(R.id.title);
        TextView tv_address = convertView.findViewById(R.id.address);
        TextView tv_view = convertView.findViewById(R.id.view);
        TextView tv_like = convertView.findViewById(R.id.like);
        TextView tv_rate = convertView.findViewById(R.id.rate);

        tv_title.setText(videoInfo.getTitle());
        tv_address.setText(videoInfo.getAddress());
        tv_view.setText(videoInfo.getView());
        tv_like.setText(videoInfo.getLike());
        tv_rate.setText(videoInfo.getRate());
        convertView.setTag(videoInfo);
        return convertView;
    }

    private void loadBitmap(String http, ImageView imageView) {
        final String imageKey = String.valueOf(http);
        imageView.setTag(http);

        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.mipmap.test);
            BitmapWorkerTask task = new BitmapWorkerTask(http);
            task.execute(http);
        }
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        String http;

        BitmapWorkerTask(String http) {
            this.http = http;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            URL imageUrl;
            Bitmap bitmap = null;
            InputStream inputStream;
            try {
                imageUrl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                conn.setDoInput(true);
                conn.setConnectTimeout(10000);
                conn.connect();
                inputStream = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView = mListView.findViewWithTag(http);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }

    }
}