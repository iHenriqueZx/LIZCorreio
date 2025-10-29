package br.com.henriplugins.menus;

import br.com.henriplugins.LIZCorreio;
import br.com.henriplugins.database.CorreioDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CorreioRecolherMenu implements Listener {

    public static void open(Player player) {
        UUID uuid = player.getUniqueId();
        Inventory inv = Bukkit.createInventory(null, 54, "Recolher Encomenda");

        try {
            List<ItemStack> encomendas = LIZCorreio.getInstance()
                    .getDatabase()
                    .getEncomendas(uuid);

            encomendas.forEach(inv::addItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("Recolher Encomenda")) return;

        if (e.getClickedInventory() == e.getView().getTopInventory()) {
            e.setCancelled(false);
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!e.getView().getTitle().equals("Recolher Encomenda")) return;
        Player p = (Player) e.getPlayer();
        UUID uuid = p.getUniqueId();

        Inventory inv = e.getInventory();
        List<ItemStack> restantes = new ArrayList<>();

        for (ItemStack item : inv.getContents()) {
            if (item != null) restantes.add(item);
        }

        try {
            CorreioDatabase db = LIZCorreio.getInstance().getDatabase();

            db.getEncomendas(uuid).forEach(it -> {
                try {
                    db.deleteEncomenda(uuid, it);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            for (ItemStack item : restantes) {
                db.addEncomenda(uuid, item);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
