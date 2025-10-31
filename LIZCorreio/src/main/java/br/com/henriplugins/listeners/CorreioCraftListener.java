package br.com.henriplugins.listeners;

import br.com.henriplugins.LIZCorreio;
import br.com.henriplugins.items.CorreioItem;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.UUID;

public class CorreioCraftListener implements Listener {

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if (e.isCancelled()) return;

        Recipe recipe = e.getRecipe();
        if (recipe == null) return;

        NamespacedKey correioKey = new NamespacedKey(LIZCorreio.getInstance(), "correio_item");
        if (!(recipe instanceof org.bukkit.inventory.ShapedRecipe shaped) ||
                !shaped.getKey().equals(correioKey)) {
            return;
        }

        UUID playerUUID = e.getWhoClicked().getUniqueId();

        ItemStack correio = CorreioItem.getCorreioItem(LIZCorreio.getInstance().getConfig(), playerUUID);

        e.getInventory().setResult(correio);
    }
}
