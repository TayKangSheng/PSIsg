package co.kopigao.psisg.helpers;

import android.util.Log;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**************************************************
 *
 *      This is a blocking HTTPRequest class
 *
 **************************************************/
public class HttpRequestHelper {

    /**************************************************
     *
     *      Unused Constructor.
     *
     **************************************************/
    public HttpRequestHelper(){ }

    /***********************************************************************************************
     *
     *      GET Request
     *          Input:
     *              (String) address: the basic url
     *          Returns:
     *              (JSONObject) Result of GET Request
     *              (Null) Null if exception is thrown.
     *
     **********************************************************************************************/
    public JSONObject HTTPGet(String address){
        Log.d("PSIDebug", "HttpRequestHelper HTTPGet");

        URL url = null;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(address);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();

            InputStream inStream = urlConnection.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
            String temp, response = "";
            while ((temp = bReader.readLine()) != null) {
                response += temp;
            }
            return (JSONObject) new JSONTokener(response).nextValue();
        } catch (Exception e){
            Log.d("PSIDebug", "HttpRequestHelper", e);
            return null;
        }
    }

    /***********************************************************************************************
     *
     *      GET Request
     *          Input:
     *              (String) address: the basic url
     *              (String[]) params: all key/value pairs
     *                  eg. {"key", "value", "key", "value"...}
     *          Returns:
     *              (JSONObject) Result of GET Request
     *              (Null) Null if exception is thrown.
     *
     **********************************************************************************************/
    public JSONObject HTTPGet(String address, String[] headers){
        Log.d("PSIDebug", "HttpRequestHelper HTTPGet");

        URL url = null;
        HttpURLConnection urlConnection = null;
        InputStream inStream = null;

        try {
            url = new URL(address);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            for (int i=0 ; i<headers.length ; i+=2){
                urlConnection.setRequestProperty(headers[i], headers[i+1]);
            }
            urlConnection.connect();

            inStream = urlConnection.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
            String temp, response = "";
            while ((temp = bReader.readLine()) != null) {
                response += temp;
            }
            return (JSONObject) new JSONTokener(response).nextValue();

        } catch (Exception e){
            Log.d("PSIDebug", "HttpRequestHelper", e);
            return null;
        }
    }

    /***********************************************************************************************
     *
     *      GET Request
     *          Input:
     *              (String) address: the basic url
     *              (String[]) params: all keys/values pairs
     *                  eg. {"key", "value", "key", "value"...}
     *              (String[]) queries: all key/value pairs
     *                  eg. {"key", "value", "key", "value"...}
     *          Returns:
     *              (JSONObject) Result of GET Request
     *              (Null) Null if exception is thrown.
     *
     **********************************************************************************************/
    public JSONObject HTTPGet(String address, String[] headers, String[] queries){
        Log.d("PSIDebug", "HttpRequestHelper HTTPGet");

        URL url = null;
        HttpURLConnection urlConnection = null;

        try {
            if (queries.length > 0){
                address += "?";
                for (int i=0 ; i<queries.length ; i+=2){
                    address += queries[i];
                    address += "=";
                    address += queries[i+1];
                }
            }
            url = new URL(address);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            for (int i=0 ; i<headers.length ; i+=2){
                urlConnection.setRequestProperty(headers[i], headers[i+1]);
            }
            urlConnection.connect();

            InputStream inStream = urlConnection.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
            String temp, response = "";
            while ((temp = bReader.readLine()) != null) {
                response += temp;
            }
            return (JSONObject) new JSONTokener(response).nextValue();
        } catch (Exception e){
            Log.d("PSIDebug", "HttpRequestHelper", e);
            return null;
        }
    }
}
