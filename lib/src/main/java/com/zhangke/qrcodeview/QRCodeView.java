package com.zhangke.qrcodeview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ZhangKe on 2017/12/11.
 */

public class QRCodeView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "QRCodeView";
    static final int EVENT_SUCCESS = 0x001;
    static final int EVENT_FAILED = 0x002;

    private boolean showResultPoint = false;

    private Camera mCamera;
    private Camera.Parameters mCameraParameters;
    private SurfaceHolder mSurfaceHolder;

    private int mWidth, mHeight;
    private int mCameraID;

    private DecodeThread mDecodeThread;
    private OnQRCodeRecognitionListener onQRCodeListener;

    private Map<DecodeHintType, Object> decodeHints = new EnumMap<>(DecodeHintType.class);

    private MainHandler mHandler = new MainHandler();
    private PreviewCallback mPreviewCallback;

    private boolean previewing = false;
    private boolean surfaceCreated = false;

    public class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_SUCCESS:
                    if (mCamera != null) {
                        onQRCodeListener.onQRCodeRecognition((Result)msg.obj);
                        restartPreviewAndDecode();
                    }
                    break;
                case EVENT_FAILED:
                    if (mCamera != null) {
                        restartPreviewAndDecode();
                    }
                    break;
            }
        }
    }

    public QRCodeView(Context context) {
        super(context);
        init();
    }

    public QRCodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QRCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.QRCodeView, defStyleAttr, 0);
        mCameraID = a.getInt(R.styleable.QRCodeView_facing, Camera.CameraInfo.CAMERA_FACING_BACK);
        showResultPoint = a.getBoolean(R.styleable.QRCodeView_showPoint, false);
        a.recycle();
        init();
    }

    private void init() {
        decodeHints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, showResultPoint);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mDecodeThread = new DecodeThread(this);
        mDecodeThread.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceCreated = true;
        openCamera();
        Log.e(TAG, "surfaceCreated");
        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceCreated = false;
        CameraUtil.releaseCamera(mCamera);
        Message.obtain(mDecodeThread.getHandler(), DecodeThread.QUIT_EVENT).sendToTarget();
    }

    private void openCamera() {
        try {
            mCamera = Camera.open(mCameraID);
            mCamera.setPreviewDisplay(this.getHolder());

            mCameraParameters = mCamera.getParameters();

            Point bestPreviewSize = CameraConfigurationUtils.findBestPreviewSizeValue(mCameraParameters, getContext());
            mCameraParameters.setPreviewSize(bestPreviewSize.x, bestPreviewSize.y);
            CameraUtil.setCameraPictureSize(mCameraParameters, mHeight);
            CameraUtil.setCameraDisplayOrientation(getContext(), mCameraID, mCamera);

            mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//自动对焦
            mCameraParameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);

            mCamera.setParameters(mCameraParameters);

            mPreviewCallback = new PreviewCallback(bestPreviewSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCamera != null) {
            mWidth = mCamera.getParameters().getPreviewSize().width;
            mHeight = mCamera.getParameters().getPreviewSize().height;

            Camera.Size imageSize = this.mCamera.getParameters().getPreviewSize();
            this.mWidth = imageSize.width;
            this.mHeight = imageSize.height;
        }
    }

    public void startPreview(){
        if(!previewing && surfaceCreated) {
            mCamera.startPreview();
            restartPreviewAndDecode();
            previewing = true;
        }
    }

    public void stopPreview(){
        if(previewing) {
            mCamera.stopPreview();
            previewing = false;
        }
    }

    public boolean isPreview(){
        return previewing;
    }

    public Camera getCamera(){
        return mCamera;
    }

    private void restartPreviewAndDecode() {
        mPreviewCallback.setHandler(mDecodeThread.getHandler(), DecodeThread.DECODE_EVENT);
        mCamera.setOneShotPreviewCallback(mPreviewCallback);
    }

    public Handler getHandler() {
        return mHandler;
    }

    public OnQRCodeRecognitionListener getOnQRCodeListener() {
        return onQRCodeListener;
    }

    public void setOnQRCodeListener(OnQRCodeRecognitionListener onQRCodeListener) {
        this.onQRCodeListener = onQRCodeListener;
    }

    public interface OnQRCodeRecognitionListener {
        void onQRCodeRecognition(Result result);
    }
}
