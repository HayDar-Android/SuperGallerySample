package io.haydar.sg.clip;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by gjy on 16/5/9.
 */
public class ClipBitmapLayout extends FrameLayout {

    private ImageView mImageView;
    private ClipBorderView mClipBorderView;
    private Context mContext;
    private OnClipListener mOnClipListener;

    public ClipBitmapLayout(Context context) {
        this(context, null);
    }

    public ClipBitmapLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public ClipBitmapLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initClipImageView();
        initClipBorderView();

    }

    public void setOnClipListener(OnClipListener mOnClipListener) {
        this.mOnClipListener = mOnClipListener;
    }

    private void initClipImageView() {
        if (mImageView == null) {
            mImageView = new ImageView(mContext);
        }
        mImageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mImageView.setOnTouchListener(new OnTouch(mImageView));
        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        addView(mImageView);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void initClipBorderView() {
        if (mClipBorderView == null) {
            mClipBorderView = new ClipBorderView(mContext);
        }
        mClipBorderView.setLayoutParams(new LayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)));
        addView(mClipBorderView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public void setImageBitmap(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);

    }

    public ImageView getImage() {
        return mImageView;
    }

    public void clip() {

        buildDrawingCache(true);
        buildDrawingCache();
        Bitmap bitmap = getDrawingCache();
        new SaveBitmapAsyncTask().execute(bitmap);
    }

    private class SaveBitmapAsyncTask extends AsyncTask<Bitmap, Void, String> {
        String path = "";
        private int x = 36;
        private int clipWidth;
        private int clipHeight;
        private int y;

        @Override
        protected void onPreExecute() {
            //显示dialog
            mOnClipListener.toggleDialog(true);
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            Bitmap bitmap = params[0];
            clipWidth = clipHeight = bitmap.getWidth() - 36 * 2;
            y = bitmap.getHeight() / 2 - clipWidth / 2;
            Bitmap cropBitmap = Bitmap.createBitmap(bitmap, x, y, clipWidth,
                    clipHeight);
            try {

                path = getCameraFilePath() + System.currentTimeMillis() + ".png";
                File outFile = new File(path);
                outFile.createNewFile();
                FileOutputStream outStream = new FileOutputStream(outFile);
                cropBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.close();
            } catch (IOException e) {
            }
            cropBitmap.recycle();
            bitmap.recycle();
            return path;
        }

        @Override
        protected void onPostExecute(String result) {
            mOnClipListener.toggleDialog(false);
            mOnClipListener.onPostResult(result);
        }

        public String getCameraFilePath() {
            File rootPath = Environment.getExternalStorageDirectory();
            File filePath = new File(rootPath, "/imageClip");
            if (!filePath.exists()) {
                filePath.mkdir();
            }
            return filePath.getAbsolutePath();
        }

    }


    interface OnClipListener {
        void toggleDialog(boolean flag);

        void onPostResult(String str);
    }
}
