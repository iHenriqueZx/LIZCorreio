package br.com.henriplugins.items;

import br.com.henriplugins.LIZCorreio;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CorreioItem {

    public static ItemStack getCorreioItemBase(FileConfiguration config) {
        String material = config.getString("correio.material", "CHEST");
        String name = color(config.getString("correio.name", "&f&lCorreio"));
        List<String> lore = new ArrayList<>();
        config.getStringList("correio.lore").forEach(str -> lore.add(color(str)));

        ItemStack item;

        if (LIZCorreio.useItemsAdder && material.contains(":")) {
            try {
                ItemStack iaItem = CustomStack.getInstance(material).getItemStack();
                item = iaItem != null ? iaItem : new ItemStack(Material.CHEST);
            } catch (NoClassDefFoundError e) {
                item = new ItemStack(Material.CHEST);
            }
        } else if (material.length() > 50) {
            item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (meta != null) {
                try {
                    PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), "CorreioItem");
                    PlayerTextures textures = profile.getTextures();
                    textures.setSkin(new URL("http://textures.minecraft.net/texture/" + material));
                    profile.setTextures(textures);
                    meta.setOwnerProfile(profile);
                    meta.setDisplayName(name);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Material mat = Material.matchMaterial(material);
            if (mat == null) mat = Material.CHEST;
            item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(name);
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }

        return item;
    }

    public static ItemStack getCorreioItem(FileConfiguration config, UUID owner) {
        ItemStack item = getCorreioItemBase(config);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(LIZCorreio.CORREIO_OWNER_KEY, PersistentDataType.STRING, owner.toString());
            item.setItemMeta(meta);
        }
        return item;
    }

    private static String color(String s) {
        return s.replace("&", "§");
    }
}
