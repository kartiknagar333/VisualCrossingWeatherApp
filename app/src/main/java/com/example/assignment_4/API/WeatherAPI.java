package com.example.assignment_4.API;


import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.assignment_4.Model.MyVolley;
import com.example.assignment_4.Model.Weather;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WeatherAPI {

    private static final String BASE_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline";
    private static final String API_KEY = "";  // Replace with your actual API key
    private final Context context;

    public WeatherAPI(Context context) {
        this.context = context;
    }


    public static String buildUrlByCityState(final String city, final String state) {
        // Format as "City%2C%20State"
        String formattedLocation = Uri.encode(city + ", " + state);
        Uri.Builder uriBuilder = Uri.parse(BASE_URL + "/" + formattedLocation).buildUpon();
        uriBuilder.appendQueryParameter("key", API_KEY);
        return uriBuilder.toString();
    }

    public static String buildUrlByCoordinates(final double latitude, final double longitude) {
        String formattedLocation = Uri.encode(latitude + "," + longitude);
        Uri.Builder uriBuilder = Uri.parse(BASE_URL + "/" + formattedLocation).buildUpon();
        uriBuilder.appendQueryParameter("key", API_KEY);
        return uriBuilder.toString();
    }

    public void fetchWeatherData(String url, final WeatherDataCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            Weather weatherData = objectMapper.readValue(response, Weather.class);
                            callback.onSuccess(weatherData);

                        } catch (Exception e) {
                            callback.onError(e);
                            Log.d("", "onResponse: " + e.toString());
                            Toast.makeText(context, "Exception: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                });

        MyVolley.getInstance(context).addToRequestQueue(stringRequest);
    }

    // Callback interface to handle success and error responses
    public interface WeatherDataCallback {
        void onSuccess(Weather weather);
        void onError(Exception e);
    }
}
