package com.example.assignment_4.Activity;

import static com.example.assignment_4.API.WeatherAPI.buildUrlByCityState;
import static com.example.assignment_4.Helper.NetworkUtil.isInternetAvailable;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.assignment_4.API.WeatherAPI;
import com.example.assignment_4.Adapter.DayTimeAdapter;
import com.example.assignment_4.Helper.ChartMaker;
import com.example.assignment_4.Helper.ColorMaker;
import com.example.assignment_4.Helper.LocationHelper;
import com.example.assignment_4.Model.HourItem;
import com.example.assignment_4.Model.Weather;
import com.example.assignment_4.R;
import com.example.assignment_4.databinding.ActivityMainBinding;
import com.example.assignment_4.databinding.DialogInputLocationBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;


public class MainActivity extends AppCompatActivity implements LocationHelper.LocationCallbackInterface {

    protected ActivityMainBinding binding;
    private LocationHelper locationHelper;
    private SharedPreferences spf;
    private boolean isCelsius = false;
    private SharedPreferences.Editor editor;
    private DayTimeAdapter adapter;
    private double hightemp = 0,lowtemp = 0;
    private boolean isLocationweather = true;
    private ArrayList<HourItem> hourItemList = new ArrayList<>();
    private Weather myweather = null;
    private ChartMaker chartMaker;
    private Window window;

