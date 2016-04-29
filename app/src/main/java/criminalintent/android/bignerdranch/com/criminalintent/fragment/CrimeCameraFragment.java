package criminalintent.android.bignerdranch.com.criminalintent.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import criminalintent.android.bignerdranch.com.criminalintent.CrimeCameraActivity;
import criminalintent.android.bignerdranch.com.criminalintent.R;

public class CrimeCameraFragment extends android.support.v4.app.Fragment {
    public static final int PERMISSION_REQUEST_CAMERA = 101;
    public static final String EXTRA_PHOTO_FILENAME =
            "com.bignerdranch.android.criminalintent.photo_filename";
    public static final String EXTRA_ORIENTATION = "orientation";
    public static final String EXTRA_CRIME_FRAGMENT_ORIENTATION = "crime orientation";
    private static final String TAG = "CrimeCameraFragment";
    private android.hardware.Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;
    Camera.ShutterCallback mShutterCallback;
    Camera.PictureCallback mPictureCallback;
    private int orientation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_camera, container, false);

        mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        final Button takePictureButton = (Button) v.findViewById(R.id.crime_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null) {
                    mCamera.takePicture(mShutterCallback,null,mPictureCallback);
                }
            }
        });

        mSurfaceView = (SurfaceView) v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // приказываем камере использовать указанную поверхность как область предварительного просмотра
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error setting up preview display", e);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

                // размер поверхности изменился; обновить размер области предварительного просмотра камеры
                if (mCamera == null) return;
                Camera.Parameters parameters = mCamera.getParameters();
                // размер видоискателя
                Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), w, h);
                parameters.setPreviewSize(s.width, s.height);
                //  размер создаваемого изображения
                s = getBestSupportedSize(parameters.getSupportedPictureSizes(),w,h);
                parameters.setPictureSize(s.width,s.height);
                mCamera.setParameters(parameters);
                try {
                    mCamera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "Could not start preview", e);
                    mCamera.release();
                    mCamera = null;
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // дальнейший выавод на поверхность невозможен прекращаем предварительный просмотр
                if (mCamera != null) {
                    mCamera.stopPreview();
                }
            }
        });

        mShutterCallback = new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
                // отображение индикатора прогресса
                mProgressContainer.setVisibility(View.VISIBLE);
            }
        };

        mPictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                // создание имени файла
                String fileName = UUID.randomUUID().toString() + ".jpg";
                // сохранение файлов jpeg  на диске
                FileOutputStream os = null;
                boolean success = true;
                try {
                    os = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
                    os.write(data);
                } catch (Exception e) {
                    Log.e(TAG, "Error writing to file" + fileName, e);
                    success = false;
                } finally {
                    try {
                        if (os != null)
                            os.close();
                    } catch (Exception e) {
                        Log.e(TAG, "Error closing file" + fileName, e);
                        success = false;
                    }
                }
                if (success) {
                    Log.i(TAG, "JPEG saved at" + fileName);
                    // Имя файла фотографии записывается в интент результата
                    CrimeCameraActivity activity = (CrimeCameraActivity)getActivity();
                    orientation = activity.getMyOrientation();
                    Log.d (TAG,"CrimeCameraFragment orientation: " + orientation);
                    Intent i = new Intent();
                    i.putExtra(EXTRA_PHOTO_FILENAME,fileName);
                    i.putExtra(EXTRA_CRIME_FRAGMENT_ORIENTATION, orientation);
                    getActivity().setResult(Activity.RESULT_OK,i);
                }else {
                    getActivity().setResult(Activity.RESULT_CANCELED);
                }
                getActivity().finish();
            }
        };
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mCamera = android.hardware.Camera.open(0);
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(getActivity(), "Camera permissions required to take photo", Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
        // mCamera = android.hardware.Camera.open(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mCamera = android.hardware.Camera.open(0);
                } else {
                    Toast.makeText(getActivity(), "Camera permissions has not been granted", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int hight) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }

}
