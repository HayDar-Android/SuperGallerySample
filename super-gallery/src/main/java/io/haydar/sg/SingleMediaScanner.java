package io.haydar.sg;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

/**
 * Created by gjy on 16/5/9.
 */
public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    private MediaScannerConnection mMSC;
    private File mFile;
    private Context context;

    public SingleMediaScanner(Context context, File f) {
        this.context = context;
        mFile = f;
        mMSC = new MediaScannerConnection(context, this);
        mMSC.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mMSC.scanFile(mFile.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mMSC.disconnect();
    }
}
