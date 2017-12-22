package com.zhangke.qrcodeview;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public final class ViewfinderView extends View {

    private int mMaskColor = 0x60000000;
    private int mFrameColor = 0xffffffff;

    private Paint mFramePaint;

    private Rect mFrame;

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

    private void init(){
        mFramePaint = new Paint();
        mFramePaint.setColor(mFrameColor);
        mFramePaint.setAntiAlias(true);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mFrame == null){
            mFrame = new Rect();
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            if(viewWidth > viewHeight){
                int tmp = viewWidth;
                viewWidth = viewHeight;
                viewHeight = tmp;
            }
            int frameWidth = (int) ((float)viewWidth * 2.0F / 3.0F);
            mFrame.top = (viewHeight - frameWidth) / 2;
            mFrame.bottom = mFrame.top + frameWidth;
            mFrame.left = (viewWidth - frameWidth) / 2;
            mFrame.right = mFrame.left + frameWidth;
        }
        canvas.drawColor(mMaskColor);

        mFramePaint.setStyle(Paint.Style.FILL);
        mFramePaint.setColor(Color.TRANSPARENT);
        canvas.drawRect(mFrame, mFramePaint);

        mFramePaint.setStyle(Paint.Style.STROKE);
        mFramePaint.setColor(mFrameColor);
        mFramePaint.setStrokeWidth(dip2px(getContext(), 1));
        canvas.drawRect(mFrame, mFramePaint);

    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
