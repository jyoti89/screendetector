package screendetector.example.com.screendetectorapp.library;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;

import screendetector.example.com.screendetectorapp.model.ScreenDetail;


/**
 * Created by Ayaz  0/23/17.
 */

public class DetectScreen {
    private final HandlerThread mHandlerThread;
    private final Handler mHandler;
    private final ContentResolver mContentResolver;
    private final ContentObserver mContentObserver;
    private final Listener mListener;

    public DetectScreen(ContentResolver contentResolver, Listener listener) {
        mHandlerThread = new HandlerThread("DetectScreen");
        mHandlerThread.start();

        mHandler = new Handler(mHandlerThread.getLooper());
        mContentResolver = contentResolver;
        mContentObserver = new DetectScreenObserver(mHandler, contentResolver, listener);
        mListener = listener;
    }

    public void register() {
        mContentResolver.registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                mContentObserver
        );
    }

    public void unregister() {
        mContentResolver.unregisterContentObserver(mContentObserver);
    }


    public interface Listener {
        void onScreenShotTaken(ScreenDetail screenDetail);
    }
}
