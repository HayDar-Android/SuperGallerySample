package io.haydar.sg.sample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;

import io.haydar.sg.list.BitmapListActivity;


public class MainActivity extends AppCompatActivity {

    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.init();
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.img);

    }

    public void onOpenGalleryEvent(View view) {
        startActivityForResult(new Intent(this, BitmapListActivity.class), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            if (!TextUtils.isEmpty(data.getStringExtra("clipimg"))) {
                img.setImageBitmap(BitmapFactory.decodeFile(data.getStringExtra("clipimg")));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}
