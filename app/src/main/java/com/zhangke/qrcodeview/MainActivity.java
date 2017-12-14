package com.zhangke.qrcodeview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

public class MainActivity extends AppCompatActivity {

    private QRCodeView mQRCodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQRCodeView = (QRCodeView) findViewById(R.id.qr_code_view);
        mQRCodeView.setOnQRCodeListener(new QRCodeView.OnQRCodeRecognitionListener() {
            @Override
            public void onQRCodeRecognition(Result result) {
                Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
