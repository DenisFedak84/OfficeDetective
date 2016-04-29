package criminalintent.android.bignerdranch.com.criminalintent;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.WindowManager;

import criminalintent.android.bignerdranch.com.criminalintent.fragment.CrimeCameraFragment;

public class CrimeCameraActivity extends SingleFragmentActivity {
    private static final String TAG = "CrimeActivity";
    public  static final int ORIENTATION_PORTRAIT_NORMAL =6 ;
    public  static final int ORIENTATION_PORTRAIT_INVERTED = 8;
    public  static final int ORIENTATION_LANDSCAPE_NORMAL = 1;
    public  static final int ORIENTATION_LANDSCAPE_INVERTED = 3;
    private OrientationEventListener mOrientationEventListener;

    private int myOrientation = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // скрытие action bar
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        // скрытие cистемную панель
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }

    @Override
    protected String lable() {
        String lable = getResources().getString(R.string.crimes_camera_title);
        return lable;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mOrientationEventListener == null){
            mOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
                @Override
                public void onOrientationChanged(int orientation) {
                    if (orientation>=315 || orientation<45){
                        if (myOrientation != ORIENTATION_PORTRAIT_NORMAL){
                            myOrientation = ORIENTATION_PORTRAIT_NORMAL;
                            Log.d (TAG, "Orientation = " + ORIENTATION_PORTRAIT_NORMAL);
                            setMyOrientation(myOrientation);
                        }
                    }
                    else if (orientation<315 && orientation>=225){
                        if (myOrientation != ORIENTATION_LANDSCAPE_NORMAL){
                            myOrientation = ORIENTATION_LANDSCAPE_NORMAL;
                            Log.d (TAG, "Orientation = " + ORIENTATION_LANDSCAPE_NORMAL);
                            setMyOrientation(myOrientation);
                        }
                    }
                    else if (orientation<225 && orientation >= 135){
                        if (myOrientation != ORIENTATION_PORTRAIT_INVERTED){
                            myOrientation = ORIENTATION_PORTRAIT_INVERTED;
                            Log.d (TAG, "Orientation = " + ORIENTATION_PORTRAIT_INVERTED);
                            setMyOrientation(myOrientation);
                        }
                    } else if (myOrientation != ORIENTATION_LANDSCAPE_INVERTED) {
                        myOrientation = ORIENTATION_LANDSCAPE_INVERTED;
                        Log.d (TAG, "Orientation = " + ORIENTATION_LANDSCAPE_INVERTED);
                        setMyOrientation(myOrientation);
                    }
                }
            };
        }
        if (mOrientationEventListener.canDetectOrientation()){
            mOrientationEventListener.enable();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mOrientationEventListener.disable();
    }

    public int getMyOrientation() {
        return myOrientation;
    }

    public void setMyOrientation(int myOrientation) {
        this.myOrientation = myOrientation;
    }
}
