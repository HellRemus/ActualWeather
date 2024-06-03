package com.actualweather.actualweather;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActualWeather extends JavaPlugin {
    private static final String API_KEY = "YOUR_OPENWEATHERMAP_API_KEY";
    private static final String CITY = "YOUR_CITY_NAME";
    private static final int INTERVAL = 6000; // 6000 ticks = 5 minutes

    @Override
    public void onEnable() {
        new WeatherCheckTask().runTaskTimerAsynchronously(this, 0, INTERVAL);
    }

    private class WeatherCheckTask extends BukkitRunnable {
        @Override
        public void run() {
            try {
                String urlString = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s", CITY, API_KEY);
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                conn.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject weatherData = (JSONObject) parser.parse(content.toString());
                JSONArray weatherArray = (JSONArray) weatherData.get("weather");
                JSONObject weatherObject = (JSONObject) weatherArray.get(0);
                String weather = (String) weatherObject.get("main");

                Bukkit.getScheduler().runTask(ActualWeather.this, () -> {
                    switch (weather.toLowerCase()) {
                        case "rain":
                            Bukkit.getWorlds().get(0).setStorm(true);
                            Bukkit.getWorlds().get(0).setThundering(false);
                            break;
                        case "thunderstorm":
                            Bukkit.getWorlds().get(0).setStorm(true);
                            Bukkit.getWorlds().get(0).setThundering(true);
                            break;
                        default:
                            Bukkit.getWorlds().get(0).setStorm(false);
                            Bukkit.getWorlds().get(0).setThundering(false);
                            break;
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
