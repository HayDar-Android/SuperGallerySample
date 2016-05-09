package io.haydar.sg.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.haydar.sg.SuperGalleryActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onOpenGalleryEvent(View view) {
        startActivity(new Intent(this, SuperGalleryActivity.class));
    }
}
