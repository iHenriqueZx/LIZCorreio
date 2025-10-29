package br.com.henriplugins.listeners;

import br.com.henriplugins.LIZCorreio;
import br.com.henriplugins.items.CorreioItem;
import br.com.henriplugins.menus.CorreioMenu;
import br.com.henriplugins.manager.CorreioManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class CorreioBlockListener implements Listener {

    private final CorreioManager correioManager;

    public CorreioBlockListener() {
        this.correioManager = LIZCorreio.getInstance().getCorreioManager();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block bloco = e.getBlock();
        if (!correioManager.isCorreio(bloco)) return;

        Player player = e.getPlayer();
        UUID dono = correioManager.getDono(bloco);

        if (!player.getUniqueId().equals(dono)) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Você não pode quebrar o correio de outro jogador!");
            return;
        }

        e.setCancelled(true);
        bloco.setType(Material.AIR);

        ItemStack correioItem = CorreioItem.getCorreioItem(LIZCorreio.getInstance().getConfig(), dono);
        player.getInventory().addItem(correioItem);

        correioManager.removerCorreioDoChao(bloco);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        if (item == null || !item.hasItemMeta()) return;

        if (!item.getItemMeta().getPersistentDataContainer().has(LIZCorreio.CORREIO_OWNER_KEY, PersistentDataType.STRING))
            return;

        UUID ownerId = UUID.fromString(item.getItemMeta().getPersistentDataContainer()
                .get(LIZCorreio.CORREIO_OWNER_KEY, PersistentDataType.STRING));

        Block bloco = e.getBlockPlaced();
        correioManager.registrarCorreioNoChao(ownerId, bloco);
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block bloco = e.getClickedBlock();
        if (bloco == null) return;

        if (!correioManager.isCorreio(bloco)) return;

        Player player = e.getPlayer();
        UUID dono = correioManager.getDono(bloco);

        e.setCancelled(true);

        if (!player.getUniqueId().equals(dono)) {
            player.sendMessage(ChatColor.RED + "Este não é o seu correio!");
            return;
        }

        CorreioMenu.openMenu(player);
    }
}
