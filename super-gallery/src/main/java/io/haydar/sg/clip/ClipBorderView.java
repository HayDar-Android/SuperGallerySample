package io.haydar.sg.clip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by gjy on 16/5/11.
 */
public class ClipBorderView extends View {

    private Paint mPaint;
    private int width;
    private int height;
    private final int PADDING = 36; //默认左右宽度

    public ClipBorderView(Context context) {
        this(context, null);
    }

    public ClipBorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipBorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(Color.parseColor("#00000000"));
        canvas.drawRect(0 + PADDING, (height - width) / 2, width - PADDING, (height - width) / 2 + width, mPaint);
        mPaint.setColor(Color.parseColor("#96000000"));
        canvas.drawRect(0, 0, width, (height - width) / 2, mPaint);
        canvas.drawRect(0, (height - width) / 2, PADDING, height, mPaint);
        canvas.drawRect(PADDING, (height - width) / 2 + width, width, height, mPaint);
        canvas.drawRect(width - PADDING, (height - width) / 2, width, (height - width) / 2 + width, mPaint);
        super.onDraw(canvas);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
