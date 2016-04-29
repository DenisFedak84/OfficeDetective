package criminalintent.android.bignerdranch.com.criminalintent.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import criminalintent.android.bignerdranch.com.criminalintent.CrimeCameraActivity;
import criminalintent.android.bignerdranch.com.criminalintent.R;
import criminalintent.android.bignerdranch.com.criminalintent.model.Crime;
import criminalintent.android.bignerdranch.com.criminalintent.model.CrimeLab;
import criminalintent.android.bignerdranch.com.criminalintent.model.Photo;
import criminalintent.android.bignerdranch.com.criminalintent.utils.PictureUtils;

/**
 * Created by Denis on 11.11.2015.
 */
public class CrimeFragment extends Fragment {
    private static final String TAG = "CrimeFragment";
    public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_IMAGE = "image";
    private static final String CHOICE_PARAMETR = "parametr";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 101;
    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_DEGREE = "myDegree";
    private Crime mCrime;
    private TextView mTitleField;
    // private Button mDateButton;
    private TextView mDateButton;
    private CheckBox mSolvedCheckBox;
    private LinearLayout llCrime;
    private FloatingActionButton mPhotoButton;
    private ImageView mPhotoView;
    private FloatingActionButton reportButton;
    private FloatingActionButton mSuspectButton;
    private FloatingActionButton mCallButton;
    private TextView tvSuspect;
    int orientation;
    int degree ;
    private Callbacks mCallbacks;
    private SharedPreferences mSettings;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mCrime = new Crime();
        UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mSettings = getActivity().getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE);
        degree = mSettings.getInt(APP_PREFERENCES_DEGREE,-1);
    }

    @TargetApi(11)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_rev01, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (getActivity().getActionBar() != null) {
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
//                    getActivity().getActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
                }
            }
        }

        mTitleField = (TextView) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                mCallbacks.onCrimeUpdated(mCrime);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (TextView) v.findViewById(R.id.crime_date);
