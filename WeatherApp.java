// This class retrieves data from API, this backend logic
// will provide latest weather data from external API and
// return it. The GUI will displayed retrieved data.

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp
{
    // Fetch weather data for location
    public static JSONObject getWeatherData(String locationName)
    {
        // Get location coordinates using geolocation API
        JSONArray locationData = getLocationData(locationName);

        // Extract latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // Build API request URL with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m&timezone=America%2FNew_York";

        try
        {
            // Call API and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // Check response status
            // 200 means connection was successful
            if(conn.getResponseCode() != 200)
            {
                System.out.println("Error: Could not connect to API");
                return null;
            }

            // Store JSON data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext())
            {
                // Read and store into string builder
                resultJson.append(scanner.nextLine());
            }

            scanner.close();
            conn.disconnect();

            // Parse through data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
            // Retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            // We want to get the current hour's data
            // So, we need to get the index of our current hour
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // Get weather data
            JSONArray weathercode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            // Get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // Get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windspeedData.get(index);

            // Build the weather JSON data object that we are going to access in our frontend
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;

    }

    // Retrieves geographic coordinates for given location
    public static JSONArray getLocationData(String locationName)
    {
        // Replace any whitespace in location name to + to adhere to API
        locationName = locationName.replaceAll(" ", "+");
        // Build API url with location parameter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try
        {
            // Call API and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // Check response status
            // 200 successful connection
            // 400 bad request
            // 500 internal server error
            if(conn.getResponseCode() != 200)
            {
                System.out.println("Error fetching data from API");
                return null;
            }
            else
            {
                // Store API results
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                // Read and store the resulting JSON data into our string builder
                while(scanner.hasNext())
                {
                    resultJson.append(scanner.nextLine());
                }

                // Close scanner
                scanner.close();
                // Close URL
                conn.disconnect();

                // Parse the JSON string into a JSON object
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                // Get list of location data the API generated from location name
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;

            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // Couldn't find location
        return null;

    }

    private static HttpURLConnection fetchApiResponse(String urlString)
    {
        try
        {
            // Attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Set request method to get
            conn.setRequestMethod("GET");
            // Connect to our API
            conn.connect();

            return conn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        // Could not make a connection
        return null;

    }

    private static int findIndexOfCurrentTime(JSONArray timeList)
    {
        String currentTime = getCurrentTime();

        // Iterate through the time list and see which one matches our current time
        for(int i = 0; i < timeList.size(); i++)
        {
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime))
            {
                return i;//return index
            }

        }

        return 0;

    }

    public static String getCurrentTime()
    {
        // Get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Format date to be 2023-09-02700:00 (This is how it is read in the API)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        // Format and print current date and time
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;

    }

    // Convert the weather code to something more readable
    private static String convertWeatherCode(long weathercode)
    {
        String weatherCondition = "";
        if(weathercode == 0)
        {
            weatherCondition = "Clear";
        }
        else if(weathercode <= 3L && weathercode > 0L)
        {
            weatherCondition = "Cloudy";
        }
        else if((weathercode >= 51L && weathercode <= 67L) || (weathercode >= 80L && weathercode <= 99L))
        {
            weatherCondition = "Rainy";
        }
        else if(weathercode >= 71L && weathercode <= 77L)
        {
            weatherCondition = "Snow";
        }

        return weatherCondition;

    }

}