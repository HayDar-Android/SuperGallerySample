package io.haydar.sg.clip;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import io.haydar.sg.R;
import io.haydar.sg.bean.CGImage;

/**
 * Created by gjy on 16/5/6.
 */
public class ClipBitmapActivity extends AppCompatActivity implements ClipBitmapLayout.OnClipListener {

    private TextView submitTV;
    private ClipBitmapLayout mClipBitmapLayout;
    private int mWidth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_clip);
        WindowManager wm = (WindowManager)
                getSystemService(Context.WINDOW_SERVICE);
        mWidth = wm.getDefaultDisplay().getWidth();
        submitTV = (TextView) findViewById(R.id.submit);
        mClipBitmapLayout = (ClipBitmapLayout) findViewById(R.id.clipimg);
        mClipBitmapLayout.setOnClipListener(this);
        submitTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mClipBitmapLayout.clip();
            }
        });
        new GetBitmapTask(mWidth, mWidth).execute((CGImage) getIntent().getBundleExtra("bundle").getSerializable("img"));
    }

    @Override
    public void toggleDialog(boolean flag) {
        if (true) {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(ClipBitmapActivity.this);
            }
            mProgressDialog.setMessage("Saving...");
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }


    @Override
    public void onPostResult(String str) {
        Intent intent = getIntent();
        intent.putExtra("clipimg", str);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mProgressDialog == null) {
            mProgressDialog.dismiss();
        }
        super.onDestroy();
    }

    private ProgressDialog mProgressDialog;

    private class GetBitmapTask extends AsyncTask<CGImage, Void, Bitmap> {
        String path;
        int width;
        int height;
        private CGImage mCgImage;

        public GetBitmapTask(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(ClipBitmapActivity.this);
            mProgressDialog.setMessage("加载中……");
            mProgressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(CGImage... params) {
            mCgImage = params[0];
            path = mCgImage.getPath();
            int targetDensity = getResources().getDisplayMetrics().densityDpi;
            BitmapFactory.Options mOptions = new BitmapFactory.Options();
            if (mCgImage.getWidth() == 0 || mCgImage.getHeight() == 0) {
                mOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mCgImage.getPath(), mOptions);
                mCgImage.setWidth(mOptions.outWidth);
                mCgImage.setHeight(mOptions.outHeight);
                mOptions.inJustDecodeBounds = false;
            }
            mOptions.inSampleSize = calculateInSampleSize(mCgImage, width, height);
            double xSScale = ((double) mCgImage.getWidth()) / ((double) width);
            double ySScale = ((double) mCgImage.getHeight()) / ((double) height);
            double startScale = xSScale > ySScale ? xSScale : ySScale;
            mOptions.inScaled = true;
            mOptions.inDensity = (int) (targetDensity * startScale);
            mOptions.inTargetDensity = targetDensity;
            mOptions.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(path, mOptions);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // mClipImageView.setImageBitmap(bitmap);
            mProgressDialog.dismiss();
            mClipBitmapLayout.setImageBitmap(bitmap);
            cancel(true);
            super.onPostExecute(bitmap);
        }
    }

    private int calculateInSampleSize(CGImage mCgImage, int reqWidth, int reqHeight) {
// Raw height and width of image
        final int height = mCgImage.getThumbnailsWidth();
        final int width = mCgImage.getThumbnailsHeight();
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
