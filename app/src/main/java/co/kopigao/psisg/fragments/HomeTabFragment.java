package co.kopigao.psisg.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Iterator;

import co.kopigao.psisg.R;
import co.kopigao.psisg.helpers.DataHelper;
import co.kopigao.psisg.helpers.JSONHelper;

public class HomeTabFragment extends Fragment {

    private DataHelper mDataHelper;
    private JSONHelper mJSONHelper;
    private String mLocation;

    public HomeTabFragment() {
        // Required empty public constructor
    }

    public static HomeTabFragment newInstance(String location) {
        Log.d("PSIDebug", "HomeFragment newInstance");

        HomeTabFragment fragment = new HomeTabFragment();
        Bundle bundle = new Bundle();
        bundle.putString("location", location);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("PSIDebug", "HomeFragment onCreate");

        if ( getArguments() != null){
            mDataHelper = new DataHelper(getContext());
            mJSONHelper = new JSONHelper(getContext(), mDataHelper.getMapData());
            mLocation = getArguments().getString("location");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("PSIDebug", "HomeFragment onCreateView");

        View view = inflater.inflate(R.layout.fragment_home_tab, container, false);
        LinearLayout mLinearLayout = (LinearLayout) view.findViewById(R.id.readings);

        try {
            JSONObject readings = mJSONHelper.getReadingsData().getJSONObject(0).getJSONObject("readings");
            Iterator<String> keys = readings.keys();
            while (keys.hasNext()){
                FrameLayout reading_entry = (FrameLayout) LayoutInflater.from(this.getContext()).inflate(R.layout.reading_entry, null);

                String key = keys.next();
                double value = readings.getJSONObject(key).getDouble(mLocation);

                ((TextView) reading_entry.findViewById(R.id.reading_entry_name)).setText(key);
                ((TextView) reading_entry.findViewById(R.id.reading_entry_value)).setText(String.valueOf(value));

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(5, 5, 5, 5);
                reading_entry.setLayoutParams(params);

                mLinearLayout.addView(reading_entry);
            }
        } catch (Exception e){
            Log.d("PSIDebug", "HomeFragment onCreateView", e);
        }

        return view;
    }

    public void addReadings(LinearLayout mLinearLayout){
        Log.d("PSIDebug", "HomeFragment addReadings");

        try {
            JSONObject readings = mJSONHelper.getReadingsData().getJSONObject(0).getJSONObject("readings");
            Iterator<String> keys = readings.keys();
            while (keys.hasNext()){
                FrameLayout reading_entry = (FrameLayout) LayoutInflater.from(this.getContext()).inflate(R.layout.reading_entry, null);

                String key = keys.next();
                double value = readings.getJSONObject(key).getDouble(mLocation);

                ((TextView) reading_entry.findViewById(R.id.reading_entry_name)).setText(key);
                ((TextView) reading_entry.findViewById(R.id.reading_entry_value)).setText(String.valueOf(value));

                mLinearLayout.addView(reading_entry);
            }
        } catch (Exception e){
            Log.d("PSIDebug", "HomeFragment addReadings", e);
        }
    }
}