package com.zhangke.qrcodeview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.zxing.Result;
import com.zhangke.qrcodeview.lib.R;

/**
 * Created by ZhangKe on 2017/12/11.
 */

public class QRCodeView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback{

    private static final String TAG = "QRCodeView";

    private Camera mCamera;
    private Camera.Parameters mCameraParameters;
    private SurfaceHolder mSurfaceHolder;

    private int mWidth, mHeight;
    private int mCameraID;

    private int dataSize = 0;
    private ByteArrayPool byteArrayPool;

    private DecodeThread mDecodeThread;
    private OnQRCodeRecognitionListener onQRCodeListener;

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
        a.recycle();
        init();
    }

    private void init(){
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        byteArrayPool = new ByteArrayPool(3);

        mDecodeThread = new DecodeThread(this);
        mDecodeThread.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera();
        mCamera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraUtil.releaseCamera(mCamera);
        Message.obtain(mDecodeThread.getHandler(), DecodeThread.QUIT_EVENT).sendToTarget();
    }

    private void openCamera(){
        try {
            mCamera = Camera.open(mCameraID);
            mCamera.setPreviewDisplay(this.getHolder());

            mCameraParameters = mCamera.getParameters();

            Point bestPreviewSize = CameraConfigurationUtils.findBestPreviewSizeValue(mCameraParameters, getContext());
            mCameraParameters.setPreviewSize(bestPreviewSize.x, bestPreviewSize.y);
            CameraUtil.setCameraPictureSize(mCameraParameters, mHeight);

            mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//自动对焦
            mCameraParameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);

            mCamera.setParameters(mCameraParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCamera != null) {
            mWidth = mCamera.getParameters().getPreviewSize().width;
            mHeight = mCamera.getParameters().getPreviewSize().height;

            Camera.Size imageSize = this.mCamera.getParameters().getPreviewSize();
            this.mWidth = imageSize.width;
            this.mHeight = imageSize.height;
            int lineBytes = imageSize.width * ImageFormat.getBitsPerPixel(mCameraParameters.getPreviewFormat()) / 8;
            this.mCamera.addCallbackBuffer(new byte[lineBytes * this.mHeight]);
            this.mCamera.addCallbackBuffer(new byte[lineBytes * this.mHeight]);
            this.mCamera.addCallbackBuffer(new byte[lineBytes * this.mHeight]);
            this.mCamera.setPreviewCallbackWithBuffer(this);
            dataSize = lineBytes * this.mHeight;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (onQRCodeListener != null) {
            byte[] buffer = new byte[dataSize];
            System.arraycopy(data, 0, buffer, 0, buffer.length);
            Message message = Message.obtain(mDecodeThread.getHandler());
            message.what = DecodeThread.DECODE_EVENT;
            message.obj = buffer;
            message.arg1 = mWidth;
            message.arg2 = mHeight;
            message.sendToTarget();
        }
        if (this.mCamera != null) {
            this.mCamera.addCallbackBuffer(data);
        }
    }

    public OnQRCodeRecognitionListener getOnQRCodeListener() {
        return onQRCodeListener;
    }

    public void setOnQRCodeListener(OnQRCodeRecognitionListener onQRCodeListener) {
        this.onQRCodeListener = onQRCodeListener;
    }

    public interface OnQRCodeRecognitionListener{
        void onQRCodeRecognition(Result result);
    }
}
