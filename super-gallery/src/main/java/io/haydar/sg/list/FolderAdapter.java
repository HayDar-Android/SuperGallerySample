package io.haydar.sg.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.haydar.sg.R;
import io.haydar.sg.bean.SGFolder;

/**
 * Created by gjy on 16/5/9.
 */
public class FolderAdapter extends BaseAdapter {
    private ArrayList<SGFolder> strings;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public FolderAdapter(Context context, ArrayList<SGFolder> strings) {
        this.mContext = context;
        this.strings = strings;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return strings.size() == 0 ? 0 : strings.size();
    }

    @Override
    public Object getItem(int position) {
        return strings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_folder, parent, false);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (strings.get(position).getCount() > 0) {
            holder.tv.setText(strings.get(position).getName() + "( " + strings.get(position).getCount() + " )");

        } else {
            holder.tv.setText(strings.get(position).getName());
        }
        return convertView;
    }

    static class ViewHolder {
        TextView tv;
    }
}
