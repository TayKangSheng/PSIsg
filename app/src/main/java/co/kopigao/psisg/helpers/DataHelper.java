package co.kopigao.psisg.helpers;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;

/***************************************************************************************************
 *
 *      This Class is for managing Shared Preference Data
 *      Through this class, we can build a central platform
 *      to access and save data.
 *
 **************************************************************************************************/
public class DataHelper {
    private static String data_key = "data_key";
    private static String map_data_key = "map_data_key";
    private static String active_data_key = "active";
    private SharedPreferences mSharedPreferences;

    public DataHelper(Context mContext){
        Log.d("PSIDebug", "DataHelper Constructor");

        mSharedPreferences = mContext.getSharedPreferences("PSI SG", Context.MODE_PRIVATE);
    }

    public String getMapData(){
        Log.d("PSIDebug", "DataHelper getMapData");

        return getData(map_data_key);
    }

    public void setMapData(String toSet){
        Log.d("PSIDebug", "DataHelper setMapData");

        setData(map_data_key, toSet);
    }

    public String getPSIData(){
        Log.d("PSIDebug", "DataHelper getPSIData");

        return getData(data_key);
    }

    public void setPSIData(String toSet){
        Log.d("PSIDebug", "DataHelper setPSIData");

        setData(data_key, toSet);
    }

    public boolean getActive(){
        Log.d("PSIDebug", "DataHelper setPSIData");

        return mSharedPreferences.getBoolean(active_data_key, false);
    }

    public void setActive(boolean toSet){
        Log.d("PSIDebug", "DataHelper setPSIData");

        setData(active_data_key, toSet);
    }


    private String getData(String key){
        Log.d("PSIDebug", "DataHelper getData");

        return mSharedPreferences.getString(key, null);
    }

    private void setData(String key, String value){
        Log.d("PSIDebug", "DataHelper setData");

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void setData(String key, boolean value){
        Log.d("PSIDebug", "DataHelper setData");

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


}
