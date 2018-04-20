package com.zhangke.qrcodeview.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.zhangke.qrcodeview.smaple.R;


/**
 * Created by ZhangKe on 2018/4/20.
 */
public class ShowQRCodeContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_qr_code_content);

        Intent intent = getIntent();
        if (intent.hasExtra("qr_content")) {
            TextView tvContent = findViewById(R.id.tv_content);
            tvContent.setText(intent.getStringExtra("qr_content"));
        }
    }
}
