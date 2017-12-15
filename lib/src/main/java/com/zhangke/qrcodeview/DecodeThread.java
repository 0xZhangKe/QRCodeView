package com.zhangke.qrcodeview;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.ByteArrayOutputStream;

public class DecodeThread extends Thread {
    private static final String TAG = "DecodeThread";

    public static final int DECODE_EVENT = 0x012;
    public static final int QUIT_EVENT = 0x013;

    private Handler mHandler;
    private MultiFormatReader multiFormatReader = new MultiFormatReader();
    private QRCodeView mQRCodeView;

    private class DecodeHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case DECODE_EVENT:
                    decode((byte[]) msg.obj, msg.arg1, msg.arg2);
                    break;
                case QUIT_EVENT:
                    if(Looper.myLooper() != null) {
                        Looper.myLooper().quit();
                    }
                    break;
            }
        }
    }

    public DecodeThread(QRCodeView qrCodeView){
        mQRCodeView = qrCodeView;
    }

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new DecodeHandler();
        Looper.loop();
    }

    public Handler getHandler(){
        return mHandler;
    }

    private void decode(byte[] data, int width, int height) {
        if(data == null || data.length <= 0) return;
        Result rawResult = null;
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
        ByteArrayPool.getInstance().returnBuf(data);
        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = multiFormatReader.decodeWithState(bitmap);
            } catch (ReaderException re) {
                // continue
            } finally {
                multiFormatReader.reset();
            }
        }

        if (rawResult != null && mQRCodeView != null) {
            Message message = Message.obtain();
            message.what = mQRCodeView.EVENT_SUCCESS;
            message.obj = rawResult;
            mQRCodeView.getHandler().sendMessage(message);
        }else{
            Message.obtain(mQRCodeView.getHandler(), QRCodeView.EVENT_FAILED).sendToTarget();
        }
    }
}
