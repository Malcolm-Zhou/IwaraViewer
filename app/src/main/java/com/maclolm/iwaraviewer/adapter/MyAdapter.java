package com.maclolm.iwaraviewer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
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
import com.maclolm.iwaraviewer.util.BitmapCacheUtils;
import com.maclolm.iwaraviewer.util.NetCacheUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MyAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<VideoInfo> VideoInfos;
    private Context context;
    private LayoutInflater mInflater = null;
    private ListView mListView;

    public MyAdapter(ArrayList<VideoInfo> VideoInfos, Context mContext, ListView lv) {
        this.VideoInfos = VideoInfos;
        this.context = mContext;
        this.mListView = lv;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NetCacheUtils.SUCCESS://成功
                    Bitmap bitmap = (Bitmap) msg.obj;
                    int position = msg.arg1;
                    ImageView image = (ImageView) mListView.findViewWithTag(position);//注意这行代码
                    image.setImageBitmap(bitmap);
                    break;
                case NetCacheUtils.FAIL://失败
                    break;
            }
        }
    };

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

        VideoInfo videoInfo = VideoInfos.get(position);//把newsBeanArrayList中特定位置的NewsBean对象获得
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_layout, null);
        }
        ImageView iv_img = convertView.findViewById(R.id.img);
        TextView tv_title = convertView.findViewById(R.id.title);
        TextView tv_address = convertView.findViewById(R.id.address);
        TextView tv_view = convertView.findViewById(R.id.view);
        TextView tv_like = convertView.findViewById(R.id.like);
        TextView tv_rate = convertView.findViewById(R.id.rate);


        String http = "http:" + videoInfo.getImgSrc();
        iv_img.setTag(position);
        iv_img.setImageResource(R.color.colorNightBG);
        //启动异步任务，加载网络图片
//        BitmapWorkerTask task = new BitmapWorkerTask(http);
//        task.execute(http);

        BitmapCacheUtils loadImgUtil = new BitmapCacheUtils(context, handler);
        loadImgUtil.getBitmap(http, position);

        tv_title.setText(videoInfo.getTitle());
        tv_address.setText(videoInfo.getAddress());
        tv_view.setText(videoInfo.getView());
        tv_like.setText(videoInfo.getLike());
        tv_rate.setText(videoInfo.getRate());
        convertView.setTag(videoInfo);
        return convertView;
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        String http;

        public BitmapWorkerTask(String http) {
            this.http = http;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            URL imageUrl = null;
            Bitmap bitmap = null;
            InputStream inputStream = null;
            try {
                imageUrl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                conn.setDoInput(true);
                conn.setConnectTimeout(10000);
                conn.connect();
                inputStream = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView = (ImageView) mListView.findViewWithTag(http);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }

    }
}