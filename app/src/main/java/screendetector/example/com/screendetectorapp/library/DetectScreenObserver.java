package screendetector.example.com.screendetectorapp.library;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import screendetector.example.com.screendetectorapp.model.ScreenDetail;

/**
 * Created by Ayaz  on 9/23/17.
 */

public class DetectScreenObserver extends ContentObserver {


    private final String MEDIA_EXTERNAL_URI_STRING = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString();
    private final String FILE_NAME_PREFIX = "screenshot";
    private final String PATH_SCREENSHOT = "screenshots/";

    private ContentResolver mContentResolver;
    private final DetectScreen.Listener mListener;
    String TAG="gtm";
    private static final String EXTERNAL_CONTENT_URI_MATCHER =
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString();
    private static final String[] PROJECTION = new String[] {
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID,
    };
    private static final String SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";
    private static final long DEFAULT_DETECT_WINDOW_SECONDS = 10;


    public DetectScreenObserver(Handler handler, ContentResolver contentResolver, DetectScreen.Listener listener) {
        super(handler);
        mContentResolver = contentResolver;
        mListener = listener;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Log.d(TAG, "onChange: " + selfChange + ", " + uri.toString());

        if (uri.toString().startsWith(EXTERNAL_CONTENT_URI_MATCHER)) {
            Cursor cursor = null;
            try {
                cursor = mContentResolver.query(uri, PROJECTION, null, null,
                        SORT_ORDER);
                if (cursor != null && cursor.moveToFirst()) {
                    String path = cursor.getString(
                            cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    long dateAdded = cursor.getLong(cursor.getColumnIndex(
                            MediaStore.Images.Media.DATE_ADDED));
                    long currentTime = System.currentTimeMillis() / 1000;
                    Log.d(TAG, "path: " + path + ", dateAdded: " + dateAdded +
                            ", currentTime: " + currentTime);
                    if (matchPath(path) && matchTime(currentTime, dateAdded)) {
                      handleItem(uri);
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "open cursor fail");
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        super.onChange(selfChange, uri);
    }/*{
        super.onChange(selfChange, uri);
       *//* if (isSingleImageFile(uri)) {
            handleItem(uri);
        }*//*

    }*/

    private static boolean matchPath(String path) {
        return path.toLowerCase().contains("screenshot") || path.contains("截屏") ||
                path.contains("截图");
    }
    private static boolean matchTime(long currentTime, long dateAdded) {
        return Math.abs(currentTime - dateAdded) <= DEFAULT_DETECT_WINDOW_SECONDS;
    }

    private void handleItem(Uri uri) {
        Cursor cursor = null;
        try {
            cursor = mContentResolver.query(uri, PROJECTION, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                final ScreenDetail screenDetail = generateScreenshotDataFromCursor(cursor);
                if (screenDetail != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onScreenShotTaken(screenDetail);
                        }
                    });
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private ScreenDetail generateScreenshotDataFromCursor(Cursor cursor) {
        final long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
        final String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
        final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

        if (isPathScreenshot(path) && isFileScreenshot(fileName)) {
            return new ScreenDetail(id, fileName, path);
        } else {
            return null;
        }
    }

    private boolean isFileScreenshot(String fileName) {
        return fileName.toLowerCase().startsWith(FILE_NAME_PREFIX);
    }

    private boolean isPathScreenshot(String path) {
        return path.toLowerCase().contains(PATH_SCREENSHOT);
    }
}
