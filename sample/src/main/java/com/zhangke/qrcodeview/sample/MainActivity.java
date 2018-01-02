package com.zhangke.qrcodeview.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.zhangke.qrcodeview.QRCodeUtil;
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
                Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeView.startPreview();
        Log.e(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        qrCodeView.stopPreview();
        super.onPause();
    }
}
