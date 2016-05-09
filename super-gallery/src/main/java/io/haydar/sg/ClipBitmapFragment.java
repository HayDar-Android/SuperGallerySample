package io.haydar.sg;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by gjy on 16/5/6.
 */
public class ClipBitmapFragment extends Fragment {

    public static ClipBitmapFragment newInstance(CGImage cgImage) {
        ClipBitmapFragment clipBitmapFragment = new ClipBitmapFragment();
        Bundle args = new Bundle();
        args.putSerializable("", "");
        clipBitmapFragment.setArguments(args);
        return clipBitmapFragment;
    }

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.item_layout, container, false);
        return view;
    }
}
