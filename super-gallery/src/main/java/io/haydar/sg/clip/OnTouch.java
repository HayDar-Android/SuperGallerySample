package io.haydar.sg.clip;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by gjy on 16/5/13.
 */
public class OnTouch implements View.OnTouchListener {
    private ImageView mImageView;

    /**
     * 两个手指的开始距离
     */

    public OnTouch(ImageView mImageView) {
        this.mImageView = mImageView;
    }

    private PointF startPoint = new PointF();
    private Matrix matrix = new Matrix();
    private Matrix currentMatrix = new Matrix();

    private int mode = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private float startDis = 0;
    private PointF midPoint;
    float curScale = 1f;

    public boolean onTouch(View v, MotionEvent event) {
        mImageView.setScaleType(ImageView.ScaleType.MATRIX);
        matrix.set(mImageView.getImageMatrix());
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                currentMatrix.set(mImageView.getImageMatrix());

                startPoint.set(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    float dx = event.getX() - startPoint.x;
                    float dy = event.getY() - startPoint.y;
                    matrix.set(currentMatrix);
                    matrix.postTranslate(dx, dy);

                } else if (mode == ZOOM) {
                    float endDis = distance(event);
                    if (endDis > 10f) {
                        float scale = endDis / startDis;
                        curScale = scale * curScale;

                        matrix.set(currentMatrix);
                        matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                    }

                }

                break;

            case MotionEvent.ACTION_UP:
                mode = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = 0;
                float scale;
                Matrix m = mImageView.getImageMatrix();
                float values[] = new float[9];
                m.getValues(values);
                float x = values[0];
                if (x < 0.2) {
                    scale = 0.2f / x;
                    matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                } else if (x > 5) {
                    scale = 5 / x;
                    matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                startDis = distance(event);

                if (startDis > 10f) {
                    midPoint = mid(event);
                    currentMatrix.set(mImageView.getImageMatrix());
                }

                break;

        }

        mImageView.setImageMatrix(matrix);
        return true;
    }


    private static float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private static PointF mid(MotionEvent event) {
        float midx = event.getX(1) + event.getX(0);
        float midy = event.getY(1) + event.getY(0);

        return new PointF(midx / 2, midy / 2);
    }


}
