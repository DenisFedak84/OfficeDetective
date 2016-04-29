package criminalintent.android.bignerdranch.com.criminalintent.fragment;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import criminalintent.android.bignerdranch.com.criminalintent.utils.PictureUtils;

public class ImageFragment extends DialogFragment {
    public static final String EXTRA_IMAGE_PATH = "com.bignerdbranch.android.criminalintent.image_path";
    public static final String EXTRA_IMAGE_DEGREE = "com.bignerdbranch.android.criminalintent.image_degree";
    private ImageView mImageView;

    public static ImageFragment newInstance (String imagePath,int degree){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_PATH,imagePath);
        args.putSerializable(EXTRA_IMAGE_DEGREE,degree);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE,0);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mImageView = new ImageView(getActivity());
        String path = (String)getArguments().getSerializable(EXTRA_IMAGE_PATH);
        int degree = (Integer)getArguments().getSerializable(EXTRA_IMAGE_DEGREE);
        BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(),path);
        Bitmap bitmap = image.getBitmap();
        bitmap = PictureUtils.rotateImageIfRequired(bitmap,degree);
        image = new BitmapDrawable(bitmap);

        mImageView.setImageDrawable(image);
        return mImageView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PictureUtils.cleanImageView(mImageView);
    }
}
