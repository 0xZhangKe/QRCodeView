package com.zhangke.qrcodeview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;
import com.zhangke.qrcodeview.QRCodeView;
import com.zhangke.qrcodeview.smaple.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

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

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        mQRCodeView.startPreview();
    }

    @Override
    protected void onPause() {
        mQRCodeView.stopPreview();
        super.onPause();
    }
}
