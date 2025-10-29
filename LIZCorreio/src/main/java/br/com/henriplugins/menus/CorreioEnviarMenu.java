package br.com.henriplugins.menus;

import br.com.henriplugins.LIZCorreio;
import br.com.henriplugins.database.CorreioDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CorreioEnviarMenu implements Listener {

    private static final Map<UUID, List<ItemStack>> itensAEnviar = new HashMap<>();

    public static void open(Player player) {
        player.openInventory(Bukkit.createInventory(null, 54, "Enviar Encomenda"));
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!e.getView().getTitle().equals("Enviar Encomenda")) return;
        Player p = (Player) e.getPlayer();

        List<ItemStack> itens = new ArrayList<>();
        for (ItemStack item : e.getInventory().getContents()) {
            if (item != null) itens.add(item);
        }

        if (itens.isEmpty()) return;

        itensAEnviar.put(p.getUniqueId(), itens);
        p.sendMessage(ChatColor.YELLOW + "Digite o nick do jogador para enviar a encomenda:");
    }

    public static List<ItemStack> pegarItens(UUID uuid) {
        return itensAEnviar.get(uuid);
    }

    public static void limparItens(UUID uuid) {
        itensAEnviar.remove(uuid);
    }

    public static boolean enviar(UUID remetente, Player destinatario) {
        List<ItemStack> itens = itensAEnviar.remove(remetente);
        if (itens == null) return false;

        try {
            CorreioDatabase db = LIZCorreio.getInstance().getDatabase();
            for (ItemStack item : itens) {
                db.addEncomenda(destinatario.getUniqueId(), item);
            }
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
