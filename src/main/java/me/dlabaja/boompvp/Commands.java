package me.dlabaja.boompvp;

import me.dlabaja.boompvp.utils.BoomPVP;
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

public class Commands implements CommandExecutor {

    BoomPVP _boomPVP = new BoomPVP();
    //Vytvoří konstruktor pro příkazy
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //dá hráči boompvp kit

        if (command.getName().equalsIgnoreCase("boomkit")) { // boomkit JMENO VOLBA(1-4)
            Player player = Bukkit.getPlayer(args[0]);
            String volba = args[1];
            assert player != null;
            if(volba.equals("1")){ //pilot
                Classes(player, 1);
                player.getInventory().setItem(1, new ItemStack(Material.FIREWORK_ROCKET, 1));
                player.getInventory().setChestplate(_boomPVP.MakeArmorUnbreakable(Material.ELYTRA, 1));
                player.getInventory().setLeggings(_boomPVP.MakeArmorUnbreakable(Material.DIAMOND_LEGGINGS, 1));
                player.getInventory().setHelmet(_boomPVP.MakeArmorUnbreakable(Material.CHAINMAIL_HELMET, 1));
            }
            if(volba.equals("2")){ //fighter
                Classes(player, 0);
                player.getInventory().setItem(1, _boomPVP.MakeItem(Material.IRON_AXE, 1, Enchantment.DAMAGE_ALL, 13));
                player.getInventory().setItem(2, _boomPVP.MakeItemUnbreakable(Material.SHIELD, 1));
                player.getInventory().setChestplate(_boomPVP.MakeArmor(Material.IRON_CHESTPLATE, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
                player.getInventory().setLeggings(_boomPVP.MakeArmorUnbreakable(Material.IRON_LEGGINGS, 1));
                player.getInventory().setBoots(_boomPVP.MakeArmorUnbreakable(Material.CHAINMAIL_BOOTS, 1));
            }
            if(volba.equals("3")){ //archer
                Classes(player, 1);
                player.getInventory().setItem(1, _boomPVP.MakeItem(Material.BOW, 1, Enchantment.ARROW_DAMAGE, 255));
                player.getInventory().setItem(8, new ItemStack(Material.ARROW, 1));
                player.getInventory().setChestplate(_boomPVP.MakeArmor(Material.CHAINMAIL_CHESTPLATE, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
                player.getInventory().setLeggings(_boomPVP.MakeArmorUnbreakable(Material.LEATHER_LEGGINGS, 1));
                player.getInventory().setHelmet(_boomPVP.MakeArmorUnbreakable(Material.CHAINMAIL_HELMET, 1));
            }
            if(volba.equals("4")){ //troller
                Classes(player, 1);
                player.getInventory().setItem(1, BoomPVP.GetSwapEgg(2));
                player.getInventory().setItem(2, _boomPVP.GetInvisWatch(1));
                player.getInventory().setChestplate(_boomPVP.MakeArmor(Material.IRON_CHESTPLATE, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
                player.getInventory().setLeggings(_boomPVP.MakeArmorUnbreakable(Material.IRON_LEGGINGS, 1));
                player.getInventory().setBoots(_boomPVP.MakeArmorUnbreakable(Material.LEATHER_BOOTS, 1));

            }
            BoomPVP.classa.put(player, Utils.Parse(volba));
        }
        return true;
    }

    private void Classes(Player player, int amplifier){
        player.getInventory().clear();
        player.removePotionEffect(PotionEffectType.SPEED);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2999999, amplifier, true));
        player.getInventory().setItemInOffHand(new ItemStack(Material.ENDER_PEARL, 16));
        player.getInventory().setItem(0, _boomPVP.MakeItem(Material.STICK, 1, Enchantment.KNOCKBACK, 5));
    }
}


