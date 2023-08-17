package com.plcoding.androidstorage;

import static com.plcoding.androidstorage.Constant.API_KEY;
import static com.plcoding.androidstorage.Constant.API_URL;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.plcoding.androidstorage.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;
    private ArrayList<ModelWeather> arrayList;
    private EditText search;
    private ImageView sicon;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private CoordinatorLayout cl_layout;
    private TextView city;
    private TextView dc;
    private TextView temp;
    private TextView today;
    private TextView typet;
    private WeatherAdapter weatherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        city = findViewById(R.id.city);
        dc = findViewById(R.id.textView2);
        dc.setVisibility(View.GONE);

        cl_layout = findViewById(R.id.cl_layout);
        arrayList = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar2);
        search = findViewById(R.id.search);
        sicon = findViewById(R.id.sicon);
        temp = findViewById(R.id.temp);
        temp.setVisibility(View.GONE);
        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setVisibility(View.GONE);
        today = findViewById(R.id.textView);
        today.setVisibility(View.GONE);
        typet = findViewById(R.id.typet);


        RecyclerView recyclerView = findViewById(R.id.rv_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        weatherAdapter = new WeatherAdapter(this, arrayList);
        recyclerView.setAdapter(weatherAdapter);
        response(WeatherDatabase.getString(getApplicationContext()));


        binding.imageView2.setOnClickListener(v -> {
            String cityName = search.getText().toString();
            WeatherDatabase.saveString(getApplicationContext(), cityName);
            progressBar.setVisibility(View.VISIBLE);
            arrayList.clear();
            response(cityName);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        });
    }

    private void response(String cityName) {
        String url = API_URL + API_KEY + cityName + "&days=1&aqi=yes&alerts=yes";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            progressBar.setVisibility(View.GONE);
            int temper;
            try {
                temp.setVisibility(View.VISIBLE);
                dc.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                today.setVisibility(View.VISIBLE);

                temper = response.getJSONObject("current").getInt("temp_c");
                temp.setText(Integer.toString(temper));
                String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                typet.setText(condition);
                String icon = response.getJSONObject("current").getJSONObject("condition").getString("icon");

                Glide.with(MainActivity.this)
                        .load("http:" + icon)
                        .into(sicon);

                city.setText(response.getJSONObject("location").getString("name"));
                int a = response.getJSONObject("current").getInt("is_day");

                if (a == 1) {
                    cl_layout.setBackgroundResource(R.drawable.bg_day);
                } else {
                    cl_layout.setBackgroundResource(R.drawable.bg_night);
                }

                JSONObject forecastObj = response.getJSONObject("forecast");
                JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                JSONArray hourArray = forecastO.getJSONArray("hour");
                for (int i = 0; i < hourArray.length(); i++) {
                    JSONObject hourObj = hourArray.getJSONObject(i);
                    String time = hourObj.getString("time");
                    int temperr = hourObj.getInt("temp_c");
                    String img = hourObj.getJSONObject("condition").getString("icon");
                    String wSpeed = hourObj.getString("wind_kph");
                    String humidity = hourObj.getString("humidity");

                    arrayList.add(new ModelWeather(temperr, humidity, wSpeed, img, time));
                    weatherAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Enter Your City And Click Search ", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "Network Issue or City Not Found", Toast.LENGTH_SHORT).show();
        });
        requestQueue.add(jsonObjectRequest);
    }
}