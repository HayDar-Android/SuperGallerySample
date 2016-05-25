package io.haydar.sg.list;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.haydar.sg.R;
import io.haydar.sg.bean.CGImage;
import io.haydar.sg.bean.SGFolder;
import io.haydar.sg.clip.ClipBitmapActivity;

/**
 * Created by gjy on 16/4/27.
 */
public class BitmapListActivity extends AppCompatActivity implements FolderPopWindow.OnItemClickListener, CustomGalleryAdapter.OnRecyclerItemClickListener {

    RecyclerView mRecyclerView;
    private TextView titleTV;
    private CustomGalleryAdapter mAdapter;
    private ArrayList<CGImage> mStringList;
    private SingleMediaScanner sms;
    private List<SGFolder> mFolderList;
    private FolderPopWindow mFolderPopWindow;
    private final int CAMERA = 11;
    private final int CLIP = 22;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.fragment_bitmap_list);
        initView();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.listview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        titleTV = (TextView) findViewById(R.id.title);
        titleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPop();
            }
        });
        mStringList = new ArrayList<>();
        mAdapter = new CustomGalleryAdapter(this, mStringList);
        mAdapter.setOnRecyclerItemClickListener(this);
        mRecyclerView.addItemDecoration(new CGItemDecoration());
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        sms = new SingleMediaScanner(this, new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DCIM" + File.separator + "Camera" + File.separator));
        titleTV.setText("最近照片");
        new SelectImageTask().execute(new SGFolder("0", "最近照片"));
    }

    /**
     * 显示pop
     */
    private void showPop() {
        if (mFolderPopWindow == null) {
            mFolderPopWindow = new FolderPopWindow(this, this);
            new SelectFolderTask().execute();
        }
        if (mFolderPopWindow.isShowing()) {
            mFolderPopWindow.dismiss();
        } else {
            mFolderPopWindow.showAsDropDown(titleTV);
        }
    }


    @Override
    public void onDestroy() {
        if (mAdapter != null) {
            mAdapter.clearCache();
        }
        super.onDestroy();
    }

    /**
     * 选择文件夹
     *
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        new SelectImageTask().execute(mFolderList.get(position));
    }

    /**
     * 跳转到裁剪页面
     *
     * @param position
     */
    @Override
    public void onRecyclerItemClick(int position) {
        Intent intent = new Intent(this, ClipBitmapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("img", mStringList.get(position));
        intent.putExtra("bundle", bundle);
        startActivityForResult(intent, CLIP);
    }

    /**
     * 调用相机拍照
     */
    @Override
    public void onCameraItemClick() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            }
            startActivityForResult(intent, CAMERA);
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath =  image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CLIP && resultCode == RESULT_OK) {
            if (!TextUtils.isEmpty(data.getStringExtra("clipimg"))) {
                setResult(RESULT_OK, data);
                finish();
            }
        } else if (requestCode == CAMERA && resultCode == RESULT_OK) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);

            CGImage cgImage = new CGImage();
            cgImage.setPath(mCurrentPhotoPath);
            Intent intent = new Intent(this, ClipBitmapActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("img", cgImage);
            intent.putExtra("bundle", bundle);
            startActivityForResult(intent, CLIP);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 遍历图片文件夹
     */
    private class SelectFolderTask extends AsyncTask<Void, Void, ArrayList<SGFolder>> {
        String folderProjection[] = {
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                "count(" + MediaStore.Images.ImageColumns.BUCKET_ID + ") as count_id"
        };

        String selection = MediaStore.Images.Media.MIME_TYPE + "=? OR +" + MediaStore.Images.Media.MIME_TYPE + " =?" + ") GROUP BY (" + MediaStore.Images.ImageColumns.BUCKET_ID;
        String imgSelectionArgs[] = {
                "image/jpeg", "image/png"
        };
        Uri imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        private Cursor cursor;

        @Override
        protected ArrayList<SGFolder> doInBackground(Void... params) {
            ArrayList<SGFolder> folderList = new ArrayList<>();
            SGFolder sgFolder = new SGFolder();
            sgFolder.setId("0");
            sgFolder.setName("最近照片");
            folderList.add(sgFolder);
            cursor = getContentResolver().query(imgUri, folderProjection, selection, imgSelectionArgs, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
            if (cursor != null) {
                sgFolder = null;
                while (cursor.moveToNext()) {
                    sgFolder = new SGFolder();
                    sgFolder.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)));
                    sgFolder.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)));
                    sgFolder.setCount(cursor.getInt(cursor.getColumnIndex("count_id")));
                    folderList.add(sgFolder);
                }
                cursor.close();
            }
            return folderList;
        }

        @Override
        protected void onPostExecute(ArrayList<SGFolder> strings) {
            mFolderList = strings;
            mFolderPopWindow.setList(strings);
            super.onPostExecute(strings);
        }
    }

    /**
     * 查询图片
     */
    private class SelectImageTask extends AsyncTask<SGFolder, Void, ArrayList<CGImage>> {
        private SGFolder sgFolder;
        String imgProjection[] = {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.MIME_TYPE,
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.WIDTH,
                MediaStore.Images.ImageColumns.HEIGHT
        };

        Uri imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        private Cursor cursor;

        @Override
        protected ArrayList<CGImage> doInBackground(SGFolder... params) {
            sgFolder = params[0];
            if (sgFolder.getId().equals("0")) {
                String sort = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC limit 30";
                String imgSelection = MediaStore.Images.Media.MIME_TYPE + "=? OR +" + MediaStore.Images.Media.MIME_TYPE + " =?";
                String imgSelectionArgs[] = {
                        "image/jpeg", "image/png"
                };
                cursor = getContentResolver().query(imgUri, imgProjection, imgSelection, imgSelectionArgs, sort);
            } else {
                String sort = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC";
                String imgSelection = "(" + MediaStore.Images.Media.MIME_TYPE + "=? OR +" + MediaStore.Images.Media.MIME_TYPE + " =?)" + "AND " + MediaStore.Images.Media.BUCKET_ID + "=?";
                String imgSelectionArgs[] = {
                        "image/jpeg", "image/png", sgFolder.getId()
                };
                cursor = getContentResolver().query(imgUri, imgProjection, imgSelection, imgSelectionArgs, sort);
            }
            ArrayList<CGImage> cgImageList = new ArrayList<>();
            cgImageList.add(new CGImage());
            if (cursor != null) {
                CGImage cgImage;
                while (cursor.moveToNext()) {
                    cgImage = new CGImage();
                    if (!cursor.isNull(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))) {
                        cgImage.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)));
                    }
                    if (!cursor.isNull(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))) {
                        cgImage.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
                        cgImage.setThumbnails(cgImage.getPath());
                    }
                    if (!cursor.isNull(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE))) {
                        cgImage.setSize(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE)));
                    }
                    if (!cursor.isNull(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))) {
                        cgImage.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)));
                    }
                    if (!cursor.isNull(cursor.getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE))) {
                        cgImage.setType(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE)));
                    }
                    if (!cursor.isNull(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID))) {
                        cgImage.setBucketId(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID)));
                    }
                    if (!cursor.isNull(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))) {
                        cgImage.setBucketName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)));
                    }
                    if (!cursor.isNull(cursor.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH))) {
                        cgImage.setWidth(cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH)));
                        cgImage.setThumbnailsWidth(cgImage.getWidth());
                    }
                    if (!cursor.isNull(cursor.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT))) {
                        cgImage.setHeight(cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT)));
                        cgImage.setThumbnailsHeight(cgImage.getHeight());
                    }
                    cgImageList.add(cgImage);
                }
                cursor.close();
            }
            return cgImageList;
        }

        @Override
        protected void onPostExecute(ArrayList<CGImage> cgImages) {
            if (sgFolder.getCount() == 0) {
                titleTV.setText(sgFolder.getName());
            } else {
                titleTV.setText(sgFolder.getName() + "( " + sgFolder.getCount() + " )");
            }
            mStringList = cgImages;
            mAdapter.setStringList(cgImages);
            cancel(true);
        }


    }

}
