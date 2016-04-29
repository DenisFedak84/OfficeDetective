package criminalintent.android.bignerdranch.com.criminalintent.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Photo {
    private static final String JSON_FILENAME = "filename";
    private  String mFilename;
    int photoOrientation;

    // создание объекта Photo, представляющего файл на диске
    public Photo (String filename){
        mFilename = filename;
    }

    // используется классом Ctime для сохранения и загрузки свойств типа Photo
    public Photo(JSONObject json)throws JSONException{
        mFilename = json.getString(JSON_FILENAME);
    }

    // загрузка в JSON
    public JSONObject toJSON() throws JSONException{
        JSONObject json = new JSONObject();
        json.put(JSON_FILENAME,mFilename);
        return json;
    }

    public String getFilename() {
        return mFilename;
    }

    public int getPhotoOrientation() {
        return photoOrientation;
    }

    public void setPhotoOrientation(int photoOrientation) {
        this.photoOrientation = photoOrientation;
    }

    public void setFilename(String mFilename) {
        this.mFilename = mFilename;
    }
}
