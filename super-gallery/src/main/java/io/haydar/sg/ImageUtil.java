package io.haydar.sg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by gjy on 16/4/28.
 */
public class ImageUtil {

    private LruCache<String, Bitmap> mBitmapLruCache;
    private int loadRes;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int MAX_POOL_SIZE = CPU_COUNT * 4 + 1;
    private static final long KEEP_ALIVE = 30;
    private DiskLruCache mDiskLruCache;
    private static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "图片加载#" + mCount.getAndIncrement());
        }
    });

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Result result = (Result) msg.obj;
            if (result.getUrl() == result.getCustomImageView().getTag().toString())
                result.getCustomImageView().setImageBitmap(result.getBitmap());
        }
    };

    /**
     * 初始化
     */

    private ImageUtil(Context mContext) {
        //初始化lrucache缓存
        mBitmapLruCache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 1024 / 8)) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };

        //初始化diskLruCache缓存
        File file = new File(mContext.getExternalCacheDir() + File.separator + "bitmap");
        if (file.exists()) {
            file.mkdirs();
        }
        try {
            mDiskLruCache = DiskLruCache.open(file, 1, 1, 20 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ImageUtil build(Context mContext) {
        return new ImageUtil(mContext);
    }


    /**
     * 加载图片
     * 先从内存缓存中获取,然后在从存储卡缓存中获取,最后才存储卡中获取原图并且压缩
     *
     * @param cgImage
     * @param mImageView
     */
    public void loadBitmap(final CGImage cgImage, final CustomImageView mImageView) {
        mImageView.setTag(cgImage.getThumbnails());
        mImageView.setImageResource(R.drawable.bg);
        final String key = hashKeyForUrl(hashKeyForUrl(cgImage.getThumbnails()));
        if (mBitmapLruCache.get(key) != null) {
            mImageView.setImageBitmap(mBitmapLruCache.get(key));
        } else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap;
                    Result result = new Result();
                    try {
                        bitmap = getDiskCacheBitmap(cgImage, key);
                        if (bitmap != null) {
                            mBitmapLruCache.put(key, bitmap);
                            result.setBitmap(bitmap);
                            result.setUrl(cgImage.getThumbnails());
                            result.setCustomImageView(mImageView);
                            mHandler.obtainMessage(1, result).sendToTarget();
                        } else {
                            if (mImageView.myWidth > 0 && Math.min(cgImage.getWidth(), cgImage.getHeight()) > mImageView.myWidth) {
                                int sampleSize = Math.min(cgImage.getWidth(), cgImage.getHeight()) / mImageView.myWidth;
                                BitmapFactory.Options mOptions = new BitmapFactory.Options();
                                mOptions.inSampleSize = sampleSize;
                                bitmap = BitmapFactory.decodeFile(cgImage.getThumbnails(), mOptions);
                            } else {
                                bitmap = BitmapFactory.decodeFile(cgImage.getThumbnails());
                            }
                            mBitmapLruCache.put(key, bitmap);
                            putDiskCache(key, bitmap);
                            result.setBitmap(bitmap);
                            result.setUrl(cgImage.getThumbnails());
                            result.setCustomImageView(mImageView);
                            mHandler.obtainMessage(1, result).sendToTarget();
                        }
                    } catch (Exception e) {
                        Logger.d(cgImage.getThumbnails());
                    }
                }
            };
            THREAD_POOL_EXECUTOR.execute(runnable);
        }
    }


    /**
     * 从存储卡缓存中获取
     *
     * @param cgImage
     * @param key
     * @return
     */
    private Bitmap getDiskCacheBitmap(CGImage cgImage, String key) {
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        synchronized (mDiskLruCache) {
            try {
                snapshot = mDiskLruCache.get(key);
                InputStream inputStream = null;
                if (snapshot != null) {
                    inputStream = snapshot.getInputStream(0);
                    if (inputStream != null) {
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                        snapshot.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;

    }

    /**
     * discache key MD5加密
     *
     * @param thumbnails
     * @return
     */
    private String hashKeyForUrl(String thumbnails) {
        String key = null;
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(thumbnails.getBytes());
            key = bytesToHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            key = String.valueOf(thumbnails.hashCode());
        }
        return key;
    }

    private String bytesToHexString(byte[] digest) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            String hex = Integer.toHexString(0xff & digest[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }


    /**
     * bitmap加入存储卡缓存
     *
     * @param key
     * @param bitmap
     */
    private void putDiskCache(String key, Bitmap bitmap) {
        synchronized (mDiskLruCache) {
            OutputStream out = null;
            DiskLruCache.Editor editor = null;
            try {
                editor = mDiskLruCache.edit(key);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (editor != null) {
                try {
                    out = editor.newOutputStream(0);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    editor.commit();
                    out.close();
                    mDiskLruCache.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        editor.abort();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }


    public void setLoadImage(int ic_launcher) {
        this.loadRes = ic_launcher;
    }


    /**
     * 清除缓存
     */
    public void clearCache() {
        if (mBitmapLruCache != null) {
            mBitmapLruCache.evictAll();
            mBitmapLruCache = null;
        }
    }
}
