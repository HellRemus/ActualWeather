package com.actualweather.actualweather;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActualWeather extends JavaPlugin {
    private static final String API_KEY = "c26ead3d4b5b4b8381c185109240306";
    private static final String CITY = "Uppsala";
    private static final int INTERVAL = 6000; // 6000 ticks = 5 minutes

    @Override
    public void onEnable() {
        new WeatherCheckTask().runTaskTimerAsynchronously(this, 0, INTERVAL);
    }

    private class WeatherCheckTask extends BukkitRunnable {
        @Override
        public void run() {
            try {
                String urlString = String.format("http://api.weatherapi.com/v1/current.json?key=%s&q=%s", API_KEY, CITY);
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
                JSONObject current = (JSONObject) weatherData.get("current");
                JSONObject condition = (JSONObject) current.get("condition");
                String weather = (String) condition.get("text");

                Bukkit.getScheduler().runTask(ActualWeather.this, () -> {
                    if (weather.toLowerCase().contains("thunder")) {
                        Bukkit.getWorlds().get(0).setStorm(true);
                        Bukkit.getWorlds().get(0).setThundering(true);
                    } else if (weather.toLowerCase().contains("rain") ||
                            weather.toLowerCase().contains("drizzle") ||
                            weather.toLowerCase().contains("snow") ||
                            weather.toLowerCase().contains("hail")) {
                        Bukkit.getWorlds().get(0).setStorm(true);
                        Bukkit.getWorlds().get(0).setThundering(false);
                    } else {
                        Bukkit.getWorlds().get(0).setStorm(false);
                        Bukkit.getWorlds().get(0).setThundering(false);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


