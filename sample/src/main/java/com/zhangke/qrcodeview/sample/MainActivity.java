package com.zhangke.qrcodeview.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;
import com.zhangke.qrcodeview.QRCodeView;
import com.zhangke.qrcodeview.smaple.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private QRCodeView qrCodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qrCodeView = (QRCodeView) findViewById(R.id.qr_code_view);
        qrCodeView.setOnQRCodeListener(new QRCodeView.OnQRCodeRecognitionListener() {
            @Override
            public void onQRCodeRecognition(Result result) {
                Intent intent = new Intent(MainActivity.this, ShowQRCodeContentActivity.class);
                intent.putExtra("qr_content", result.getText());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeView.startPreview();
    }

    @Override
    protected void onPause() {
        qrCodeView.stopPreview();
        super.onPause();
    }
}
