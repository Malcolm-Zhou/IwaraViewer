package com.maclolm.iwaraviewer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    private Button submitBtn;

    private ListView listviewsimple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pageNumInput = findViewById(R.id.editTextPageNum);
        submitBtn = findViewById(R.id.submitBtn);
        listviewsimple = findViewById(R.id.listviewsimple);




        ArrayList<VideoInfo> list = CrawlTool.getCrawlData("101");

        MyAdapter myAdapter = new MyAdapter(list, this);
        listviewsimple.setAdapter(myAdapter);

    }

    public void onSumbit(View view) {
        Toast.makeText(getApplicationContext(), "Page: " + pageNumInput.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    public void onPlayVideo(View view) {


        TextView tv_addressview = view.findViewById(R.id.address);
        Toast.makeText(getApplicationContext(), tv_addressview.getText(), Toast.LENGTH_SHORT).show();
        play(tv_addressview.getText().toString());
    }

    private void play(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
        mediaIntent.setDataAndType(Uri.parse(url), mimeType);
        startActivity(mediaIntent);
    }


}
