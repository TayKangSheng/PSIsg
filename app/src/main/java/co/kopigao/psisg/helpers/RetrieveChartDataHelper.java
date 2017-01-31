package co.kopigao.psisg.helpers;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

/***************************************************************************************************
 *
 *      This is a Async HTTPRequest class
 *      to retrieve Chart Data
 *      AsyncTask<Params, Progress, Result>
 *
 **************************************************************************************************/
public class RetrieveChartDataHelper extends AsyncTask<Bundle, Integer, JSONObject> {

    private RetrieveChartDataHelperListener mListener;

    public RetrieveChartDataHelper(RetrieveChartDataHelperListener listener){
        mListener = listener;
    }

    // Do not manipulate UI Thread in background computation.
    // Use postExecute to call methods on UI Thread.
    @Override
    protected JSONObject doInBackground(Bundle... params) {
        Log.d("PSIDebug", "RetrieveChartDataHelper doInBackground");

        String address = params[0].getString("address");
        String[] headers = params[0].getStringArray("headers");
        String[] queries = params[0].getStringArray("queries");

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

            return results;
        } catch (Exception e) {
            Log.d("PSIDebug", "AsyncHttpRequestHelper", e);
        }
        return null;
    }

    public void onPostExecute(JSONObject result){
        String AsyncTaskErrorMessage = "Fail to retrieve Chart updates";

        mListener.RetrieveChartDataHelperResponse(result, AsyncTaskErrorMessage);
    }

    public interface RetrieveChartDataHelperListener{
        void RetrieveChartDataHelperResponse(JSONObject output, String error_message);
    }
}
