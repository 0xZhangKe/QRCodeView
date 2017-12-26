package com.zhangke.qrcodeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.zxing.Result;

/**
 * Created by ZhangKe on 2017/12/11.
 */

public class QRCodeView extends FrameLayout implements SurfaceHolder.Callback {

    private static final String TAG = "QRCodeView";
    static final int EVENT_SUCCESS = 0x001;
    static final int EVENT_FAILED = 0x002;

    private boolean showFrame = true;
    private boolean showResultPoint = false;
    private int frameColor = Color.WHITE;

    private SurfaceView mSurfaceView;
    private ViewfinderView mViewfinderView;

    private Camera mCamera;
    private Camera.Parameters mCameraParameters;
    private SurfaceHolder mSurfaceHolder;

    private int mWidth, mHeight;
    private int mCameraID;

    private DecodeThread mDecodeThread;
    private OnQRCodeRecognitionListener onQRCodeListener;

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
                    Result result = (Result) msg.obj;
                    if (mCamera != null) {
                        onQRCodeListener.onQRCodeRecognition(result);
                        restartPreviewAndDecode();
                    }
                    if (showResultPoint && mViewfinderView != null) {
                        mViewfinderView.addPoint(result.getResultPoints(), mWidth, mHeight);
                    }
                    break;
                case EVENT_FAILED:
                    if (mCamera != null) {
                        restartPreviewAndDecode();
                    }
                    if (showResultPoint && mViewfinderView != null) {
                        mViewfinderView.addPoint(null, mWidth, mHeight);
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
        showFrame = a.getBoolean(R.styleable.QRCodeView_showFrame, true);
        mCameraID = a.getInt(R.styleable.QRCodeView_facing, Camera.CameraInfo.CAMERA_FACING_BACK);
        showResultPoint = a.getBoolean(R.styleable.QRCodeView_showPoint, false);
        frameColor = a.getColor(R.styleable.QRCodeView_frameColor, Color.WHITE);
        a.recycle();
        init();
    }

    private void init() {
        mSurfaceView = new SurfaceView(getContext());
        mSurfaceView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mSurfaceView);

        if (showFrame || showResultPoint) {
            mViewfinderView = new ViewfinderView(getContext());
            mViewfinderView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            addView(mViewfinderView);
            mViewfinderView.setFrameColor(frameColor);
            mViewfinderView.setShowFrame(showFrame);
        }

        mSurfaceHolder = mSurfaceView.getHolder();
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
            mCamera.setPreviewDisplay(mSurfaceHolder);

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

    public void startPreview() {
        if (!previewing && surfaceCreated) {
            mCamera.startPreview();
            restartPreviewAndDecode();
            previewing = true;
        }
    }

    public void stopPreview() {
        if (previewing) {
            mCamera.stopPreview();
            previewing = false;
        }
    }

    public boolean isPreview() {
        return previewing;
    }

    public Camera getCamera() {
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
