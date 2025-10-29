package br.com.henriplugins.manager;

import br.com.henriplugins.LIZCorreio;
import br.com.henriplugins.database.CorreioDatabase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class CorreioManager {

    private final LIZCorreio plugin;
    private final CorreioDatabase db;
    private final Map<String, UUID> correiosCache = new HashMap<>();

    public CorreioManager(LIZCorreio plugin) throws SQLException {
        this.plugin = plugin;
        File dbFile = new File(plugin.getDataFolder(), "correio.db");
        this.db = new CorreioDatabase(dbFile);
        loadCorreiosToCache();
    }

    private String keyFromLocation(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

    public void registrarCorreioNoChao(UUID owner, Block bloco) {
        String key = keyFromLocation(bloco.getLocation());
        correiosCache.put(key, owner);
        try {
            db.insertCorreio(key, bloco.getWorld().getName(), bloco.getX(), bloco.getY(), bloco.getZ(), owner);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void removerCorreioDoChao(Block bloco) {
        String key = keyFromLocation(bloco.getLocation());
        correiosCache.remove(key);
        try {
            db.deleteCorreio(key);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isCorreio(Block bloco) {
        return correiosCache.containsKey(keyFromLocation(bloco.getLocation()));
    }

    public UUID getDono(Block bloco) {
        return correiosCache.get(keyFromLocation(bloco.getLocation()));
    }

    private void loadCorreiosToCache() {
        try {
            List<CorreioDatabase.CorreioRow> rows = db.loadAllCorreios();
            for (CorreioDatabase.CorreioRow r : rows) {
                correiosCache.put(r.key, r.owner);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void adicionarEncomenda(UUID owner, ItemStack item) {
        try {
            db.addEncomenda(owner, item);
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public List<ItemStack> getEncomendas(UUID owner) {
        try {
            return db.getEncomendas(owner);
        } catch (SQLException | IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void removerEncomenda(UUID owner, ItemStack item) {
        try {
            db.deleteEncomenda(owner, item);
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        try { db.close(); } catch (Exception ignored) {}
    }
}
