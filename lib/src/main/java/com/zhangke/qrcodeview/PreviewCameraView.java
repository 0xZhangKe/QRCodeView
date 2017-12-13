package com.zhangke.qrcodeview;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zhangke.qrcodeview.lib.R;

import java.io.IOException;

/**
 * Created by ZhangKe on 2017/12/13.
 */

public class PreviewCameraView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = PreviewCameraView.class.getSimpleName();

    public static final int DECODE_SUCCESS_EVENT = 0x01;
    public static final int DECODE_FAILED_EVENT = 0x02;

    private CameraManager mCameraManager;
    private Context mContext;

    private DecodeThread mDecodeThread;

    private MainHandler mHandler = new MainHandler();

    private class MainHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case DECODE_SUCCESS_EVENT:
                    mCameraManager.requestPreviewFrame(mDecodeThread.getHandler(), DecodeThread.DECODE_EVENT);
                    break;
                case DECODE_FAILED_EVENT:
                    mCameraManager.requestPreviewFrame(mDecodeThread.getHandler(), DecodeThread.DECODE_EVENT);
                    break;
            }
        }
    }

    public PreviewCameraView(Context context) {
        super(context);
        init();
    }

    public PreviewCameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreviewCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mContext = getContext();
        mCameraManager = new CameraManager(mContext.getApplicationContext());
        mDecodeThread = new DecodeThread(this);
        mDecodeThread.start();
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initCamera(holder);
        startPreview();
        if(mDecodeThread != null) {
            mCameraManager.requestPreviewFrame(mDecodeThread.getHandler(), DecodeThread.DECODE_EVENT);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCameraManager.closeDriver();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (mCameraManager.isOpen()) {
            Log.e(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            mCameraManager.openDriver(surfaceHolder);
        } catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage(), ioe);
            ioe.printStackTrace();
        } catch (RuntimeException e) {
            Log.e(TAG, "Unexpected error initializing camera", e);
            e.printStackTrace();
        }
    }

    public void startPreview(){
        if(mCameraManager != null && !mCameraManager.isPreview()){
            mCameraManager.startPreview();
        }
    }

    private void stopPreview(){
        if(mCameraManager != null && mCameraManager.isOpen()){
            mCameraManager.stopPreview();
        }
    }

    private OnPreviewCallback onPreviewCallback;

    public void setOnPreviewCallback(OnPreviewCallback onPreviewCallback) {
        this.onPreviewCallback = onPreviewCallback;
    }

    public OnPreviewCallback getOnPreviewCallback() {
        return onPreviewCallback;
    }

    public MainHandler getHandler(){
        return mHandler;
    }

    public interface OnPreviewCallback{
        boolean onPreview(byte[] data, Handler handler);
    }
}
