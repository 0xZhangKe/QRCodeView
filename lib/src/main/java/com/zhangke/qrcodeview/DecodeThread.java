package com.zhangke.qrcodeview;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class DecodeThread extends Thread {

    public static final int DECODE_EVENT = 0x012;
    public static final int QUIT_EVENT = 0x013;

    private Handler mHandler;

    private PreviewCameraView mPreviewCameraView;

    private class DecodeHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case DECODE_EVENT:
                    if(mPreviewCameraView != null && mPreviewCameraView.getOnPreviewCallback() != null && mPreviewCameraView.getHandler() !=  null){
                        mPreviewCameraView.getOnPreviewCallback().onPreview((byte[])msg.obj, mPreviewCameraView.getHandler());
                    }
                    break;
                case QUIT_EVENT:
                    Looper.myLooper().quit();
                    break;
            }
        }
    }

    public DecodeThread(PreviewCameraView previewCameraView){
        this.mPreviewCameraView = previewCameraView;
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

}
