package com.example.assignment_4.Model;

import android.annotation.SuppressLint;

import com.example.assignment_4.R;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather implements Serializable {

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("longitude")
    private double longitude;

    @JsonProperty("resolvedAddress")
    private String resolvedAddress;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getResolvedAddress() {
        return resolvedAddress;
    }


    @JsonProperty("days")
    private List<Day> days;

    public List<Day> getDays() {
        return days;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public CurrentConditions getCurrentConditions() {
        return currentConditions;
    }

    @JsonProperty("alerts")
    private List<Alert> alerts;

    @JsonProperty("currentConditions")
    private CurrentConditions currentConditions;

    // Getters and Setters...

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Day implements Serializable {
        @JsonProperty("datetime")
        private String datetime;

        @JsonProperty("datetimeEpoch")
        private long datetimeEpoch;

        @JsonProperty("tempmax")
        private double tempmax;

        @JsonProperty("tempmin")
        private double tempmin;

        @JsonProperty("precipprob")
        private int precipprob;

        @JsonProperty("uvindex")
        private int uvindex;

        @JsonProperty("conditions")
        private String conditions;

        @JsonProperty("description")
        private String description;

        @JsonProperty("icon")
        private String icon;

        @JsonProperty("hours")
        private List<Hour> hours;

        public String formatDate() {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, MM/dd", Locale.getDefault());

            try {
                // Parse the input date
                Date date = inputFormat.parse(datetime);

                // Format the date to the desired output
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        public double getavaragetemp(){
            double sum = 0;
            for (Hour hour : hours) {
                sum += hour.getdoubletemp(false);
            }
            return sum / hours.size();
        }

        // Getters
        public String getDatetime() {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date date = sdf.parse(datetime);

                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);

                Calendar targetDate = Calendar.getInstance();
                assert date != null;
                targetDate.setTime(date);
                targetDate.set(Calendar.HOUR_OF_DAY, 0);
                targetDate.set(Calendar.MINUTE, 0);
                targetDate.set(Calendar.SECOND, 0);
                targetDate.set(Calendar.MILLISECOND, 0);

                if (today.equals(targetDate)) {
                    return "Today";
                } else {
                    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                    return dayFormat.format(date);
                }
            } catch (Exception e) {
                return datetime; // Return an empty string if there's an error
            }
        }

        public long getDatetimeEpoch() {
            return datetimeEpoch;
        }

        @SuppressLint("DefaultLocale")
        public String getTempmax(boolean iscelsius) {
            return iscelsius ? String.format("%.0f",((tempmax - 32) * 5 / 9)) + "°C" : String.format("%.0f",tempmax) + "°F";
        }

        @SuppressLint("DefaultLocale")
        public String getTempmin(boolean iscelsius) {
            return iscelsius ? String.format("%.0f",((tempmin - 32) * 5 / 9)) + "°C" : String.format("%.0f",tempmin) + "°F";
        }

        public int getPrecipprob() {
            return precipprob;
        }

        public int getUvindex() {
            return uvindex;
        }

        public String getConditions() {
            return conditions;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }

        public List<Hour> getHours() {
            return hours;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hour implements Serializable {
        @JsonProperty("datetime")
        private String datetime;

        @JsonProperty("datetimeEpoch")
        private long datetimeEpoch;

        @JsonProperty("temp")
        private double temp;

        @JsonProperty("feelslike")
        private double feelslike;

        @JsonProperty("humidity")
        private double humidity;

        @JsonProperty("windgust")
        private double windgust;

        @JsonProperty("windspeed")
        private double windspeed;

        @JsonProperty("winddir")
        private double winddir;

        @JsonProperty("visibility")
        private double visibility;

        @JsonProperty("cloudcover")
        private double cloudcover;

        @JsonProperty("uvindex")
        private int uvindex;

        @JsonProperty("conditions")
        private String conditions;

        @JsonProperty("icon")
        private String icon;

        // Getters

        public String getDatetime() {
            try {
                SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                Date date = sdf24.parse(datetime);

                SimpleDateFormat sdf12 = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
                assert date != null;
                return sdf12.format(date);

            } catch (Exception e) {
                return datetime;
            }
        }

        public String gettimeforchar(){
            return datetime;
        }

        public long getDatetimeEpoch() {
            return datetimeEpoch;
        }

        public double getdoubletemp(boolean isCelsius) {
            return isCelsius ? ((temp - 32) * 5 / 9) : temp;
        }

        @SuppressLint("DefaultLocale")
        public String getTemp(boolean  isCelsius) {
            return  isCelsius ? String.format("%.0f", ((temp - 32) * 5 / 9)) + "°C" : String.format("%.0f",temp) + "°F";
        }

        @SuppressLint("DefaultLocale")
        public String getFeelslike(boolean isCelsius) {
            return  isCelsius ? String.format("%.0f", ((feelslike - 32) * 5 / 9)) + "°C" : String.format("%.0f",feelslike) + "°F";        }

        public double getHumidity() {
            return humidity;
        }

        public double getWindgust() {
            return windgust;
        }

        public double getWindspeed() {
            return windspeed;
        }

        public double getWinddir() {
            return winddir;
        }

        public double getVisibility() {
            return visibility;
        }

        public double getCloudcover() {
            return cloudcover;
        }

        public int getUvindex() {
            return uvindex;
        }

        public String getConditions() {
            return conditions;
        }

        public String getIcon() {
            return icon;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Alert implements Serializable {
        @JsonProperty("event")
        private String event;

        @JsonProperty("headline")
        private String headline;

        @JsonProperty("id")
        private String id;

        @JsonProperty("description")
        private String description;

        public String getEvent() {
            return event;
        }

        public String getHeadline() {
            return headline;
        }

        public String getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentConditions implements Serializable {

        @JsonProperty("datetime")
        private String datetime;

        @JsonProperty("datetimeEpoch")
        private long datetimeEpoch;

        @JsonProperty("temp")
        private double temp;

        @JsonProperty("feelslike")
        private double feelslike;

        @JsonProperty("humidity")
        private double humidity;

        @JsonProperty("windgust")
        private double windgust;

        @JsonProperty("windspeed")
        private double windspeed;

        @JsonProperty("winddir")
        private double winddir;

        @JsonProperty("visibility")
        private double visibility;

        @JsonProperty("cloudcover")
        private double cloudcover;

        @JsonProperty("uvindex")
        private int uvindex;

        @JsonProperty("conditions")
        private String conditions;

        @JsonProperty("icon")
        private String icon;

        @JsonProperty("sunrise")
        private String sunrise;

        @JsonProperty("sunset")
        private String sunset;

        // Getters

        public String getDatetime() {
           return getAMPMTime(datetime);
        }

        public int getOnlyHour24(){
            return Integer.parseInt(datetime.substring(0, 2));
        }
        public long getDatetimeEpoch() {
            return datetimeEpoch;
        }

        public double getdoubletemp() {
            return temp;
        }

        @SuppressLint("DefaultLocale")
        public String getTemp(boolean isCelsius) {
            return isCelsius ? String.format("%.0f", ((temp - 32) * 5 / 9)) + "°C" : String.format("%.0f",temp) + "°F";
        }

        @SuppressLint("DefaultLocale")
        public String getFeelslike(boolean isCelsius) {
            return isCelsius ?  String.format("%.0f", ((feelslike - 32) * 5 / 9)) + "°C" : String.format("%.0f",feelslike) + "°F";

        }

        public double getHumidity() {
            return humidity;
        }

        public double getWindgust() {
            return windgust;
        }

        public double getWindspeed() {
            return windspeed;
        }

        public double getWinddir() {
            return winddir;
        }

        public double getVisibility() {
            return visibility;
        }

        public double getCloudcover() {
            return cloudcover;
        }

        public int getUvindex() {
            return uvindex;
        }

        public String getConditions() {
            return conditions;
        }

        public String getIcon() {
            return icon;
        }

        public String getSunrise() {
            return getAMPMTime(sunrise);
        }

        private static String getAMPMTime(String time) {
            try {
                SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                Date date = sdf24.parse(String.valueOf(time));

                SimpleDateFormat sdf12 = new SimpleDateFormat("h:mm a", Locale.ENGLISH);

                assert date != null;
                return sdf12.format(date);

            } catch (ParseException e) {
                return time;
            }
        }

        public String getSunset() {
            return getAMPMTime(sunset);
        }

        public String getwindtxt() {
            String winds;
            if (winddir >= 337.5 || winddir < 22.5)
                winds =  "N";
            if (winddir >= 22.5 && winddir < 67.5)
                winds = "NE";
            if (winddir >= 67.5 && winddir < 112.5)
                winds = "E";
            if (winddir >= 112.5 && winddir < 157.5)
                winds = "SE";
            if (winddir >= 157.5 && winddir < 202.5)
                winds = "S";
            if (winddir >= 202.5 && winddir < 247.5)
                winds = "SW";
            if (winddir >= 247.5 && winddir < 292.5)
                winds = "W";
            if (winddir >= 292.5 && winddir < 337.5)
                winds = "NW";
            else
                winds = "X"; // We'll use 'X' as the default if we get a bad value

            winds = "Winds: " + winds + " at " + windspeed + " mph";

            return winds;
        }

        public String getwindstext() {
            String winds;
            if (winddir >= 337.5 || winddir < 22.5)
                winds =  "N";
            if (winddir >= 22.5 && winddir < 67.5)
                winds = "NE";
            if (winddir >= 67.5 && winddir < 112.5)
                winds = "E";
            if (winddir >= 112.5 && winddir < 157.5)
                winds = "SE";
            if (winddir >= 157.5 && winddir < 202.5)
                winds = "S";
            if (winddir >= 202.5 && winddir < 247.5)
                winds = "SW";
            if (winddir >= 247.5 && winddir < 292.5)
                winds = "W";
            if (winddir >= 292.5 && winddir < 337.5)
                winds = "NW";
            else
                winds = "X"; // We'll use 'X' as the default if we get a bad value


            if(windgust == 0.0 || String.valueOf(windgust).isEmpty()) {
                winds = "Winds: " + winds + " at" + windspeed + " mph";
            }else{
                winds = "Winds: " + winds + " at" + windspeed + " mph gusting to" + windgust + " mph";

            }
            return winds;
        }

    }

    public static int getId(String resourceName) {
        try {
            Field idField = R.drawable.class.getDeclaredField(resourceName.replace("-", "_"));
            return idField.getInt(idField);
        } catch (Exception e) {
            return R.mipmap.ic_launcher;
        }
    }

}
