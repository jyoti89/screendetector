package screendetector.example.com.screendetectorapp.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import screendetector.example.com.screendetectorapp.R;
import screendetector.example.com.screendetectorapp.library.DetectScreen;
import screendetector.example.com.screendetectorapp.model.ScreenDetail;

public class MainActivity extends AppCompatActivity {
    private DetectScreen mDetectScreen;

    private TextView mText;
    private ImageView mImage;
    private int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mText = (TextView) findViewById(R.id.imageName);
        mImage = (ImageView) findViewById(R.id.imageView);


      if(checkPermission()) {
         setScreenDetector();
      } else{
          requestPermission();
          setScreenDetector();
      }
    }

    private void setScreenDetector() {
        mDetectScreen = new DetectScreen(getContentResolver(), new DetectScreen.Listener() {
            @Override
            public void onScreenShotTaken(ScreenDetail screenshotData) {
                mText.setText(screenshotData.getFileName());
                Uri uri = Uri.parse(screenshotData.getPath());
                mImage.setImageURI(uri);
            }
        });
    }

    //permissions in android Marshmallow
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /*
* Method is used to Request Permission
* */
    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDetectScreen.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDetectScreen.unregister();
    }
}
