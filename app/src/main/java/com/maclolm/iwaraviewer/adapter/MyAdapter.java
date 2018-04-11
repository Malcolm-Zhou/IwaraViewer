package com.maclolm.iwaraviewer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maclolm.iwaraviewer.R;
import com.maclolm.iwaraviewer.bean.VideoInfo;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    private ArrayList<VideoInfo> VideoInfos;
    private Context context;
    private LayoutInflater mInflater = null;

    public MyAdapter(ArrayList<VideoInfo> VideoInfos, Context mContext) {
        this.VideoInfos = VideoInfos;
        this.context = mContext;
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

        VideoInfo videoInfo = VideoInfos.get(position);//把newsBeanArrayList中特定位置的NewsBean对象获得
        if (convertView == null) {
            convertView = mInflater.from(context).inflate(R.layout.item_layout, null);
        }
        ImageView iv_img = convertView.findViewById(R.id.img);
        TextView tv_title = convertView.findViewById(R.id.title);
        TextView tv_address = convertView.findViewById(R.id.address);
        TextView tv_view = convertView.findViewById(R.id.view);
        TextView tv_like = convertView.findViewById(R.id.like);
        TextView tv_rate = convertView.findViewById(R.id.rate);

        iv_img.setImageResource(R.drawable.test);
        tv_title.setText(videoInfo.getTitle());
        tv_address.setText(videoInfo.getAddress());
        tv_view.setText(videoInfo.getView());
        tv_like.setText(videoInfo.getLike());
        tv_rate.setText(videoInfo.getRate());
        convertView.setTag(videoInfo);
        return convertView;
    }
}