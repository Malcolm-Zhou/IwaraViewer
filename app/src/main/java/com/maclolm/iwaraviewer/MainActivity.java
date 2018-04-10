package com.maclolm.iwaraviewer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText pageNumInput;
    private Button submitBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pageNumInput = findViewById(R.id.editTextPageNum);
        submitBtn = findViewById(R.id.submitBtn);


//        String http = "http://i.iwara.tv/sites/default/files/styles/thumbnail/public/video_embed_field_thumbnails/youtube/15Rkgv_UWjc.jpg?itok=qPCONQ9q";
//        ImageView imageView = (ImageView) findViewById(R.id.imageView);
//        //启动异步任务，加载网络图片
//        new LoadImagesTask(imageView).execute(http);

    }

    public void onSumbit(View view) {
        Toast.makeText(getApplicationContext(), "Page: "+ pageNumInput.getText().toString(), Toast.LENGTH_SHORT).show();
    }

}
