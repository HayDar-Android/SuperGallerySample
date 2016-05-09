package io.haydar.sg;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;

/**
 * Created by gjy on 16/5/9.
 */
public class FolderPopWindow extends PopupWindow {

    private View view;
    private OnItemClickListener mOnItemClickListener;
    private ListView mListView;
    private FolderAdapter mFolderAdapter;
    private Context context;

    public FolderPopWindow(Context mContext, OnItemClickListener OnItemClickListener) {
        this.mOnItemClickListener = OnItemClickListener;
        this.context = mContext;
        view = LayoutInflater.from(mContext).inflate(R.layout.layout_select_folder, null);
        mListView = (ListView) view.findViewById(R.id.listview);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setOutsideTouchable(false);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                close();
                mOnItemClickListener.onItemClick(position);
            }
        });
    }

    private void close() {
        this.dismiss();
    }


    public void setList(ArrayList<SGFolder> strings) {
        if (mFolderAdapter == null) {
            mFolderAdapter = new FolderAdapter(context, strings);
        }
        mFolderAdapter.notifyDataSetChanged();
        mListView.setAdapter(mFolderAdapter);
    }

    interface OnItemClickListener {
        void onItemClick(int position);
    }
}
