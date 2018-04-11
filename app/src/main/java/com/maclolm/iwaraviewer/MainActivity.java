package com.maclolm.iwaraviewer;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.maclolm.iwaraviewer.adapter.MyAdapter;
import com.maclolm.iwaraviewer.bean.VideoInfo;
import com.maclolm.iwaraviewer.util.CrawlTool;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText pageNumInput;
    private ArrayList<VideoInfo> list;
    private MyAdapter adapter;
    private Handler handler;
    private ListView listviewsimple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pageNumInput = findViewById(R.id.editTextPageNum);
        Button submitBtn = findViewById(R.id.submitBtn);
        listviewsimple = findViewById(R.id.listviewsimple);


        VideoInfo info3 = new VideoInfo("address", "Title", "101", "imgsrc", "100", "3000", "3.3");
        list = new ArrayList<>();


    }

    public void onSumbit(View view) {
        Toast.makeText(getApplicationContext(), "Page: " + pageNumInput.getText().toString(), Toast.LENGTH_SHORT).show();
        //开一条子线程加载网络数据
        Runnable runnable = new Runnable() {
            public void run() {
                list = CrawlTool.getCrawlData("5");
                handler.sendMessage(handler.obtainMessage(0, list));
            }
        };

        try {
            //开启线程
            new Thread(runnable).start();
            //handler与线程之间的通信及数据处理
            handler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.what == 0) {
                        //msg.obj是获取handler发送信息传来的数据
                        @SuppressWarnings("unchecked")
                        ArrayList<VideoInfo> list = (ArrayList<VideoInfo>) msg.obj;
                        //给ListView绑定数据
                        BinderListData(list);
                    }
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //绑定数据
    public void BinderListData(ArrayList<VideoInfo> list) {
        //创建adapter对象
        adapter = new MyAdapter(list, this, listviewsimple);
        //将Adapter绑定到listview中
        listviewsimple.setAdapter(adapter);
    }

    public void onPlayVideo(View view) {


        TextView tv_addressview = view.findViewById(R.id.address);

        //play(tv_addressview.getText().toString());
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(tv_addressview.getText());
        Toast.makeText(getApplicationContext(), "复制成功", Toast.LENGTH_SHORT).show();
    }

    private void play(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
        mediaIntent.setDataAndType(Uri.parse(url), mimeType);
        startActivity(mediaIntent);
    }





}
