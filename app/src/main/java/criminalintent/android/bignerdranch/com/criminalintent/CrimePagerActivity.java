package criminalintent.android.bignerdranch.com.criminalintent;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.UUID;

import criminalintent.android.bignerdranch.com.criminalintent.fragment.CrimeFragment;
import criminalintent.android.bignerdranch.com.criminalintent.model.Crime;
import criminalintent.android.bignerdranch.com.criminalintent.model.CrimeLab;

public class CrimePagerActivity extends AppCompatActivity implements CrimeFragment.Callbacks{
    public static final String TAG = CrimePagerActivity.class.getSimpleName();
    ViewPager mViewPager;
    private ArrayList<Crime> mCrimes;
    android.support.v7.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // получаем id выбраного элемента списка
        UUID crimeId = (UUID) getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
        Crime crime = CrimeLab.get(this).getCrime(crimeId);

//        actionBar = getActionBar();
        actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(crime.getTitle());
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        }

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        mCrimes = CrimeLab.get(this).getmCrimes();
        FragmentManager fm = getSupportFragmentManager();

        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        // перебераем все преступления и находим то которое соответствует crimeId
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                               @Override
                                               public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                               }

                                               @Override
                                               public void onPageSelected(int position) {
                                                   Crime crime = mCrimes.get(position);
//                                                   if (crime.getTitle() != null) {
//                                                       setTitle(crime.getTitle());
//                                                   }
                                                   if (actionBar != null) {
                                                       actionBar.setTitle(crime.getTitle());
                                                   }
                                               }

                                               @Override
                                               public void onPageScrollStateChanged(int state) {

                                               }
                                           }

        );
    }

    @Override
    public void onCrimeUpdated(Crime crime) {

    }
}
