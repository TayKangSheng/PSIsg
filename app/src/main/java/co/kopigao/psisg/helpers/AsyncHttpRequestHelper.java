package co.kopigao.psisg.helpers;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;


/***************************************************************************************************
 *
 *      This is a Async HTTPRequest class
 *
 **************************************************************************************************/
public class AsyncHttpRequestHelper extends AsyncTask<Bundle, Integer, JSONObject> {

    private AsyncResponseListener callback;

    /***********************************************************************************************
     *
     *      Custom constructor to set callback target.
     *
     **********************************************************************************************/
    public AsyncHttpRequestHelper(AsyncResponseListener toCallback){
        Log.d("PSIDebug", "AsyncHttpRequestHelper Constructor");

        callback = toCallback;
    }

    /***********************************************************************************************
     *
     *      All background computation codes are in this method. After computation is done,
     *      call to listener is made.
     *
     *      Input:
     *          (bundles[]) Sets of data to be examined and worked on.
     *      Return:
     *          (null) The return of this method is not important. The results is return through
     *                  the AsyncResponse method.
     *
     **********************************************************************************************/
    @Override
    protected JSONObject doInBackground(Bundle... bundles) {
        Log.d("PSIDebug", "AsyncHttpRequestHelper doInBackground");

        String address = bundles[0].getString("address");
        String[] headers = bundles[0].getStringArray("headers");
        String[] queries = bundles[0].getStringArray("queries");
        String AsyncTaskErrorMessage = bundles[0].getString("errorMessage");
        String tag = bundles[0].getString("tag");

        try {
            JSONObject results;
            HttpRequestHelper httpRequestHelper = new HttpRequestHelper();

            if (headers != null) {
                if (queries != null) {
                    results = httpRequestHelper.HTTPGet(address, headers, queries);
                } else {
                    results = httpRequestHelper.HTTPGet(address, headers);
                }
            } else {
                results = httpRequestHelper.HTTPGet(address);
            }
            callback.AsyncResponse(results, AsyncTaskErrorMessage, tag);
        } catch (Exception e) {
            Log.d("PSIDebug", "AsyncHttpRequestHelper", e);
            callback.AsyncResponse(null, AsyncTaskErrorMessage, tag);
        }
        return null;
    }

    /***********************************************************************************************
     *
     *      Listener interface implemented on class that calls AsyncHttpRequestHelper.
     *      Through this interface, we can callback to the class and return the results
     *          after background computation is finished.
     *
     **********************************************************************************************/
    public interface AsyncResponseListener{
        void AsyncResponse(JSONObject output, String error_message, String tag);
    }
}
