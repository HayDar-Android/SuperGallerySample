package io.haydar.sg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.orhanobut.logger.Logger;

import io.haydar.sg.bean.CGImage;
import io.haydar.sg.clip.ClipBitmapFragment;
import io.haydar.sg.list.BitmapListFragment;

/**
 * Created by gjy on 16/5/6.
 */
public class SuperGalleryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_super_gallery);
        Logger.init();
        init();
    }

    private void init() {
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, new BitmapListFragment()).commit();
    }

    public void startClipBitmapFragment(CGImage cgImage) {
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, ClipBitmapFragment.newInstance(cgImage)).commit();
    }
}
