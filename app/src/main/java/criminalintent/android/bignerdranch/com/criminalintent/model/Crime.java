package criminalintent.android.bignerdranch.com.criminalintent.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Denis on 10.11.2015.
 */
public class Crime {
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";
    private static final String JSON_PHOTO = "photo";
    private static final String JSON_SUSPECT = "suspect";

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private Photo mPhoto;
    private String mSuspect;

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    private String mNumber;

    public Crime() {
        mId = UUID.randomUUID();
       mDate = new Date();
    }

    public Crime(JSONObject json)throws JSONException{
        mId = UUID.fromString(json.getString(JSON_ID));
        mTitle = json.getString(JSON_TITLE);
        mDate = new Date(json.getLong(JSON_DATE));
        mSolved = json.getBoolean(JSON_SOLVED);
        if (json.has(JSON_PHOTO)){
            mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
        }
        if(json.has(JSON_SUSPECT)){
            mSuspect = json.getString(JSON_SUSPECT);
        }
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean mSolved) {
        this.mSolved = mSolved;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public UUID getId() {
        return mId;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    public Photo getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Photo mPhoto) {
        this.mPhoto = mPhoto;
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject json = new JSONObject();
        json.put(JSON_ID,mId.toString());
        json.put(JSON_TITLE,mTitle);
        json.put(JSON_SOLVED,mSolved);
        json.put(JSON_DATE,mDate.getTime());
        if (mPhoto !=null)
            json.put(JSON_PHOTO,mPhoto.toJSON());
        json.put(JSON_SUSPECT,mSuspect);
        return json;
    }

    public void delPhoto (){
        mPhoto = null;
    }
    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String mSuspect) {
        this.mSuspect = mSuspect;
    }
}
