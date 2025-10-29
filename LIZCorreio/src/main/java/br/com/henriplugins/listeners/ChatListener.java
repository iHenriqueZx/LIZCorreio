package br.com.henriplugins.listeners;

import br.com.henriplugins.LIZCorreio;
import br.com.henriplugins.menus.CorreioEnviarMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        List<ItemStack> itens = CorreioEnviarMenu.pegarItens(uuid);
        if (itens == null || itens.isEmpty()) return;

        e.setCancelled(true);
        String nick = e.getMessage();
        Player target = Bukkit.getPlayerExact(nick);

        UUID destino = uuid;
        boolean valido = true;

        if (target == null || !target.isOnline() || target.getUniqueId().equals(uuid)) {
            valido = false;
        } else {
            destino = target.getUniqueId();
        }

        for (ItemStack item : itens) {
            LIZCorreio.getInstance().getCorreioManager().adicionarEncomenda(destino, item);
        }

        CorreioEnviarMenu.limparItens(uuid);

        if (!valido) {
            p.sendMessage(ChatColor.RED + "Jogador inválido, offline ou você mesmo. Seus itens foram enviados para o seu próprio correio!");
        } else {
            p.sendMessage(ChatColor.GREEN + "Encomenda enviada com sucesso para " + nick + "!");
            target.sendMessage(ChatColor.YELLOW + "Você recebeu uma nova encomenda!");
        }
    }
}
