package com.example.cloud;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "awsexample";

    private ArrayList<MyTime> mArrayList = new ArrayList<>();
    private ArrayList<BarEntry> mEntryList = new ArrayList<>();
    private String mJsonString;
    final ArrayList<String> xAxisLabel = new ArrayList<>();
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xAxisLabel.add("Mon");
        xAxisLabel.add("Tue");
        xAxisLabel.add("Wed");
        xAxisLabel.add("Thu");
        xAxisLabel.add("Fri");
        xAxisLabel.add("Sat");
        xAxisLabel.add("Sun");

        Calendar calendar = Calendar.getInstance();
        BarChart real_time = findViewById(R.id.real_time);
//        BarChart objective_time = findViewById(R.id.objective_time);

        BarDataSet real_time_set = new BarDataSet(mEntryList, "real_time");
//        BarDataSet objective_time_set = new BarDataSet(getObjectiveTime(), "objective_time");

        BarData real_time_data = new BarData();
//        BarData objective_time_data = new BarData();

        real_time_data.addDataSet(real_time_set);
//        objective_time_data.addDataSet(objective_time_set);

        real_time_set.setColor(R.color.purple_200);
//        objective_time_set.setColor(R.color.purple_700);

        real_time.setData(real_time_data);
//        objective_time.setData(objective_time_data);

        Button button_search = (Button) findViewById(R.id.button_main_search);
        button_search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mArrayList.clear();
                for(int i = 0; i < 7; i ++) {
                    int date = calendar.getTime().getDate();

                    GetData task = new GetData();

                    task.execute("https://vx289k7h1k.execute-api.ap-northeast-2.amazonaws.com/default/awake", "2022-11-" + (30 - i));
//                    xAxisLabel.add("11-"+(date-i));
                }

            }
        });
    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
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
                jsonObject.put("Time", name);
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

            Response response = null;
            try {
                response = client.newCall(request).execute();
                String resStr = response.body().string();

                return resStr;
            } catch (IOException e) {
                e.printStackTrace();

                return e.toString();
            }
        }
    }

    private void showResult() {

        String TAG_NAME = "Time";
        String TAG_SLEEP = "sleep";
        String TAG_AWAKE = "awake";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            String str = jsonObject.getString("body");
            JSONObject jsonObjectBody = new JSONObject(str);

            String name = jsonObjectBody.getString(TAG_NAME);
            String sleep[] = jsonObjectBody.getString(TAG_SLEEP).split(":");
            String awake[] = jsonObjectBody.getString(TAG_AWAKE).split(":");

            int sleep_h = Integer.parseInt(sleep[0]);
            float sleep_m = Float.parseFloat(sleep[1]) / 60;
            int awake_h = Integer.parseInt(awake[0]);
            float awake_m = Float.parseFloat(awake[1]) / 60;

            mEntryList.add(new BarEntry(i++, (float) (24 - sleep_h + awake_h + (awake_m - sleep_m))));

            BarChart real_time = findViewById(R.id.real_time);
            real_time.getAxisLeft().setAxisMinimum(4);
            real_time.getAxisRight().setAxisMinimum(4);
            real_time.getAxisLeft().setAxisMaximum(12);
            real_time.getAxisRight().setAxisMaximum(12);
            XAxis xAxis = real_time.getXAxis();
            xAxis.setDrawLabels(true);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            BarDataSet real_time_set = new BarDataSet(mEntryList, "sleep_time");

            BarData real_time_data = new BarData();

            real_time_data.addDataSet(real_time_set);

            real_time_set.setColor(R.color.purple_200);

            real_time.setData(real_time_data);
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
}