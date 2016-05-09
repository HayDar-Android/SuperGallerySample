package io.haydar.sg.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by gjy on 16/4/27.
 */
public class CustomImageView extends ImageView {


    public static int myWidth;

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (myWidth == 0) {
            myWidth = getMeasuredWidth();
        }
    }
}
