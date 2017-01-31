package co.kopigao.psisg.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONHelper {
    Context mContext;
    JSONObject mData;
    JSONArray mLocationMetaData;
    JSONArray mReadingsData;

    public JSONHelper(Context context, String toParse){
        try {
            mData = new JSONObject(toParse);
            mLocationMetaData = mData.getJSONArray("region_metadata");
            mReadingsData = mData.getJSONArray("items");

        } catch (Exception e){
            Log.d("PSISG", "JSONHelper", e);

            Toast.makeText(mContext, "Error receiving information", Toast.LENGTH_SHORT).show();
        }
    }

    public JSONArray getLocationMetaData(){
        if (mLocationMetaData == null){
            return null;
        }
        return mLocationMetaData;
    }

    public JSONArray getReadingsData(){
        if (mReadingsData == null){
            return null;
        }
        return mReadingsData;
    }

}
