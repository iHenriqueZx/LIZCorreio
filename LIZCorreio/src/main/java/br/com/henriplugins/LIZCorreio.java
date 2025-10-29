package br.com.henriplugins;

import br.com.henriplugins.commands.CorreioCommand;
import br.com.henriplugins.database.CorreioDatabase;
import br.com.henriplugins.listeners.ChatListener;
import br.com.henriplugins.listeners.CorreioCraftListener;
import br.com.henriplugins.manager.CorreioManager;
import br.com.henriplugins.listeners.CorreioBlockListener;
import br.com.henriplugins.manager.ConfigManager;
import br.com.henriplugins.manager.RecipeManager;
import br.com.henriplugins.menus.CorreioEnviarMenu;
import br.com.henriplugins.menus.CorreioMenu;
import br.com.henriplugins.menus.CorreioRecolherMenu;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public final class LIZCorreio extends JavaPlugin {

    public static boolean useItemsAdder = false;
    private static LIZCorreio instance;

    private ConfigManager configManager;
    private CorreioManager correioManager;
    private CorreioDatabase database;
    private CorreioBlockListener correioBlockListener;

    public static NamespacedKey CORREIO_OWNER_KEY;

    @Override
    public void onEnable() {

        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            useItemsAdder = true;
            getLogger().info("ItemsAdder detectado! Suporte ativado.");
        } else {
            useItemsAdder = false;
            getLogger().warning("ItemsAdder não encontrado! Usando apenas itens vanilla/Base64.");
        }

        instance = this;

        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        try {
            File dbFile = new File(dataFolder, "correio.db");
            if (!dbFile.exists()) dbFile.createNewFile();

            database = new CorreioDatabase(dbFile);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("Falha ao criar/conectar ao banco de dados! Desabilitando plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        try {
            this.correioManager = new CorreioManager(this);
        } catch (SQLException ex) {
            ex.printStackTrace();
            getLogger().severe("Falha ao inicializar o CorreioManager! Desabilitando plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.configManager = new ConfigManager(this);
        configManager.loadConfig();

        CORREIO_OWNER_KEY = new NamespacedKey(this, "correio_owner");

        getCommand("correio").setExecutor(new CorreioCommand());
        Bukkit.getPluginManager().registerEvents(new CorreioMenu(), this);
        Bukkit.getPluginManager().registerEvents(new CorreioEnviarMenu(), this);
        Bukkit.getPluginManager().registerEvents(new CorreioRecolherMenu(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new CorreioCraftListener(), this);

        this.correioBlockListener = new CorreioBlockListener();
        Bukkit.getPluginManager().registerEvents(correioBlockListener, this);

        RecipeManager.registerRecipe(this);

        getLogger().info("LIZCorreio habilitado com sucesso!");
    }

    @Override
    public void onDisable() {
        if (correioManager != null) {
            correioManager.close();
        }
        try {
            if (database != null) database.close();
        } catch (SQLException ignored) {}

        getLogger().info("LIZCorreio desabilitado com sucesso!");
    }

    public CorreioManager getCorreioManager() {
        return correioManager;
    }

    public static LIZCorreio getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CorreioBlockListener getCorreioBlockListener() {
        return correioBlockListener;
    }

    public CorreioDatabase getDatabase() {
        return database;
    }
}