    @SuppressLint({"SetTextI18n", "ObsoleteSdkInt"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        window = getWindow();
        window.setStatusBarColor(getColor(R.color.white));
        window.setNavigationBarColor(getColor(R.color.white));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.black));
            getWindow().setNavigationBarColor(getColor(R.color.black));
            getWindow().getDecorView().setSystemUiVisibility(0);
        }
        setContentView(binding.getRoot());
        spf = getSharedPreferences("weather_data", MODE_PRIVATE);
        editor = spf.edit();

        locationHelper = new LocationHelper(this, this);
        chartMaker = new ChartMaker(this, binding);

        getstarted();

        binding.iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myweather != null){
                    binding.progressHorizontal.setVisibility(View.VISIBLE);
                    if(isCelsius){
                        isCelsius = false;
                        binding.iv4.setImageResource(R.drawable.units_f);
                        binding.tvtemp.setText(myweather.getCurrentConditions().getTemp(false));
                        binding.tvfeellike.setText("Feels Like: " + myweather.getCurrentConditions().getFeelslike(false));
                        setHouritemList(false);
                        TreeMap<String, Double> tm = new TreeMap<>();

                        for(int i = 0; i < myweather.getDays().get(0).getHours().size(); i++){
                            tm.put(myweather.getDays().get(0).getHours().get(i).gettimeforchar(),
                                    myweather.getDays().get(0).getHours().get(i).getdoubletemp(isCelsius));
                        }

                        chartMaker.makeChart(tm);
                    }else{
                        isCelsius = true;
                        binding.iv4.setImageResource(R.drawable.units_c);
                        binding.tvtemp.setText(myweather.getCurrentConditions().getTemp(true));
                        binding.tvfeellike.setText("Feels Like: " + myweather.getCurrentConditions().getFeelslike(true));
                        setHouritemList(false);
                        TreeMap<String, Double> tm = new TreeMap<>();

                        for(int i = 0; i < myweather.getDays().get(0).getHours().size(); i++){
                            tm.put(myweather.getDays().get(0).getHours().get(i).gettimeforchar(),
                                    myweather.getDays().get(0).getHours().get(i).getdoubletemp(isCelsius));
                        }

                        chartMaker.makeChart(tm);
                    }

                }else {
                    getDialog("Weather Data Error", "There was an error retrieving the weather data.\nPlease try again later.",false);
                }
            }
        });

        binding.iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myweather != null) {
                    openLocationInMap();
                }else{
                    getDialog("Weather Data Error", "There was an error retrieving the weather data.\nPlease try again later.",false);
                }
            }
        });

        binding.iv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DailyForecastActivity.class);
                intent.putExtra("myweather", myweather);
                intent.putExtra("isCelsius", isCelsius);
                startActivity(intent);
            }
        });

        binding.iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLocationweather = true;
                    getstarted();
            }
        });

        binding.iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myweather != null) {
                    doShare();
                }else {
                    getDialog("Weather Data Error", "There was an error retrieving the weather data.\nPlease try again later.",false);
                }
            }
        });

        binding.iv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInternetAvailable(MainActivity.this)){
                    openLocationDialog();
                }else {
                    getDialog("No Internet Connection", "This app requires an internet connection to function properly.\nPlease check your connection and try again.",false);
                }
            }
        });

        binding.main.setOnRefreshListener(() -> {
            if(isLocationweather) {
                getstarted();
            }else {
                binding.progressHorizontal.setVisibility(View.VISIBLE);
                getWeather(isLocationweather,spf.getString("city", ""), spf.getString("state", ""));
            }
        });

    }

    private void getstarted() {
        if(isInternetAvailable(this)){
            locationHelper.requestLocationUpdates();
        }else{
            binding.main.setRefreshing(false);
            getDialog("No Internet Connection", "This app requires an internet connection to function properly.\nPlease check your connection and try again.",false);
        }
    }

    @Override
    public void onLocationRetrieved(String city, String state) {
        if(city.isEmpty() || state.isEmpty()){
           getdefaultlocation();
        }else{
            binding.progressHorizontal.setVisibility(View.VISIBLE);
            getWeather(true, city, state);
        }
    }

    @Override
    public void onLocationError(String error,boolean flag) {
        binding.main.setRefreshing(false);
        if(flag){
            getDialog("Do you want to enable location?", error,true);
        }else{
            getDialog("Location Error", error,false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationHelper.handlePermissionsResult(requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        locationHelper.handleSettingsResult(requestCode, resultCode);
    }

    private void getWeather(boolean flag,String city, String state) {
        new WeatherAPI(this).fetchWeatherData(buildUrlByCityState(city, state)
                , new WeatherAPI.WeatherDataCallback(){
                    @Override
                    public void onSuccess(Weather weather) {
                        isLocationweather = flag;
                        myweather = weather;
                        editor.putString("city", city);
                        editor.putString("state", state);
                        editor.apply();
                        setvalue();
                        binding.main.setRefreshing(false);
                    }
                    @Override
                    public void onError(Exception e) {
                        binding.main.setRefreshing(false);
                        binding.progressHorizontal.setVisibility(View.GONE);
                        getDialog("Weather Data Error", "There was an error retrieving the weather data.Please try again later.",false);
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void setvalue() {
        int iconid = Weather.getId(myweather.getCurrentConditions().getIcon());
        binding.ivweathimg.setImageResource(iconid);

        if(Character.isDigit(myweather.getResolvedAddress().split(",")[0].charAt(1))){
            binding.tvtoptitle.setText(getCityNameFromLatLon(myweather.getLatitude(), myweather.getLongitude())
            + ", " + getdate() + " " + myweather.getCurrentConditions().getDatetime());
        }else {
            binding.tvtoptitle.setText(myweather.getResolvedAddress().split(",")[0]
                    + ", " + getdate() + " " + myweather.getCurrentConditions().getDatetime());
        }

        binding.tvtemp.setText(myweather.getCurrentConditions().getTemp(isCelsius));
        binding.tvfeellike.setText("Feels Like: " + myweather.getCurrentConditions().getFeelslike(false));
        binding.tvweathertype.setText(myweather.getCurrentConditions().getConditions() +
                "(" + myweather.getCurrentConditions().getCloudcover() + "% clouds)");
        binding.tvwind.setText(myweather.getCurrentConditions().getwindstext());
        binding.tvhumdity.setText("Humidity: " + myweather.getCurrentConditions().getHumidity() + "%");
        binding.tvuvindex.setText("UV Index: " + myweather.getCurrentConditions().getUvindex());
        binding.tvvisibility.setText("Visibility: " + myweather.getCurrentConditions().getVisibility() + " mi");
        binding.tvsunrise.setText("Sunrise: " + myweather.getCurrentConditions().getSunrise());
        binding.tvsunset.setText("Sunset: " + myweather.getCurrentConditions().getSunset());
        TreeMap<String, Double> tm = new TreeMap<>();

        for(int i = 0; i < myweather.getDays().get(0).getHours().size(); i++){
            tm.put(myweather.getDays().get(0).getHours().get(i).gettimeforchar(),
                    myweather.getDays().get(0).getHours().get(i).getdoubletemp(isCelsius));
        }

        chartMaker.makeChart(tm);
        setHouritemList(true);
        ColorMaker.setColorGradient(window,binding.main, myweather.getCurrentConditions().getdoubletemp(),  "F");
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setHouritemList(boolean firsttime){
        hourItemList.clear();
        int i;
        int j;
        if(myweather.getCurrentConditions().getOnlyHour24() < 23) {
            i = 0;
            j = myweather.getCurrentConditions().getOnlyHour24() + 1;
        }else{
            i = 1;
            j = 0;
        }
        do{
            do{
                hourItemList.add(new HourItem(myweather.getDays().get(i).getDatetime(),
                        myweather.getDays().get(i).getHours().get(j).getDatetime(),
                        myweather.getDays().get(i).getHours().get(j).getIcon(),
                        myweather.getDays().get(i).getHours().get(j).getConditions(),
                        myweather.getDays().get(i).getHours().get(j).getTemp(isCelsius)));

                j++;
            }while (j < myweather.getDays().get(i).getHours().size());
            j = 0;
            i++;
        }while (i < myweather.getDays().size());

        if(firsttime){
            adapter = new DayTimeAdapter(hourItemList);
            binding.rvhouritem.setAdapter(adapter);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            binding.rvhouritem.setLayoutManager(layoutManager);
        }else{
            adapter.notifyDataSetChanged();
        }
        binding.progressHorizontal.setVisibility(View.GONE);
    }

    private String getdate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d", Locale.ENGLISH);
        return sdf.format(new Date());
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void openLocationDialog() {
        DialogInputLocationBinding dialogbinding = DialogInputLocationBinding.inflate(getLayoutInflater());
        androidx.appcompat.app.AlertDialog dialog;

        MaterialAlertDialogBuilder builder;

        builder = new MaterialAlertDialogBuilder(this)
                .setView(dialogbinding.getRoot())
                .setBackground(getDrawable(R.drawable.borderdialogbox))
                .setNegativeButton("CANCEl", (dialog2, which) -> {
                    dialog2.dismiss();
                }).setPositiveButton("OK", (dialog3, which) -> {
                    if(dialogbinding.etlocation.getText().toString().trim().isEmpty()){
                        Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show();
                    }else{
                        dialog3.dismiss();
                        if(dialogbinding.etlocation.getText().toString().contains(",")){
                            binding.progressHorizontal.setVisibility(View.VISIBLE);
                            getWeather(false,dialogbinding.etlocation.getText().toString().split(",")[0].trim(),
                                    dialogbinding.etlocation.getText().toString().split(",")[1].trim());
                        }else{
                            binding.progressHorizontal.setVisibility(View.VISIBLE);
                            getWeather(false,dialogbinding.etlocation.getText().toString().trim(),"");
                        }
                    }
                });

        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.purple));
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.purple));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void getDialog(String title, String message, boolean flag){
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_custom_layout, null);

        TextView titleView = customLayout.findViewById(R.id.dialog_title);
        titleView.setText(title);

        TextView messageView = customLayout.findViewById(R.id.dialog_message);
        messageView.setText(message);

        androidx.appcompat.app.AlertDialog dialog;
        MaterialAlertDialogBuilder builder;

        if(flag) {
             builder = new MaterialAlertDialogBuilder(this)
                    .setView(customLayout)
                    .setBackground(getDrawable(R.drawable.borderdialogbox))
                     .setNegativeButton("No,Thanks", (dialog2, which) -> {
                         dialog2.dismiss();
                         getdefaultlocation();
                     })
                    .setPositiveButton("Yes", (dialog3, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    });
        }else{
             builder = new MaterialAlertDialogBuilder(this)
                    .setView(customLayout)
                    .setBackground(getDrawable(R.drawable.borderdialogbox))
                    .setPositiveButton("OK", (dialog1, which) -> dialog1.dismiss());

        }

        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.purple));
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.purple));

    }


    public void doShare() {
        if(settodayhightemp()){
            @SuppressLint("DefaultLocale")
            String ht = String.format("%.1f",hightemp);
            @SuppressLint("DefaultLocale")
            String lt = String.format("%.1f",lowtemp);

            String text = "Forecast: " + myweather.getCurrentConditions().getConditions() +
                    " with a high of " + ht + (isCelsius ? "°C" : "°F") + " and a low of " +
                    lt + (isCelsius ? "°C" : "°F") + ".\n\nNow : "+ binding.tvtemp.getText().toString() + ", " + myweather.getCurrentConditions().getConditions() +
                    " (" + binding.tvfeellike.getText().toString() + ")\n\n" + binding.tvhumdity.getText().toString() + "\n" + myweather.getCurrentConditions().getwindtxt()
                    + "\nUV Index: " + myweather.getCurrentConditions().getUvindex() +
                    "\nSunrise: " + myweather.getCurrentConditions().getSunrise() +
                    "\nSunset: " + myweather.getCurrentConditions().getSunset() +
                    "\nVisibility: " + myweather.getCurrentConditions().getVisibility() + " mi";
            String subject = "Weather for " + binding.tvtoptitle.getText().toString().split(",")[0] +
                    "( " + locationHelper.getPostalCode(myweather.getLatitude(), myweather.getLongitude()) +" )";

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            sendIntent.putExtra(Intent.EXTRA_TEXT, subject + "\n\n" + text);

            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, "Share to...");
            startActivity(shareIntent);
        }
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
        } catch (IOException e) {
            getdefaultlocation();
        }
        return cityName;
    }

    private boolean settodayhightemp() {
        lowtemp = myweather.getDays().get(0).getHours().get(0).getdoubletemp(isCelsius);
        hightemp = lowtemp;

        for(int i = 1; i < myweather.getDays().get(0).getHours().size(); i++){
            if(lowtemp > myweather.getDays().get(0).getHours().get(i).getdoubletemp(isCelsius)){
                lowtemp = myweather.getDays().get(0).getHours().get(i).getdoubletemp(isCelsius);
            }
            if(hightemp < myweather.getDays().get(0).getHours().get(i).getdoubletemp(isCelsius)){
                hightemp = myweather.getDays().get(0).getHours().get(i).getdoubletemp(isCelsius);
            }
        }
        return true;
    }


    private void getdefaultlocation() {
        if(!spf.getString("city","").isEmpty()  && !spf.getString("state","").isEmpty()){
            binding.progressHorizontal.setVisibility(View.VISIBLE);
            getWeather(true,spf.getString("city", ""), spf.getString("state", ""));
        }else {
            binding.iv5.performClick();
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void openLocationInMap() {
        Uri locationUri =  Uri.parse("geo:0,0?q=" + Uri.encode(binding.tvtoptitle.getText().toString().split(",")[0]));

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, locationUri);

        Intent chooser = Intent.createChooser(mapIntent, "Choose a map app");

        try {
            startActivity(chooser);
        } catch (Exception e) {
            Toast.makeText(this, "No map application found", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onSaveInstanceState(@androidx.annotation.NonNull Bundle outState) {
        outState.putSerializable("myweather", myweather);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@androidx.annotation.NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            myweather = (Weather) savedInstanceState.getSerializable("myweather");
            setvalue();
        }

    }

}