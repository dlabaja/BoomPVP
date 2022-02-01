package me.dlabaja.boompvp;

import me.dlabaja.boompvp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {

    BoomPVP boomPVP = new BoomPVP();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        try {
            if (command.getName().equalsIgnoreCase("boomkit"))  //boomkit [player] [1-4]
                return boomPVP.CommandBoomkit(args);
            if (command.getName().equalsIgnoreCase("skipmap")) {  //skipmap
                return boomPVP.CommandSkipmap(sender);
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }
}


