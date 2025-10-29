package br.com.henriplugins.manager;

import br.com.henriplugins.LIZCorreio;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final LIZCorreio plugin;
    private FileConfiguration config;

    public ConfigManager(LIZCorreio plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
