package com.maclolm.iwaraviewer;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.LruCache;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
    private ListView listviewsimple;
    private Spinner resSpinner;
    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pageNumInput = findViewById(R.id.editTextPageNum);
        listviewsimple = findViewById(R.id.listviewsimple);
        resSpinner = findViewById(R.id.resSpinner);

        String[] resArr = {"Source", "540p", "360p"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, resArr);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        resSpinner.setAdapter(spinnerAdapter);
        resSpinner.setSelection(1, true);

        initLruCache();

    }

    public void onSumbit(View view) {
        final String pageNum = pageNumInput.getText().toString();
        Toast.makeText(getApplicationContext(), "Loading Page: " + pageNum, Toast.LENGTH_SHORT).show();
        //开一条子线程加载网络数据
        try {
            //handler与线程之间的通信及数据处理
            @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.what == 10) {
                        //msg.obj是获取handler发送信息传来的数据
                        @SuppressWarnings("unchecked")
                        ArrayList<VideoInfo> list = (ArrayList<VideoInfo>) msg.obj;
                        //给ListView绑定数据
                        BinderListData(list);
                    }
                }
            };
            Runnable runnable = new Runnable() {
                public void run() {
                    list = CrawlTool.getCrawlData(pageNum);
                    handler.sendMessage(handler.obtainMessage(10, list));
                }
            };
            new Thread(runnable).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //绑定数据
    public void BinderListData(ArrayList<VideoInfo> list) {
        //创建adapter对象
        adapter = new MyAdapter(list, this, listviewsimple, mMemoryCache);
        //将Adapter绑定到listview中
        listviewsimple.setAdapter(adapter);
    }

    public void onPlayVideo(View view) {

        TextView tv_addressview = view.findViewById(R.id.address);
        final String address = tv_addressview.getText().toString();
        final String[] url = {""};

        //handler与线程之间的通信及数据处理
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 11) {
                    //msg.obj是获取handler发送信息传来的数据
                    @SuppressWarnings("unchecked")
                    String url = (String) msg.obj;
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cm.setText(url);
                    Toast.makeText(getApplicationContext(), "复制成功", Toast.LENGTH_SHORT).show();
                    play(url);

                }
            }
        };

        Runnable runnable = new Runnable() {
            public void run() {
                String resolution = resSpinner.getSelectedItem().toString();
                url[0] = CrawlTool.GetVideoAddress(address, resolution);
                if (!url[0].contains("file.php")) {
                    //视频来自外站
                    url[0] = "TempAddressForOuterVideo";
                }
                handler.sendMessage(handler.obtainMessage(11, url[0]));
            }
        };

        new Thread(runnable).start();
    }

    private void play(String url) {
//        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
//        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
        mediaIntent.setDataAndType(Uri.parse(url), "video/mp4");
        startActivity(mediaIntent);
    }

    private void initLruCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/4th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 4;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }


}
