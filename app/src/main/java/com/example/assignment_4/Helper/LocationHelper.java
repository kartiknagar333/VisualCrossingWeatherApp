package com.example.assignment_4.Helper;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.common.api.ResolvableApiException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int LOCATION_SETTINGS_REQUEST = 101;
    private static final int MAX_SETTINGS_PROMPT_RETRIES = 3;
    private static final String TAG = "LocationHelper";

    private int settingsPromptRetryCount = 0;

    public interface LocationCallbackInterface {
        void onLocationRetrieved(String city, String state);
        void onLocationError(String error, boolean openSettings);
    }

    private final Activity activity;
    private final FusedLocationProviderClient fusedLocationClient;
    private final LocationCallbackInterface callbackInterface;

    public LocationHelper(Activity activity, LocationCallbackInterface callbackInterface) {
        this.activity = activity;
        this.callbackInterface = callbackInterface;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public void requestLocationUpdates() {
        if (isLocationPermissionGranted()) {
            checkLocationSettings();
        } else {
            requestLocationPermission();
        }
    }

    private boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    public void handlePermissionsResult(int requestCode, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationSettings();
            } else {
                boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                callbackInterface.onLocationError("Location permission denied.", !shouldShowRationale);
            }
        }
    }

    public void handleSettingsResult(int requestCode, int resultCode) {
        if (requestCode == LOCATION_SETTINGS_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                getLocation();
            } else {
                retryOrNotifySettingsError();
            }
        }
    }

    private void checkLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(activity);

        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(locationSettingsResponse -> getLocation());
        task.addOnFailureListener(this::handleLocationSettingsFailure);
    }

    private void handleLocationSettingsFailure(Exception e) {
        if (e instanceof ResolvableApiException) {
            try {
                ResolvableApiException resolvable = (ResolvableApiException) e;
                resolvable.startResolutionForResult(activity, LOCATION_SETTINGS_REQUEST);
            } catch (IntentSender.SendIntentException ex) {
                callbackInterface.onLocationError("Failed to prompt location settings.", true);
            }
        } else {
            callbackInterface.onLocationError("Location settings are inadequate and cannot be resolved.", true);
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        fetchCityAndState(location);
                    } else {
                        requestLocationUpdatesWithCallback();
                    }
                })
                .addOnFailureListener(e -> callbackInterface.onLocationError("Failed to get location.", false));
    }

    private void retryOrNotifySettingsError() {
        if (settingsPromptRetryCount < MAX_SETTINGS_PROMPT_RETRIES) {
            settingsPromptRetryCount++;
            new Handler(Looper.getMainLooper()).postDelayed(this::checkLocationSettings, 1000);
            Toast.makeText(activity, "Location services are required. Please enable them.", Toast.LENGTH_SHORT).show();
        } else {
            callbackInterface.onLocationError("Location services are required. Please enable them in settings.", true);
        }
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdatesWithCallback() {
        LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                fetchCityAndState(locationResult.getLastLocation());
            } else {
                callbackInterface.onLocationError("Location update returned empty results.", false);
            }
        }
    };

    private void fetchCityAndState(Location location) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String city = address.getLocality();
                String state = address.getAdminArea();
                if (city != null && state != null && !city.trim().isEmpty() && !state.trim().isEmpty()) {
                    callbackInterface.onLocationRetrieved(city, state);
                } else {
                    callbackInterface.onLocationError("Location details incomplete.", false);
                }
            } else {
                callbackInterface.onLocationError("Could not retrieve address.", false);
            }
        } catch (IOException e) {
            callbackInterface.onLocationError("Geocoder failed.", false);
            Log.e(TAG, "Geocoder IOException", e);
        }
    }

    public String getPostalCode(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getPostalCode();  // Get the postal code
            } else {
                Log.e(TAG, "No address found for the provided coordinates.");
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder service failed", e);
            return null;
        }
    }

}
