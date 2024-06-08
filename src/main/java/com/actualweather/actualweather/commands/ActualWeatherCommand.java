package com.actualweather.actualweather.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ActualWeatherCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public ActualWeatherCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to use this command.");
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage("Usage: /actualweather <subcommand>");
            return false;
        }

        String subcommand = args[0];

        switch (subcommand.toLowerCase()) {
            case "setapikey":
                return setAPIKey(sender, args);
            case "help":
                return displayHelp(sender);
            // Add other subcommands here as needed
            default:
                sender.sendMessage("Unknown subcommand. Use '/actualweather help' to see available commands.");
                return false;
        }
    }

    private boolean setAPIKey(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("Usage: /actualweather setapikey <api_key>");
            return false;
        }

        String apiKey = args[1];
        FileConfiguration config = plugin.getConfig();
        config.set("API_key", apiKey);
        plugin.saveConfig();
        sender.sendMessage("API key updated successfully.");
        return true;
    }

    private boolean displayHelp(CommandSender sender) {
        // Display help message
        sender.sendMessage("Available commands:");
        sender.sendMessage("/actualweather setapikey <api_key>: Set API key");
        sender.sendMessage("/actualweather help: Display this help message");
        // Add help for other subcommands here as needed
        return true;
    }
}
