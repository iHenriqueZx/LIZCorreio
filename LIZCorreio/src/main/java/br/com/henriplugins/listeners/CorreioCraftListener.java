package br.com.henriplugins.listeners;

import br.com.henriplugins.LIZCorreio;
import br.com.henriplugins.items.CorreioItem;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class CorreioCraftListener implements Listener {

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if (e.isCancelled()) return;

        ItemStack result = e.getCurrentItem();
        if (result == null) return;

        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;

        UUID playerUUID = e.getWhoClicked().getUniqueId();
        ItemStack correio;

        if (LIZCorreio.useItemsAdder) {
            String id = meta.getPersistentDataContainer().getOrDefault(LIZCorreio.CORREIO_OWNER_KEY, PersistentDataType.STRING, "correio_default");
            try {
                ItemStack iaItem = CustomStack.getInstance(id).getItemStack();
                correio = iaItem != null ? iaItem : CorreioItem.getCorreioItem(LIZCorreio.getInstance().getConfig(), playerUUID);
            } catch (Exception ex) {
                correio = CorreioItem.getCorreioItem(LIZCorreio.getInstance().getConfig(), playerUUID);
            }
        } else {
            correio = CorreioItem.getCorreioItem(LIZCorreio.getInstance().getConfig(), playerUUID);
        }

        e.setCurrentItem(correio);
    }
}
