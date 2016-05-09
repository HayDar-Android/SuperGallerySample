package io.haydar.sg.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import io.haydar.sg.R;
import io.haydar.sg.bean.CGImage;
import io.haydar.sg.util.CustomImageView;
import io.haydar.sg.util.ImageUtil;

/**
 * Created by gjy on 16/4/27.
 */
public class CustomGalleryAdapter extends RecyclerView.Adapter<CustomGalleryAdapter.ViewHolder> {

    private LayoutInflater mLayoutInflater;

    private ArrayList<CGImage> mStringList;
    private View v;
    private ViewHolder mViewHolder;
    private ImageUtil mImageUtil;
    private boolean isScroll = false;
    private OnRecyclerItemClickListener mOnRecyclerItemClickListener;

    public CustomGalleryAdapter(Context context, ArrayList<CGImage> mStringList) {
        this.mStringList = mStringList;
        this.mLayoutInflater = LayoutInflater.from(context);
        mImageUtil = ImageUtil.build(context.getApplicationContext());
    }


    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener mOnRecyclerItemClickListener) {
        this.mOnRecyclerItemClickListener = mOnRecyclerItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = mLayoutInflater.inflate(R.layout.item_layout, parent, false);
        mViewHolder = new ViewHolder(v);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position == 0) {
            holder.mImageView.setScaleType(ImageView.ScaleType.CENTER);
            holder.mImageView.setImageResource(android.R.drawable.ic_menu_camera);
            return;
        }
        if (holder.mImageView.getScaleType() != ImageView.ScaleType.CENTER_CROP) {
            holder.mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        mImageUtil.loadBitmap(mStringList.get(position), holder.mImageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRecyclerItemClickListener != null) {
                    mOnRecyclerItemClickListener.onRecyclerItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStringList.size();
    }

    public void setIsScroll(boolean isScroll) {
        this.isScroll = isScroll;
    }

    public void clearCache() {
        if (mImageUtil != null) {
            mImageUtil.clearCache();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private CustomImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (CustomImageView) itemView.findViewById(R.id.img);
        }
    }

    public void setStringList(ArrayList<CGImage> mStringList) {
        if (this.mStringList != null) {
            this.mStringList.clear();
        }
        this.mStringList = mStringList;
        notifyDataSetChanged();
    }

    public void addStringList(ArrayList<CGImage> mStringList) {
        int positionItem = this.mStringList.size();
        int itemCount = mStringList.size();
        this.mStringList.addAll(mStringList);
        if (positionItem > 0 && itemCount > 0) {
            notifyItemRangeInserted(positionItem, itemCount);
        }
    }

    interface OnRecyclerItemClickListener {
        void onRecyclerItemClick(int position);
    }
}
