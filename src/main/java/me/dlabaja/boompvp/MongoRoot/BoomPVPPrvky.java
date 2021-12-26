package me.dlabaja.boompvp.MongoRoot;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BoomPVPPrvky {
    public static Location currentLocation;
    public long[] dayTimeList = new long[]{6000, 18000};
    public HashMap<Location, String> mapToName = new HashMap<>();
    public List<Location> listLokace = new ArrayList<>();
    public List<Player> cantPVP = new ArrayList<>();

    public HashMap<Player, ItemStack[]> inv = new HashMap<>();
    public HashMap<Player, Boolean> invStats = new HashMap<>();
    public HashMap<Player, Integer> killy = new HashMap<>();
    public HashMap<Player, Integer> smrti = new HashMap<>();
    public HashMap<Player, Integer> killstreak = new HashMap<>();
    public static HashMap<String, String> classa = new HashMap<>();

    //vrátí nezničitelný a enchantovaný item
    public ItemStack MakeItem(Material item, int count, Enchantment ench, int lvl) {
        ItemStack stack = new ItemStack(item, count);
        ItemMeta stackMeta = stack.getItemMeta();
        stackMeta.addEnchant(ench, lvl, true);
        stackMeta.setUnbreakable(true);
        stack.setItemMeta(stackMeta);
        return stack;
    }

    //vrátí nezničitelný a enchantovaný armor
    public ItemStack MakeArmor(Material item, int count, Enchantment ench, int lvl) {
        ItemStack stack = new ItemStack(item, count);
        ItemMeta stackMeta = stack.getItemMeta();
        stackMeta.addEnchant(ench, lvl, true);
        stackMeta.setUnbreakable(true);
        stackMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        stack.setItemMeta(stackMeta);
        return stack;
    }

    //vrátí nezničitelný item
    public ItemStack MakeItemUnbreakable(Material item, int count) {
        ItemStack stack = new ItemStack(item, count);
        ItemMeta stackMeta = stack.getItemMeta();
        stackMeta.setUnbreakable(true);
        stack.setItemMeta(stackMeta);
        return stack;
    }

    //vrátí nezničitelný armor
    public ItemStack MakeArmorUnbreakable(Material item, int count) {
        ItemStack stack = new ItemStack(item, count);
        ItemMeta stackMeta = stack.getItemMeta();
        stackMeta.setUnbreakable(true);
        stackMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        stack.setItemMeta(stackMeta);
        return stack;
    }

    //vrátí SwappEgg
    public ItemStack SwapEgg(int count) {
        ItemStack is = new ItemStack(Material.EGG, count);
        ItemMeta bm = is.getItemMeta();
        bm.setDisplayName(ChatColor.GOLD + "SwapEgg");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Tohle bych po politicích neházel...");
        bm.setLore(lore);
        is.setItemMeta(bm);
        return is;
    }

    public ItemStack ExitInvisWatch(int count) {
        ItemStack is = new ItemStack(Material.MUSIC_DISC_13, count);
        ItemMeta bm = is.getItemMeta();
        bm.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "EXIT INVISIBLE MODE");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("whaj ar u reading dis?");
        bm.setLore(lore);
        is.setItemMeta(bm);
        return is;
    }

    public ItemStack InvisWatch(int count) {
        ItemStack is = new ItemStack(Material.CLOCK, count);
        ItemMeta bm = is.getItemMeta();
        bm.setDisplayName(ChatColor.GOLD + "Invisible Watch");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Spy amongus");
        bm.setLore(lore);
        is.setItemMeta(bm);
        return is;
    }
}
