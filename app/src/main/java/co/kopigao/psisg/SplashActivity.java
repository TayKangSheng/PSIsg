package co.kopigao.psisg;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import co.kopigao.psisg.helpers.RetrieveMapDataHelper;
import io.fabric.sdk.android.Fabric;
import org.json.JSONObject;

import co.kopigao.psisg.fragments.AlertDialogFragment;
import co.kopigao.psisg.helpers.DataHelper;

public class SplashActivity extends AppCompatActivity
        implements RetrieveMapDataHelper.RetrieveMapDataHelperListener,
            AlertDialogFragment.AlertDialogListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("PSIDebug", "SplashActivity onCreate");

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_splash);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        RetrieveMapDataHelper mRetrieveMapDataHelper = new RetrieveMapDataHelper(this);

        String URL = getResources().getString(R.string.API_url);
        String[] headers = { "api-key", getResources().getString(R.string.API_key) };

        Bundle MapDataBundle = new Bundle();
        MapDataBundle.putString("address",URL);
        MapDataBundle.putStringArray("headers", headers);

        mRetrieveMapDataHelper.execute(MapDataBundle);
    }

    @Override
    public void RetrieveMapDataHelperResponse(JSONObject output, String error_message) {
        Log.d("PSIDebug", "SplashActivity RetrieveMapDataHelperResponse");

        if (output == null){
            AlertDialogFragment alertFragment = AlertDialogFragment.newInstance(error_message,"Continue","Quit");
            alertFragment.show(getSupportFragmentManager(), "AlertFragment");
        } else {
            DataHelper dataHelper = new DataHelper(this);
            dataHelper.setMapData(output.toString());

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();

        finish();
    }

    /*************************************************************
     *
     *       User selects "Continue".
     *       Continues to the next activity.
     *
     **************************************************************/
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.d("PSIDebug", "SplashActivity onDialogPositiveClick");

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialog.dismiss();
        startActivity(intent);
        finish();
    }

    /*************************************************************
    *
    *       User selects "Quit".
    *       Exits the application.
    *
    **************************************************************/
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.d("PSIDebug", "SplashActivity onDialogNegativeClick");

        dialog.dismiss();
        finish();
    }
}
