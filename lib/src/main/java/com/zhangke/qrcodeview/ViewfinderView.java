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

    private Paint mPaint;

    private Rect mFrame;

    private List<ResultPoint> drawPoint = new ArrayList<>();

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
        mPaint.setColor(mFrameColor);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mFrame == null) {
            long startTime = System.currentTimeMillis();
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
        int lineWidth = dip2px(getContext(), 4);
        mPaint.setStrokeWidth(lineWidth);
        int inSideFrameSize = dip2px(getContext(), 50);
        canvas.drawLine(mFrame.left, mFrame.top + lineWidth / 2, mFrame.left + inSideFrameSize, mFrame.top + lineWidth / 2, mPaint);
        canvas.drawLine(mFrame.left + lineWidth / 2, mFrame.top, mFrame.left + lineWidth / 2, mFrame.top + inSideFrameSize, mPaint);
        canvas.drawLine(mFrame.right - inSideFrameSize, mFrame.top + lineWidth / 2, mFrame.right, mFrame.top + lineWidth / 2, mPaint);
        canvas.drawLine(mFrame.right - lineWidth / 2, mFrame.top, mFrame.right - lineWidth / 2, mFrame.top + inSideFrameSize, mPaint);

        canvas.drawLine(mFrame.left + lineWidth / 2, mFrame.bottom - inSideFrameSize, mFrame.left + lineWidth / 2, mFrame.bottom, mPaint);
        canvas.drawLine(mFrame.left, mFrame.bottom - lineWidth / 2, mFrame.left + inSideFrameSize, mFrame.bottom - lineWidth / 2, mPaint);
        canvas.drawLine(mFrame.right - inSideFrameSize, mFrame.bottom - lineWidth / 2, mFrame.right, mFrame.bottom - lineWidth / 2, mPaint);
        canvas.drawLine(mFrame.right - lineWidth / 2, mFrame.bottom, mFrame.right - lineWidth / 2, mFrame.bottom - inSideFrameSize, mPaint);

        //绘制内框
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dip2px(getContext(), 1));
        canvas.drawRect(mFrame, mPaint);

        if(drawPoint != null && !drawPoint.isEmpty()){
            for(ResultPoint point : drawPoint){
                canvas.drawPoint(point.getX(), point.getY(), mPaint);
            }
        }
    }

    private void addPoint(List<ResultPoint> points){
        drawPoint.clear();
        drawPoint.addAll(points);
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFrame = null;
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mFrame = null;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
