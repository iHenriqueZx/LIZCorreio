package br.com.henriplugins.menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CorreioMenu implements Listener {
    public static final String TITLE = ChatColor.DARK_GRAY + "Correio";

    public static void openMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        inv.setItem(12, createItem(Material.PAPER, ChatColor.GREEN + "Enviar Encomenda"));
        inv.setItem(14, createItem(Material.CHEST, ChatColor.GREEN + "Recolher Encomenda"));

        player.openInventory(inv);
    }

    private static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> lore = new ArrayList<>();
            lore.add("");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        if (e.getView() == null) return;

        if (!e.getView().getTitle().equals(CorreioMenu.TITLE)) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) return;

        Player player = (Player) e.getWhoClicked();

        switch (e.getCurrentItem().getType()) {
            case PAPER -> {
                e.getWhoClicked().closeInventory();
                CorreioEnviarMenu.open((Player) e.getWhoClicked());
            }
            case CHEST -> {
                e.getWhoClicked().closeInventory();
                CorreioRecolherMenu.open((Player) e.getWhoClicked());
            }
        }
    }
}
