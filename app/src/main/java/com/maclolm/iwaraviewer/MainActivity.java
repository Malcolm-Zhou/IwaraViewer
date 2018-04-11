package com.maclolm.iwaraviewer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.maclolm.iwaraviewer.adapter.MyAdapter;
import com.maclolm.iwaraviewer.bean.VideoInfo;
import com.maclolm.iwaraviewer.util.CrawlTool;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText pageNumInput;
    private Button submitBtn;

    private ListView listviewsimple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pageNumInput = findViewById(R.id.editTextPageNum);
        submitBtn = findViewById(R.id.submitBtn);
        listviewsimple = findViewById(R.id.listviewsimple);


//        String http = "http://i.iwara.tv/sites/default/files/styles/thumbnail/public/video_embed_field_thumbnails/youtube/15Rkgv_UWjc.jpg?itok=qPCONQ9q";
//        ImageView imageView = (ImageView) findViewById(R.id.imageView);
//        //启动异步任务，加载网络图片
//        new LoadImagesTask(imageView).execute(http);

//        ArrayList<VideoInfo> list = CrawlTool.getCrawlData("101");

        ArrayList<VideoInfo> list = new ArrayList<>();
        VideoInfo info1 = new VideoInfo("address", "Test Title Long LongLongLongLongLongLongLongLongLongLongLongLongLongLongLong", "101", "imgsrc", "300", "3000", "10");
        VideoInfo info2 = new VideoInfo("address", "Test Title Short", "101", "imgsrc", "200", "3000", "6.6");
        VideoInfo info3 = new VideoInfo("address", "Test Title", "101", "imgsrc", "100", "3000", "3.3");
        list.add(info1);
        list.add(info2);
        list.add(info3);
        MyAdapter myAdapter = new MyAdapter(list, this);//创建适配器，传参数为NewsBean集合，Context->this，
        listviewsimple.setAdapter(myAdapter);

    }

    public void onSumbit(View view) {
        Toast.makeText(getApplicationContext(), "Page: " + pageNumInput.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    public void onPlayVideo(View view) {


        TextView tv_addressview = view.findViewById(R.id.address);
        Toast.makeText(getApplicationContext(), tv_addressview.getText(), Toast.LENGTH_SHORT).show();
    }

    private void play(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
        mediaIntent.setDataAndType(Uri.parse(url), mimeType);
        startActivity(mediaIntent);
    }


}
