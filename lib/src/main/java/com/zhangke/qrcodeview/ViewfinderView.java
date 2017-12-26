package com.zhangke.qrcodeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.zxing.ResultPoint;

import java.util.ArrayList;
import java.util.List;

public final class ViewfinderView extends View {
    private static final String TAG = "ViewfinderView";

    private int mMaskColor = 0x80000000;
    private int mFrameColor = 0xffffffff;
    private boolean showFrame = true;

    private Paint mPaint;

    private Rect mFrame;

    private int mWidth = 0;
    private int mHeight = 0;

    private int previewWidth;
    private int previewHeight;

    private float scaleX = 0.0F;
    private float scaleY = 0.0F;

    private ResultPoint[] drawPoint;

    public ViewfinderView(Context context) {
        super(context);
        init();
    }

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewfinderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.TRANSPARENT);

        if (mWidth == 0) {
            mWidth = getMeasuredWidth();
            mHeight = getMeasuredHeight();
        }
        if (scaleX == 0.0) {
            scaleX = previewHeight / (float) mWidth;
            scaleY = previewWidth / (float) mHeight;
        }

        if (showFrame) {

            if (mFrame == null) {
                mFrame = new Rect();
                int viewWidth = getWidth();
                int viewHeight = getHeight();
                if (viewWidth > viewHeight) {
                    int tmp = viewWidth;
                    viewWidth = viewHeight;
                    viewHeight = tmp;
                }
                int frameWidth = (int) ((float) viewWidth * 2.0F / 3.0F);
                mFrame.top = (viewHeight - frameWidth) / 2;
                mFrame.bottom = mFrame.top + frameWidth;
                mFrame.left = (viewWidth - frameWidth) / 2;
                mFrame.right = mFrame.left + frameWidth;
            }

            //绘制灰色半透明背景
            mPaint.setColor(mMaskColor);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(mFrame.left, 0, mFrame.right, mFrame.top, mPaint);
            canvas.drawRect(0, 0, mFrame.left, getHeight(), mPaint);
            canvas.drawRect(mFrame.right, 0, getWidth(), getHeight(), mPaint);
            canvas.drawRect(mFrame.left, mFrame.bottom, mFrame.right, getHeight(), mPaint);

            //绘制内框边角
            mPaint.setColor(mFrameColor);
            int halfLineWidth = dip2px(getContext(), 4) / 2;
            mPaint.setStrokeWidth(halfLineWidth * 2);
            int inSideFrameSize = dip2px(getContext(), 50);
            canvas.drawLine(mFrame.left, mFrame.top + halfLineWidth, mFrame.left + inSideFrameSize, mFrame.top + halfLineWidth, mPaint);
            canvas.drawLine(mFrame.left + halfLineWidth, mFrame.top, mFrame.left + halfLineWidth, mFrame.top + inSideFrameSize, mPaint);
            canvas.drawLine(mFrame.right - inSideFrameSize, mFrame.top + halfLineWidth, mFrame.right, mFrame.top + halfLineWidth, mPaint);
            canvas.drawLine(mFrame.right - halfLineWidth, mFrame.top, mFrame.right - halfLineWidth, mFrame.top + inSideFrameSize, mPaint);

            canvas.drawLine(mFrame.left + halfLineWidth, mFrame.bottom - inSideFrameSize, mFrame.left + halfLineWidth, mFrame.bottom, mPaint);
            canvas.drawLine(mFrame.left, mFrame.bottom - halfLineWidth, mFrame.left + inSideFrameSize, mFrame.bottom - halfLineWidth, mPaint);
            canvas.drawLine(mFrame.right - inSideFrameSize, mFrame.bottom - halfLineWidth, mFrame.right, mFrame.bottom - halfLineWidth, mPaint);
            canvas.drawLine(mFrame.right - halfLineWidth, mFrame.bottom, mFrame.right - halfLineWidth, mFrame.bottom - inSideFrameSize, mPaint);

            //绘制内框
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(dip2px(getContext(), 1));
            canvas.drawRect(mFrame, mPaint);
        }

        if (drawPoint != null && drawPoint.length > 0) {
            for (ResultPoint point : drawPoint) {
                float x = (int) ((previewHeight - point.getY()) / scaleX);
                float y = (int) (point.getX() / scaleY);
                canvas.drawPoint(x, y, mPaint);
            }
        }
    }

    public void addPoint(ResultPoint[] points, int previewWidth, int previewHeight) {
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
        drawPoint = points;
        invalidate();
    }

    public void setFrameColor(int frameColor) {
        this.mFrameColor = frameColor;
        invalidate();
    }

    public void setShowFrame(boolean showFrame) {
        this.showFrame = showFrame;
        invalidate();
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
