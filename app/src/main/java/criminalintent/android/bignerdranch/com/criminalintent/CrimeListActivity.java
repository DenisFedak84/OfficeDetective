package criminalintent.android.bignerdranch.com.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import criminalintent.android.bignerdranch.com.criminalintent.fragment.CrimeFragment;
import criminalintent.android.bignerdranch.com.criminalintent.fragment.CrimeListFragment;
import criminalintent.android.bignerdranch.com.criminalintent.model.Crime;

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks,CrimeFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected String lable() {
        String lable = getResources().getString(R.string.crimes_list_title);
        return lable;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    // если используется телефонный интерфейс - запустить экземпляр CrimePagerActivity
    // если используется планшетный интерфейс - поместить CrimeFragment в detailFragmentContainer
    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detailFragmentContainer) == null) {
            Intent i = new Intent(this,CrimePagerActivity.class);
            i.putExtra(CrimeFragment.EXTRA_CRIME_ID,crime.getId());
            startActivity(i);
        }else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            if (oldDetail!= null){
                ft.remove(oldDetail);
            }
            ft.add(R.id.detailFragmentContainer,newDetail);
            ft.commit();
        }
    }


    @Override
    public void onCrimeUpdated(Crime crime) {
        FragmentManager fm = getSupportFragmentManager();
        CrimeListFragment listFragment = (CrimeListFragment)fm.findFragmentById(R.id.fragmentContainer);
        listFragment.updateUI();
    }
}