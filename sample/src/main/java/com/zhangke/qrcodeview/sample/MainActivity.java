package com.zhangke.qrcodeview.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.zhangke.qrcodeview.QRCodeUtil;
import com.zhangke.qrcodeview.smaple.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ImageView img01;
    private ImageView img02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.qr_code);
//        img01.setImageBitmap(bitmap);
        Toast.makeText(this, QRCodeUtil.decodeQRCode(bitmap), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
