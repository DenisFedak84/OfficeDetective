package criminalintent.android.bignerdranch.com.criminalintent.model;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import criminalintent.android.bignerdranch.com.criminalintent.CriminalIntentJSONSerializer;

/**
 * Created by Denis on 18.11.2015.
 */
public class CrimeLab {
    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crimes.json";

    private static CrimeLab sCrimeLab;
    private Context mAppContext;
    private ArrayList<Crime> mCrimes;
    private CriminalIntentJSONSerializer mSerializer;



    private CrimeLab(Context appContext) {
        mAppContext = appContext;
        mSerializer = new CriminalIntentJSONSerializer(mAppContext,FILENAME);
        try {
            mCrimes = mSerializer.loadCrime();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            mCrimes = new ArrayList<Crime>();
            Log.e(TAG,"Error loading crime: ",e);
        }
    }

    public static CrimeLab get(Context c) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(c.getApplicationContext());
        }
        return sCrimeLab;
    }

    public ArrayList<Crime> getmCrimes() {
        return mCrimes;
    }
// возвращаем одно приступление по id
    public Crime getCrime(UUID id) {
        for (Crime c : mCrimes) {
            if (c.getId().equals(id))
                return c;
        }
        return null;
    }

public void addCrime(Crime c){
    mCrimes.add(c);
}

    public boolean saveCrimes (){

        try {
            mSerializer.saveCrimes(mCrimes);
            Log.d(TAG, "crimes saved to file");
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "Error saved crimes:" +e);
            return false;
        }
    }

    public void deleteCrime (Crime c){
        mCrimes.remove(c);
    }
}

