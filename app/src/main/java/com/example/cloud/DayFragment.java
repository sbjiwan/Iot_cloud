package com.example.cloud;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DayFragment extends Fragment {
    private static final String TAG = "awsexample";

    private final ArrayList<BarEntry> mRealEntryList = new ArrayList<>();
    private final ArrayList<BarEntry> mObjectiveEntryList = new ArrayList<>();
    private final ArrayList<IBarDataSet> mDateSets = new ArrayList<>();
    private final LegendEntry[] entries = new LegendEntry[2];
    private BarChart time;
    private String mJsonString;
    final ArrayList<String> xAxisLabel = new ArrayList<>();
    final String[] str = {"", "일", "월", "화", "수","목","금","토"};
    int i = 0;
    int position;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_day, container, false);
        Bundle bundle = getArguments();

        assert bundle != null;
        position = bundle.getInt("position", 0);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, - position);
        calendar.add(Calendar.DATE, 1);
        time = rootView.findViewById(R.id.time_day);

        LegendEntry entry1 = new LegendEntry();
        entry1.label = "실제 수면 시간";
        entry1.formColor = Color.rgb(135,225,225);
        LegendEntry entry2 = new LegendEntry();
        entry2.label = "목표 수면 시간";
        entry2.formColor = Color.rgb(5,90,90);
        entries[1] = entry1;
        entries[0] = entry2;

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);

        GetData task = new GetData();

        if(date < 10)
            task.execute("https://vnywog6a3c.execute-api.ap-northeast-2.amazonaws.com/default/dynamo", year + "-" + month+ "-" + "0" + date);
        else
            task.execute("https://vnywog6a3c.execute-api.ap-northeast-2.amazonaws.com/default/dynamo", year + "-" + month+ "-" + date);
        xAxisLabel.add(month + "." + date + " " + str[calendar.get(Calendar.DAY_OF_WEEK)]);

        return rootView;
    }


    @SuppressLint("StaticFieldLeak")
    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getContext(),
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result == null){
            }
            else {
                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String name = params[1];
            String serverURL = params[0];

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("Date", name);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(serverURL)
                    .post(body)
                    .build();

            Response response;
            try {
                response = client.newCall(request).execute();

                return Objects.requireNonNull(response.body()).string();
            } catch (IOException e) {
                e.printStackTrace();

                return e.toString();
            }
        }
    }

    private void showResult() {
        String TAG_AWAKE = "awake";
        String TAG_SLEEP = "sleep";
        String TAG_TIMESTAMP = "timestamp";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            String str = jsonObject.getString("body");
            JSONObject jsonObjectBody = new JSONObject(str);

            String[] objective = jsonObjectBody.getString(TAG_AWAKE).split(":");
            String[] sleep = jsonObjectBody.getString(TAG_SLEEP).split(":");
            String[] awake = jsonObjectBody.getString(TAG_TIMESTAMP).split(":");

            int sleep_h = Integer.parseInt(sleep[0]);
            if(sleep_h < 10)
                sleep_h += 24;
            float sleep_m = Float.parseFloat(sleep[1]) / 60;
            int objective_h = Integer.parseInt(objective[0]);
            float objective_m = Float.parseFloat(objective[1]) / 60;
            int awake_h = Integer.parseInt(awake[0]);
            float awake_m = Float.parseFloat(awake[1]) / 60;

            mRealEntryList.add(new BarEntry(i++, (float) (24 - sleep_h + awake_h + (awake_m - sleep_m))));
            mObjectiveEntryList.add(new BarEntry(i++, (float) (24 - sleep_h + objective_h + (objective_m - sleep_m))));

            setChart();
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
    private void setChart() {
        time.getAxisLeft().setAxisMinimum(4);
        time.getAxisRight().setAxisMinimum(4);
        time.getAxisLeft().setAxisMaximum(12);
        time.getAxisRight().setAxisMaximum(12);

        BarDataSet real_time_set = new BarDataSet(mRealEntryList, "");
        real_time_set.setColor(Color.rgb(135,225,225));
        real_time_set.setDrawValues(false);
        BarDataSet objective_time_set = new BarDataSet(mObjectiveEntryList, "");
        objective_time_set.setColor(Color.rgb(5,90,90));
        objective_time_set.setDrawValues(false);

        mDateSets.add(objective_time_set);
        mDateSets.add(real_time_set);

        BarData time_data = new BarData(mDateSets);

        time.setData(time_data);

        time.setDoubleTapToZoomEnabled(false);
        time.getDescription().setEnabled(false);
        time.setDrawGridBackground(false);

        XAxis xAxis = time.getXAxis();
        xAxis.setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.rgb(135,225,225));

        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));

        Legend l = time.getLegend();
        l.setCustom(entries);
        l.setWordWrapEnabled(true);
        l.setTextSize(14);
        l.setTextColor(Color.rgb(135,225,225));
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.CIRCLE);

        YAxis leftAxis = time.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.setTypeface(Typeface.DEFAULT);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(Color.rgb(135,225,225));
        leftAxis.setDrawGridLines(false);
        time.getAxisRight().setEnabled(false);

        setBarWidth(time_data);

        float barSpace = 0.02f;
        float groupSpace = 0.3f;
        float defaultBarWidth = (1 - groupSpace) / 2 - barSpace;
        time_data.setBarWidth(defaultBarWidth);

        time.invalidate();
    }

    private void setBarWidth(BarData barData) {
        if (mDateSets.size() > 1) {
            float barSpace = 0.02f;
            float groupSpace = 0.3f;
            float defaultBarWidth = (1 - groupSpace) / 2 - barSpace;

            barData.setBarWidth(defaultBarWidth);

            time.getXAxis().setAxisMinimum(0);
            time.getXAxis().setAxisMaximum(0 + time.getBarData().getGroupWidth(groupSpace, barSpace));
            time.getXAxis().setCenterAxisLabels(true);

            time.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
            time.invalidate();
        }
    }
}
