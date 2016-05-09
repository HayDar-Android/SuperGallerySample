package io.haydar.sg.bean;

import android.graphics.Bitmap;

import io.haydar.sg.util.CustomImageView;


/**
 * Created by gjy on 16/5/5.
 */
public class Result {

    private Bitmap bitmap;
    private CustomImageView customImageView;
    private String url;

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public CustomImageView getCustomImageView() {
        return customImageView;
    }

    public void setCustomImageView(CustomImageView customImageView) {
        this.customImageView = customImageView;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
