package co.kopigao.psisg;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import co.kopigao.psisg.fragments.ChartFragment;
import co.kopigao.psisg.fragments.HomeFragment;
import co.kopigao.psisg.helpers.AsyncHttpRequestHelper;
import co.kopigao.psisg.helpers.DataHelper;
import co.kopigao.psisg.helpers.RetrieveChartDataHelper;
import co.kopigao.psisg.helpers.RetrieveMapDataHelper;

public class MainActivity extends AppCompatActivity
implements RetrieveMapDataHelper.RetrieveMapDataHelperListener,
        RetrieveChartDataHelper.RetrieveChartDataHelperListener{

    private int mCurrentFragment;
    private Menu mOptionsMenu;
    private boolean mRefreshingFragment = false;
    private FragmentManager mFragmentManager;
    private DataHelper mDataHelper;
    private RetrieveMapDataHelper mRetrieveMapDataHelper;
    private RetrieveChartDataHelper mRetrieveChartDataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("PSIDebug", "MainActivity onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDataHelper = new DataHelper(this);

        mFragmentManager = getSupportFragmentManager();

        mCurrentFragment=R.id.action_home;
    }

    @Override
    protected void onStart(){
        Log.d("PSIDebug", "MainActivity onStart");

        super.onStart();
        mDataHelper.setActive(true);
        SetFragment(mCurrentFragment);
    }

    @Override
    protected void onStop(){
        Log.d("PSIDebug", "MainActivity onStop");

        super.onStop();
        mDataHelper.setActive(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("PSIDebug", "MainActivity onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.main, menu);
        mOptionsMenu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("PSIDebug", "MainActivity onOptionsItemSelected");

        int id = item.getItemId();
        if (id == R.id.action_home && id != mCurrentFragment) {
            Log.d("PSIDebug", "MainActivity onOptionsItemSelected R.id.action_home");

            SetFragment(id);
        } else if (id == R.id.action_chart && id != mCurrentFragment) {
            Log.d("PSIDebug", "MainActivity onOptionsItemSelected R.id.action_chart");

            SetFragment(id);
        } else if (id == R.id.action_refresh && mRefreshingFragment == false) {
            Log.d("PSIDebug", "MainActivity onOptionsItemSelected R.id.action_refresh refresh");

            item.setIcon(R.drawable.ic_cancel);
            mRefreshingFragment = true;
            RetrieveMapData();
            RetrieveChartData();
        } else if (id == R.id.action_refresh && mRefreshingFragment == true){
            Log.d("PSIDebug", "MainActivity onOptionsItemSelected R.id.action_refresh cancel");
            item.setIcon(R.drawable.ic_refresh);
            mRetrieveChartDataHelper.cancel(true);
            mRetrieveMapDataHelper.cancel(true);
            mRefreshingFragment = false;
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /************************************************************
     *
     *  Setting of Fragment should only be done through
     *  this method.
     *
     ***********************************************************/
    private void SetFragment(int toSet){
        Log.d("PSIDebug", "MainActivity SetFragment");

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        switch (toSet){
            case R.id.action_home:
                HomeFragment homeFragment = HomeFragment.newInstance();
                fragmentTransaction.replace(R.id.fragment_container, homeFragment);
                fragmentTransaction.commit();
                mCurrentFragment = toSet;
                break;
            case R.id.action_chart:
                ChartFragment chartFragment = ChartFragment.newInstance();
                fragmentTransaction.replace(R.id.fragment_container, chartFragment);
                fragmentTransaction.commit();
                mCurrentFragment = toSet;
                break;
            default:
                throw new NullPointerException("Fragment ID toSet does not exist");
        }
    }

    private void RetrieveChartData(){
        Log.d("PSIDebug", "MainActivity RetrieveChartData");

        mRetrieveChartDataHelper = new RetrieveChartDataHelper(this);

        String URL = getResources().getString(R.string.API_url);
        String[] headers = { "api-key", getResources().getString(R.string.API_key) };

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"), Locale.US);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String[] queries = {
                "date", df.format(calendar.getTime())
        };

        Bundle ChartDataBundle = new Bundle();
        ChartDataBundle.putString("address",URL);
        ChartDataBundle.putStringArray("headers", headers);
        ChartDataBundle.putStringArray("queries", queries);

        mRetrieveChartDataHelper.execute(ChartDataBundle);
    }

    private void RetrieveMapData(){
        Log.d("PSIDebug", "MainActivity RetrieveMapData");

        mRetrieveMapDataHelper = new RetrieveMapDataHelper(this);

        String URL = getResources().getString(R.string.API_url);
        String[] headers = { "api-key", getResources().getString(R.string.API_key) };

        Bundle MapDataBundle = new Bundle();
        MapDataBundle.putString("address",URL);
        MapDataBundle.putStringArray("headers", headers);

        mRetrieveMapDataHelper.execute(MapDataBundle);
    }

    /************************************************************
     *
     *  Async Response of Chart Data Helper.
     *  Retrieve and save the data.
     *  Call Refresh if the
     *      1. Current fragment is chart fragment, AND
     *      2. Application is currently active.
     *
     ***********************************************************/
    @Override
    public void RetrieveChartDataHelperResponse(JSONObject output, String error_message) {
        Log.d("PSIDebug", "MainActivity RetrieveChartDataHelperResponse");

        if (output != null) {
            mDataHelper.setPSIData(output.toString());

            if (mCurrentFragment == R.id.action_chart && mDataHelper.getActive()==true) {
                SetFragment(R.id.action_chart);
            }
        } else {
            if (mCurrentFragment == R.id.action_chart) {
                Toast.makeText(this, error_message, Toast.LENGTH_SHORT).show();
            }
        }
        if (mOptionsMenu!=null) mOptionsMenu.findItem(R.id.action_refresh).setIcon(R.drawable.ic_refresh);
        mRefreshingFragment = false;
    }

    /************************************************************
     *
     *  Async Response of Map Data Helper.
     *  Retrieve and save the data.
     *  Call Refresh if the
     *      1. Current fragment is map fragment, AND
     *      2. Application is currently active.
     *
     ***********************************************************/
    @Override
    public void RetrieveMapDataHelperResponse(JSONObject output, String error_message) {
        Log.d("PSIDebug", "MainActivity RetrieveMapDataHelperResponse");

        if (output != null) {
            mDataHelper.setMapData(output.toString());

            if (mCurrentFragment == R.id.action_home && mDataHelper.getActive()==true) {
                SetFragment(R.id.action_home);
            }
        } else {
            if (mCurrentFragment == R.id.action_home) {
                Toast.makeText(this, error_message, Toast.LENGTH_SHORT).show();
            }
        }

        if (mOptionsMenu!=null) mOptionsMenu.findItem(R.id.action_refresh).setIcon(R.drawable.ic_refresh);
        mRefreshingFragment = false;
    }
}
