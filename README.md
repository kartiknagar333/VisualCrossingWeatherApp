# Visual Crossing Weather App

## Overview

The **Visual Crossing Weather App** provides a comprehensive weather service, pulling real-time data from the **Visual Crossing Weather API**. The app displays current weather conditions, hourly forecasts, and a 15-day weather forecast. The app uses various Android features such as **RecyclerViews**, **Volleys**, **Picasso/Glide**, and **Graphing Libraries** for smooth user interaction.

## Features

### Core Features:
- **Current Weather**: Displays temperature, humidity, wind speed, and other weather details.
- **Hourly Forecast**: Shows hourly temperature data for the current day using a horizontal RecyclerView.
- **15-Day Forecast**: Displays a 15-day weather forecast with temperature and conditions for each day.
- **Location**: Automatically detects and uses the user's current location for weather data. You can also manually set the location.
- **Unit Toggle**: Switch between Celsius and Fahrenheit.
- **Temperature-based Background**: The background color changes based on the temperature, ranging from blue (cold) to red (hot).
- **Weather Sharing**: Share current weather data with friends via standard sharing options.
- **Map Location**: View the current location on a map.

### Extra Features:
- **Swipe-Refresh**: Implement a swipe-to-refresh feature to refresh weather data (extra credit).
- **Error Handling**: Handles API errors, location issues, and connectivity problems gracefully.

## Technologies Used
- **Android SDK** (API Level 28 and above)
- **RecyclerView** for displaying hourly and daily forecasts.
- **Volleys** for handling network requests to the Visual Crossing API.
- **Picasso/Glide** for loading weather icons.
- **Graphing Library** to display temperature data.
- **View Binding** to access and manipulate UI components.
- **Gradients** for temperature-based background colors.

## API Usage
The app pulls weather data from the Visual Crossing Weather API. You need to set up a free account to get your API key. (https://www.visualcrossing.com/)
- Replace [location] with a city name (e.g., "Chicago, IL") or latitude/longitude.
- Replace [YOUR_API_KEY] with your personal API key.

## Screenshots
- **HomeActivity:**
<div align="center">
  <img src="https://github.com/user-attachments/assets/6a916427-6a6a-48d4-bdd7-8ecf117f3c65" alt="Home" width="336" height="748" hspace = "10px"/>

  <img src="https://github.com/user-attachments/assets/498c91e6-2d30-473b-9963-d792a7baf77f" alt="Home" width="336" height="748" />
</div>
<br>

- **SearchCityDialog:** To get weather details 
<div align="center">
  <img src="https://github.com/user-attachments/assets/8e8d7aed-9efa-4587-b1b6-30c576813305" alt="Home" width="336" height="748"/>
</div>
<br>

- **ShareDialog:** To share weather deatils 
<div align="center">
  <img src="https://github.com/user-attachments/assets/da93ec55-786b-4056-af63-a701edec74c1" alt="Home" width="336" height="748"/>
</div>
<br>

- **15-DayWeatherForecast:**
<div align="center">
  <img src="https://github.com/user-attachments/assets/489081d4-0ea9-4cb9-a0ad-5a0491ff9bd8" alt="Home" width="336" height="748"/>
</div>
<br>