//        android.text.format.DateFormat df = new android.text.format.DateFormat();
//        mDateButton.setText( df.format("EEEE, MMM dd, yyyy", mCrime.getDate()));
        // mDateButton.setText(mCrime.getDate().toString());
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = new DatePickerFragment().newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);

            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                mCallbacks.onCrimeUpdated(mCrime);
            }
        });

        llCrime = (LinearLayout) v.findViewById(R.id.ll_crime);
        llCrime.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ActionMode.Callback callback = new ActionMode.Callback() {

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        MenuInflater inflater = mode.getMenuInflater();
                        inflater.inflate(R.menu.crime_list_item_context, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_item_delete_crime:
                                CrimeLab crimeLab = CrimeLab.get(getActivity());
                                crimeLab.deleteCrime(mCrime);
                                getActivity().finish();
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {

                    }
                };
                ActionMode mode = getActivity().startActionMode(callback);
                return false;
            }
        });

        mPhotoButton = (FloatingActionButton) v.findViewById(R.id.fab_photo);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        // если камера недоступна блокируем кнопку
        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT))) {
            mPhotoButton.setEnabled(false);
        }

        mPhotoView = (ImageView) v.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo p = mCrime.getPhoto();
                if (p == null)
                    return;
                FragmentManager fm = getActivity().getSupportFragmentManager();
                String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path, degree).show(fm, DIALOG_IMAGE);
            }
        });

        mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ActionMode.Callback callback = new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        MenuInflater inflater = mode.getMenuInflater();
                        inflater.inflate(R.menu.crime_list_item_context, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_item_delete_crime:
                                String filename = mCrime.getPhoto().getFilename();
                                File file = new File(getActivity().getFileStreamPath(filename).getAbsolutePath());
                                boolean deleted = file.delete();
                                Log.i(TAG, "Long Click deleted =" + deleted);
                                mPhotoView.setImageDrawable(null);
                                //  mCrime.getPhoto().setFilename(null);
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {

                    }
                };
                ActionMode mode = getActivity().startActionMode(callback);
                return false;
            }
        });

        reportButton = (FloatingActionButton) v.findViewById(R.id.fab_message);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // текст отчета и строка темы включена в дополенине
                // активность реагирующая на интент знает эти константы и что с ними надо делать
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        mSuspectButton = (FloatingActionButton) v.findViewById(R.id.fab_search);
        tvSuspect = (TextView) v.findViewById(R.id.tv_Suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            tvSuspect.setText(getResources().getString(R.string.suspect) + " " + mCrime.getSuspect());
        }

        mCallButton = (FloatingActionButton) v.findViewById(R.id.fab_call);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    String telephone = mCrime.getNumber();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telephone));
                    getActivity().startActivity(intent);
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                        Toast.makeText(getActivity(), "Read contacts permissions required to read contacts", Toast.LENGTH_LONG).show();
                    }
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
                }
            }
        });

        return v;
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            mCallbacks.onCrimeUpdated(mCrime);
            updateDate();
        } else if (requestCode == REQUEST_PHOTO) {
            // создание нового объекта Photo и связывание его с Crime
            String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            orientation = data.getIntExtra(CrimeCameraFragment.EXTRA_CRIME_FRAGMENT_ORIENTATION, -1);
            if (filename != null) {
                Log.i(TAG, "filename " + filename);

                if (mCrime.getPhoto() != null) {
                    String oldFileName = mCrime.getPhoto().getFilename();
                    if (!filename.equals(oldFileName)) {
                        File file = new File(getActivity().getFileStreamPath(oldFileName).getAbsolutePath());
                        boolean deleted = file.delete();
                        Log.i(TAG, "deleted =" + deleted);
                    }
                }

                Photo p = new Photo(filename);
                mCrime.setPhoto(p);
                mCallbacks.onCrimeUpdated(mCrime);
                p.setPhotoOrientation(orientation);
                showPhoto();
            }
        } else if (requestCode == REQUEST_CONTACT) {
            Uri uri = data.getData();
            Log.d(TAG, "Contact uri =" + uri);
            // достать из таблицы только поле "имя"
//            String [] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME};
//            Cursor c = getActivity().getContentResolver().query(uri,queryFields,null,null,null);
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor c = getActivity().getContentResolver().query(uri, queryFields, null, null, null);

            if (c.getCount() == 0) {
                c.close();
                return;
            }
            // непосредственно достаем имя
            c.moveToFirst();
            int numberIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String number = c.getString(numberIndex);
            Log.d(TAG, "Number = " + number);
            String suspect = c.getString(0);
            mCrime.setSuspect(suspect);
            mCrime.setNumber(number);
            mCallbacks.onCrimeUpdated(mCrime);
            tvSuspect.setText(getResources().getString(R.string.suspect) + " " + suspect);
            c.close();
        }
    }

    private void updateDate() {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        mDateButton.setText(DateFormat.format("EEEE, MMM dd, yyyy", mCrime.getDate()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPhoto() {
        // назначение изображения, полученного на основе фотографии
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;
        if (p != null) {
            String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
            Log.d(TAG, "Path: " + path);
            b = PictureUtils.getScaledDrawable(getActivity(), path);
            // int orientation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            orientation = p.getPhotoOrientation();
            Log.d(TAG, "Orientation = " + orientation);

                degree = PictureUtils.getDegree(orientation);
                saveDegree(degree);
                Bitmap bitmap = b.getBitmap();
                bitmap = PictureUtils.rotateImageIfRequired(bitmap, degree);
                bitmap = PictureUtils.cropToSquare(bitmap);
                b = new BitmapDrawable(bitmap);
        }
        mPhotoView.setImageDrawable(b);
    }

    private void saveDegree(int degree) {

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_DEGREE,degree);
        editor.apply();
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mCrime.getPhoto() != null)
            showPhoto();
    }

    @Override
    public void onStop() {
        super.onStop();
        //if (mCrime.getPhoto().getFilename()!= null)
        //  PictureUtils.cleanImageView(mPhotoView);
    }

    // создаем отчет преступления
    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }


}
