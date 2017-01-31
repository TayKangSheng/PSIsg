package co.kopigao.psisg.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.kopigao.psisg.R;
import co.kopigao.psisg.helpers.DataHelper;
import co.kopigao.psisg.helpers.JSONHelper;

public class HomeFragment extends Fragment
        implements GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback,
        TabLayout.OnTabSelectedListener, GoogleMap.OnMapClickListener {

    private DataHelper mDataHelper;
    private JSONHelper mJSONHelper;
    private Marker mMarker;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private GoogleMap mGoogleMap;
    private ArrayList<Marker> mMarkers;

    public HomeFragment() { }

    public static HomeFragment newInstance() {
        Log.d("PSIDebug", "HomeFragment newInstance");

        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("PSIDebug", "HomeFragment onCreate");

        mDataHelper = new DataHelper(getContext());
        mJSONHelper = new JSONHelper(getContext(), mDataHelper.getMapData());

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("PSIDebug", "HomeFragment onCreateView");

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Without this, onMapReady will not be called when its done.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create View Pager
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        createViewPager(mViewPager);

        // Set up TabLayout with View Pager
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setOnTabSelectedListener(this);
        createTabIcons(mTabLayout);

        try {
            String date_string = mJSONHelper.getReadingsData().getJSONObject(0).getString("update_timestamp");
            SimpleDateFormat given_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            Date timestamp = given_format.parse(date_string);
            DateFormat print_format = new SimpleDateFormat("MMM dd',' yyyy HH:mma");
            TextView date = (TextView) view.findViewById(R.id.map_date);
            date.setText(print_format.format(timestamp)+" ");
        } catch (Exception e){
            Log.d("PSIDebug", "HomeFragment onCreateView", e);
        }

        return view;
    }

    private void createViewPager(ViewPager viewPager) {
        Log.d("PSISG", "HomeFragment createViewPager");

        JSONArray locations = mJSONHelper.getLocationMetaData();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        for (int i=0 ; i<locations.length() ; i++){
            try {
                String location_name = locations.getJSONObject(i).getString("name");
                adapter.addFrag(HomeTabFragment.newInstance(location_name), location_name);
            } catch (Exception e){
                Log.d("PSISG", "HomeFragment createViewPager", e);
            }
        }
        viewPager.setAdapter(adapter);
    }

    private void createTabIcons(TabLayout tabLayout) {
        Log.d("PSIDebug", "HomeFragment createTabIcons");

        JSONArray locations = mJSONHelper.getLocationMetaData();
        JSONArray readings = mJSONHelper.getReadingsData();
        for (int i=0 ; i<locations.length() ; i++){
            try {
                LinearLayout tabOne = (LinearLayout) LayoutInflater.from(this.getContext()).inflate(R.layout.custom_tab, null);
                JSONObject location_metadata = locations.getJSONObject(i);
                JSONObject reading_metadata = readings.getJSONObject(0).getJSONObject("readings");

                String location_name = location_metadata.getString("name");
                double reading_psi24 = reading_metadata.getJSONObject("psi_twenty_four_hourly").getDouble(location_name);

                ((TextView) tabOne.findViewById(R.id.custom_tab_title)).setText(location_name.substring(0, 1).toUpperCase() + location_name.substring(1));
                ((TextView) tabOne.findViewById(R.id.custom_tab_value)).setText(String.valueOf(reading_psi24));
                tabLayout.getTabAt(i).setCustomView(tabOne);
                tabLayout.getTabAt(i).setTag(location_name);
            } catch (Exception e){
                Log.d("PSIDebug", "HomeFragment createTabIcons", e);
            }
        }
        tabLayout.getTabAt(0).getCustomView().setSelected(true);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("PSIDebug", "HomeFragment onMapReady");

        mGoogleMap = googleMap;
        UiSettings uisettings = mGoogleMap.getUiSettings();
        uisettings.setMapToolbarEnabled(false);
        uisettings.setScrollGesturesEnabled(false);
        uisettings.setZoomGesturesEnabled(false);
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMapClickListener(this);

        mMarkers = new ArrayList<Marker>();

        JSONArray locations = mJSONHelper.getLocationMetaData();
        for (int i=0 ; i<locations.length() ; i++){
            try {
                JSONObject location_metadata = locations.getJSONObject(i);
                String location_name = location_metadata.getString("name");

                JSONObject metadata = location_metadata.getJSONObject("label_location");
                double latitude = metadata.getDouble("latitude");
                double longitude = metadata.getDouble("longitude");

                LatLng lat_lng = new LatLng(latitude, longitude);
                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(lat_lng)
                        .title(location_name.substring(0, 1).toUpperCase() + location_name.substring(1)));
                marker.setTag(location_name);
                mMarkers.add(marker);

                if (location_name.equals("central")){
                    LatLng camera_position = new LatLng(lat_lng.latitude + 0.015, lat_lng.longitude);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(camera_position));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camera_position, 10));
                }

            } catch (Exception e){
                Log.d("PSIDebug", "HomeFragment onMapReady", e);
            }
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("PSIDebug", "HomeFragment onMarkerClick");

        if (mMarker != null) {
            mMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
        }
        mMarker = marker;
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        mTabLayout.getTabAt(mMarkers.indexOf(marker)).select();
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d("PSIDebug", "HomeFragment onMapClick");

        if (!mMarker.getTag().equals("national")){
            onTabSelected(mTabLayout.getTabAt(0));
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.d("PSIDebug", "HomeFragment onTabSelected");

        tab.select();
        mViewPager.setCurrentItem(tab.getPosition());
        if (mMarker != mMarkers.get(tab.getPosition())){
            onMarkerClick(mMarkers.get(tab.getPosition()));
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        Log.d("PSIDebug", "HomeFragment onTabUnselected");

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        Log.d("PSIDebug", "HomeFragment onTabReselected");

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
