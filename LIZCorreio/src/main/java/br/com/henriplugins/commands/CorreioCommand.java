package br.com.henriplugins.commands;

import br.com.henriplugins.LIZCorreio;
import br.com.henriplugins.items.CorreioItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CorreioCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Use: /correio <give|reload>");
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "give" -> {
                if (!sender.hasPermission("lizcorreio.give")) {
                    sender.sendMessage(ChatColor.RED + "Sem permissão!");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Use: /correio give <player>");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Jogador não encontrado.");
                    return true;
                }

                UUID ownerUUID = target.getUniqueId();
                target.getInventory().addItem(
                        CorreioItem.getCorreioItem(LIZCorreio.getInstance().getConfig(), ownerUUID)
                );

                sender.sendMessage(ChatColor.GREEN + "Correio enviado para " + target.getName() + "!");
                target.sendMessage(ChatColor.GREEN + "Você recebeu um item do Correio!");

                return true;
            }

            case "reload" -> {
                if (!sender.hasPermission("lizcorreio.reload")) {
                    sender.sendMessage(ChatColor.RED + "Sem permissão!");
                    return true;
                }

                LIZCorreio.getInstance().reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Configurações recarregadas com sucesso!");
                return true;
            }

            default -> sender.sendMessage(ChatColor.RED + "Subcomando inválido!");
        }

        return true;
    }
}
