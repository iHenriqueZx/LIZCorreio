package br.com.henriplugins.manager;

import br.com.henriplugins.LIZCorreio;
import br.com.henriplugins.items.CorreioItem;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.UUID;

public class RecipeManager {

    public static void registerRecipe(LIZCorreio plugin) {
        FileConfiguration config = plugin.getConfig();
        if (!config.getBoolean("correio.recipe.enabled")) return;

        NamespacedKey key = new NamespacedKey(plugin, "correio_item");
        ItemStack item = CorreioItem.getCorreioItemBase(config);

        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape(
                config.getStringList("correio.recipe.shape").toArray(new String[0])
        );

        config.getConfigurationSection("correio.recipe.ingredients")
                .getKeys(false)
                .forEach(c -> recipe.setIngredient(c.charAt(0),
                        Material.matchMaterial(config.getString("correio.recipe.ingredients." + c))));

        Bukkit.addRecipe(recipe);
    }
}
