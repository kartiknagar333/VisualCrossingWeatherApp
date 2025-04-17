package com.example.assignment_4.Activity;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.assignment_4.Adapter.DayTimeAdapter;
import com.example.assignment_4.Adapter.Dayforeitem;
import com.example.assignment_4.Model.DayItem;
import com.example.assignment_4.Model.Weather;
import com.example.assignment_4.R;
import com.example.assignment_4.databinding.ActivityDailyForecastBinding;
import com.example.assignment_4.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DailyForecastActivity extends AppCompatActivity {
    private List<DayItem> list;
    private boolean isCelsius;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDailyForecastBinding binding = ActivityDailyForecastBinding.inflate(getLayoutInflater());
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.white));
        window.setNavigationBarColor(getColor(R.color.white));
        getWindow().setStatusBarColor(getColor(R.color.black));
        getWindow().setNavigationBarColor(getColor(R.color.black));
        getWindow().getDecorView().setSystemUiVisibility(0);
        setContentView(binding.getRoot());

        final Weather myweather = (Weather) getIntent().getSerializableExtra("myweather");

        isCelsius = getIntent().getBooleanExtra("isCelsius", false);

        assert myweather != null;
        if(Character.isDigit(myweather.getResolvedAddress().split(",")[0].charAt(1))){
            binding.tvtoptitle.setText(getCityNameFromLatLon(myweather.getLatitude(), myweather.getLongitude())
                    +" "+myweather.getDays().size()+ "-Day Forecast");
        }else {
            binding.tvtoptitle.setText(myweather.getResolvedAddress().split(",")[0]
                    +" "+myweather.getDays().size()+ "-Day Forecast");
        }

        Dayforeitem adapter = new Dayforeitem(getlist(myweather),isCelsius);
        binding.rvdayforitem.setAdapter(adapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rvdayforitem.setLayoutManager(layoutManager);
    }


    private List<DayItem> getlist(Weather w){
        list = new ArrayList<>();
        for (int i = 0; i < w.getDays().size(); i++) {
            list.add(new DayItem(w.getDays().get(i).formatDate(),
                    w.getDays().get(i).getTempmax(isCelsius),
                    w.getDays().get(i).getTempmin(isCelsius),
                    w.getDays().get(i).getDescription(),
                    String.valueOf(w.getDays().get(i).getPrecipprob()),
                    String.valueOf(w.getDays().get(i).getUvindex()),
                    w.getDays().get(i).getHours().get(8).getTemp(isCelsius),
                    w.getDays().get(i).getHours().get(13).getTemp(isCelsius),
                    w.getDays().get(i).getHours().get(17).getTemp(isCelsius),
                    w.getDays().get(i).getHours().get(23).getTemp(isCelsius),
                    w.getDays().get(i).getHours().get(0).getIcon(),
                    w.getDays().get(i).getavaragetemp()));
        }
        return list;
    }

    private String getCityNameFromLatLon(Double latitude, Double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String cityName = "Unknown City";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude,longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                cityName = address.getLocality();
            }
        } catch (IOException ignored) {

        }
        return cityName;
    }
}