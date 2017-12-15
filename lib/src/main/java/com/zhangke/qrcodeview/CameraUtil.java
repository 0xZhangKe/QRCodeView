package com.zhangke.qrcodeview;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.util.List;

/**
 * Created by ZhangKe on 2017/11/8.
 */

public class CameraUtil {

    private static final String TAG = "CameraUtil";

    public static Camera initCamera(Activity activity, Camera camera, int cameraId){
        int mWidth = 720;
        int mHeight = 1280;
        int mFormat = ImageFormat.NV21;
        camera = Camera.open(cameraId);
        try {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(mWidth, mHeight);
            parameters.setPreviewFormat(mFormat);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE );//自动对焦
            parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);

            CameraUtil.setCameraDisplayOrientation(activity, cameraId, camera);

            for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
                Log.d(TAG, "SIZE:" + size.width + "x" + size.height);
            }
            for (Integer format : parameters.getSupportedPreviewFormats()) {
                Log.d(TAG, "FORMAT:" + format);
            }

            List<int[]> fps = parameters.getSupportedPreviewFpsRange();
            for (int[] count : fps) {
                Log.d(TAG, "T:");
                for (int data : count) {
                    Log.d(TAG, "V=" + data);
                }
            }
            camera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camera;
    }

    public static void setCameraDisplayOrientation(Context context, int cameraId, Camera camera){
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public static void setCameraPreviewSize(Camera.Parameters parameters, int suggestHeight){
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        int position = 0;
        int diff = Integer.MAX_VALUE;
        for (int i = 0; i < sizes.size(); i++) {
            if (sizes.get(i).height % 2 == 0) {
                int tmp = Math.abs(sizes.get(i).width - suggestHeight);
                if (tmp < diff) {
                    diff = tmp;
                    position = i;
                }
            }
        }
        parameters.setPreviewSize(sizes.get(position).width, sizes.get(position).height);
    }

    public static void setCameraPictureSize(Camera.Parameters parameters, int suggestHeight){
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        int position = 0;
        int diff = Integer.MAX_VALUE;
        for (int i = 0; i < sizes.size(); i++) {
            if (sizes.get(i).height % 2 == 0) {
                int tmp = Math.abs(sizes.get(i).height - suggestHeight);
                if (tmp < diff) {
                    diff = tmp;
                    position = i;
                }
            }
        }
        parameters.setPictureSize(sizes.get(position).width, sizes.get(position).height);
    }

    public static void releaseCamera(Camera camera) {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}
