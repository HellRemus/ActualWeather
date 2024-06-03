package com.actualweather.actualweather;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActualWeather extends JavaPlugin {

    private FileConfiguration config;

    @Override
    public void onEnable() {
        // Register the main command executor
        getCommand("actualweather").setExecutor(new ActualWeatherCommandExecutor(this));
        // Add alias for the main command
        getCommand("aw").setExecutor(new ActualWeatherCommandExecutor(this));

        // Load configuration file
        saveDefaultConfig();
        config = getConfig();

        // Read configuration values
        String apiKey = getConfig().getString("API_key");
        String city = getConfig().getString("city");
        int updateInterval = getConfig().getInt("update_interval");

        // Use configuration values in your plugin
        new WeatherCheckTask(apiKey, city, updateInterval).runTaskTimerAsynchronously(this, 0, updateInterval);
    }

    private class WeatherCheckTask extends BukkitRunnable {
        private final String apiKey;
        private final String city;

        public WeatherCheckTask(String apiKey, String city, int updateInterval) {
            this.apiKey = apiKey;
            this.city = city;
        }

        @Override
        public void run() {
            try {
                String urlString = String.format("http://api.weatherapi.com/v1/current.json?key=%s&q=%s", apiKey, city);
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
                    // Weather handling logic
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

