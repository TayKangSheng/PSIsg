package co.kopigao.psisg.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.kopigao.psisg.R;
import co.kopigao.psisg.helpers.DataHelper;
import co.kopigao.psisg.helpers.JSONHelper;


public class ChartFragment extends Fragment {

    private DataHelper mDataHelper;
    private JSONHelper mJSONHelper;

    public ChartFragment() {

    }

    public static ChartFragment newInstance() {
        Log.d("PSIDebug", "ChartFragment newInstance");

        ChartFragment fragment = new ChartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("PSIDebug", "ChartFragment onCreate");

        super.onCreate(savedInstanceState);

        mDataHelper = new DataHelper(getContext());
        mJSONHelper = new JSONHelper(getContext(), mDataHelper.getPSIData());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("PSIDebug", "ChartFragment onCreateView");

        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        addDataToChart(view);

        try {
            int latestReadingsIndex = mJSONHelper.getReadingsData().length() - 1;
            String date_string = mJSONHelper.getReadingsData().getJSONObject(latestReadingsIndex).getString("update_timestamp");
            SimpleDateFormat given_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            Date timestamp = given_format.parse(date_string);
            DateFormat print_format = new SimpleDateFormat("MMM dd',' yyyy HH:mma");
            TextView date = (TextView) view.findViewById(R.id.chart_date);
            date.setText(print_format.format(timestamp));
        } catch (Exception e){
            Log.d("PSIDebug", "ChartFragment onCreateView", e);
        }

        return view;
    }

    private void addDataToChart(View mView){
        Log.d("PSIDebug", "ChartFragment addDataToChart");

        LineChart chart = (LineChart) mView.findViewById(R.id.chart);

        String key = "psi_twenty_four_hourly";
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        int[] colors = {Color.RED, Color.BLUE, Color.DKGRAY, Color.GREEN, Color.CYAN, Color.MAGENTA};

        JSONArray locations = mJSONHelper.getLocationMetaData();
        JSONArray readings = mJSONHelper.getReadingsData();

        for (int i=0 ; i<locations.length() ; i++){
            List<Entry> entries = new ArrayList<Entry>();
            try {
                String location = locations.getJSONObject(i).getString("name");

                for (int j = 0; j < readings.length(); j++) {
                    String date_string = readings.getJSONObject(j).getString("timestamp");
                    SimpleDateFormat given_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                    Date date = given_format.parse(date_string);
                    DateFormat print_format = new SimpleDateFormat("H");
                    int xAxis = Integer.valueOf(print_format.format(date));
                    float yAxis = (float) readings.getJSONObject(j).getJSONObject("readings").getJSONObject("psi_twenty_four_hourly").getDouble(location);
                    entries.add(new Entry(xAxis, yAxis));
                }

                LineDataSet dataSet = new LineDataSet(entries, location+" PSI 24");
                dataSet.setColor(colors[i]);
                dataSet.setCircleColor(colors[i]);
                dataSet.setCircleColorHole(ContextCompat.getColor(getContext(), R.color.psisg_White));

                if (location.equals("national")){
                    float lineWidth = (float) (dataSet.getLineWidth()*1.1);
                    dataSet.setLineWidth(lineWidth);
                }

                dataSets.add(dataSet);
            }catch (Exception e) {
                Log.d("PSIDebug", "ChartFragment addDataToChart", e);
            }
        }

        LineData lineData = new LineData(dataSets);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new MyValueFormatter());

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawLabels(false);

        chart.getLegend().setWordWrapEnabled(true);
        Description chartDesc = new Description();
        chartDesc.setText("24hr PSI");
        chart.setDescription(chartDesc);

        chart.invalidate(); // refresh

    }

    class MyValueFormatter implements IAxisValueFormatter {

        private SimpleDateFormat mFormat;

        public MyValueFormatter() {
            mFormat = new SimpleDateFormat("HH a");
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            String hour = String.valueOf((int) value);
            try {
                SimpleDateFormat given_format = new SimpleDateFormat("H");
                Date date = given_format.parse(hour);
                return mFormat.format(date);
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

}
