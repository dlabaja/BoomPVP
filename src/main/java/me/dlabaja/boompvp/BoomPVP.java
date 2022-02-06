package me.dlabaja.boompvp;

import me.dlabaja.boompvp.utils.Config;
import me.dlabaja.boompvp.utils.Sql;
import me.dlabaja.boompvp.utils.Utils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class BoomPVP {

    public WorldBorder border;
    public static int time;
    public static Location currentLocation;
    public static HashMap<Location, String> maps = new HashMap<>();
    public long[] dayTimeList = new long[]{6000, 18000};
    public List<Player> cantPVP = new ArrayList<>();

    public HashMap<Player, ItemStack[]> inv = new HashMap<>();
    public HashMap<Player, Boolean> invStats = new HashMap<>();
    public HashMap<Player, Integer> killy = new HashMap<>();
    public HashMap<Player, Integer> smrti = new HashMap<>();
    public HashMap<Player, Integer> killstreak = new HashMap<>();
    public static HashMap<Player, Integer> classa = new HashMap<>();
    public ArrayList<Location> listLokace = new ArrayList<>();
    public HashMap<Location, String> mapToName = new HashMap<>();

    public void RemoveFiredProjectiles(Player player) {
        //smaže všechny entity co hráč vystřelil
        for (Entity ent : player.getNearbyEntities(200, 200, 200)) {
            if (ent.getType() == EntityType.ARROW) {
                Arrow arrow = (Arrow) ent;
                if (arrow.getShooter() == player)
                    ent.remove();
            }
            if (ent.getType() == EntityType.ENDER_PEARL) {
                EnderPearl pearl = (EnderPearl) ent;
                if (pearl.getShooter() == player)
                    ent.remove();
            }
        }
    }

    public void ClearInventory(Player player) {
        //odstraní perly a další věci z inv
        player.setHealth(20);
        if (player.getInventory().getItemInOffHand().getType().equals(Material.ENDER_PEARL) || player.getInventory().getItemInOffHand().getType().equals(Material.ARROW)) {
            player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        }
        player.getInventory().remove(Material.ENDER_PEARL);
        player.getInventory().remove(Material.ARROW);
        player.getInventory().remove(Material.EGG);
        player.getInventory().remove(Material.FIREWORK_ROCKET);
    }

    public void AddItems(Player player) {
        //přidá věci podle třídy kterou má hráč
        if (Objects.requireNonNull(player).getKiller() != null) {
            if (Objects.requireNonNull(player.getLastDamageCause()).getCause() != EntityDamageEvent.DamageCause.PROJECTILE)
                Objects.requireNonNull(player.getKiller()).getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 4));
            if (classa.get(Objects.requireNonNull(player.getKiller())).equals(1))
                player.getKiller().getInventory().addItem(new ItemStack(Material.FIREWORK_ROCKET, 1));
            if (classa.get(player.getKiller()).equals(4))
                player.getKiller().getInventory().addItem(GetSwapEgg(1));
        }

        if (player.getInventory().getItemInOffHand().getType().equals(Material.AIR)) {
            player.getInventory().setItemInOffHand(new ItemStack(Material.ENDER_PEARL, 16));
        } else
            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 16));
        if (classa.get(player).equals(1))
            player.getInventory().addItem(new ItemStack(Material.FIREWORK_ROCKET, 1));
        if (classa.get(player).equals(3))
            player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
        if (classa.get(player).equals(4)) {
            player.getInventory().addItem(GetSwapEgg(2));
        }
    }

    public void NewScoreboard(Player event) {
        Scoreboard board = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        Objective sc;
        try {
            sc = board.registerNewObjective(event.getName(), "dummy", "---" + ChatColor.GOLD + "" + ChatColor.BOLD + "Statistiky" + ChatColor.WHITE + "" + ChatColor.BOLD + "---");
        } catch (IllegalArgumentException e) {
            sc = board.getObjective(event.getName());
            assert sc != null;

            sc.unregister();
            sc = board.registerNewObjective(event.getName(), "dummy", "---" + ChatColor.GOLD + "" + ChatColor.BOLD + "Statistiky" + ChatColor.WHITE + "" + ChatColor.BOLD + "---");
        }
        sc.setDisplaySlot(DisplaySlot.SIDEBAR);

        Objects.requireNonNull(event.getPlayer()).setScoreboard(board);

        float kd;
        if (smrti.get(event) == 0)
            kd = 0;
        else {
            kd = Math.round((((float) killy.get(event)) / ((float) smrti.get(event))) * 100) / 100f;
        }

        sc.getScore(String.format("%13s%-6s", ChatColor.GOLD + "Kills:            " + ChatColor.BOLD + "  " + ChatColor.RESET + ChatColor.WHITE, killy.get(event))).setScore(5);
        sc.getScore(String.format("%13s%-6s", ChatColor.GOLD + "Deaths:           " + ChatColor.WHITE, smrti.get(event))).setScore(4);
        sc.getScore(String.format("%13s%-6s", ChatColor.GOLD + "K/D:               " + ChatColor.WHITE, kd)).setScore(3);
        sc.getScore("").setScore(2);
        sc.getScore(ChatColor.GOLD + "Killstreak:        " + ChatColor.WHITE + killstreak.get(event)).setScore(1);
        event.setScoreboard(board);
    }

    public void OnSuicide(PlayerDeathEvent event) {
        event.setDeathMessage("☠ " + ChatColor.GOLD + "" + Objects.requireNonNull(event.getEntity().getPlayer()).getName() + ChatColor.WHITE + " died");
    }

    public void OnNotSuicide(PlayerDeathEvent event) {
        if (Objects.requireNonNull(event.getEntity().getLastDamageCause()).getDamage() <= 1)
            OnKnockOff(event);
        else
            OnSwordKill(event);
        if (Objects.requireNonNull(event.getEntity().getLastDamageCause()).getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            OnProjectileKill(event);
        }
        Objects.requireNonNull(event.getEntity().getKiller()).playSound(event.getEntity().getKiller().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.5f);
        killy.replace(event.getEntity().getKiller(), killy.get(event.getEntity().getKiller()) + 1);
        killstreak.replace(event.getEntity().getKiller(), killstreak.get(event.getEntity().getKiller()) + 1);
        if (killstreak.get(event.getEntity().getKiller()) % 5 == 0)
            OnKillstreak(event);
        NewScoreboard(event.getEntity().getKiller());
    }

    public Boolean CommandBoomkit(Player player, String[] args) {
        var volba = args[0];
        classa.put(player, Utils.Parse(volba));
        switch (volba) {
            case "1":
                AddClassCommonItems(player, 1);
                player.getInventory().setItem(1, new ItemStack(Material.FIREWORK_ROCKET, 1));
                player.getInventory().setChestplate(MakeArmorUnbreakable(Material.ELYTRA, 1));
                player.getInventory().setLeggings(MakeArmorUnbreakable(Material.DIAMOND_LEGGINGS, 1));
                player.getInventory().setHelmet(MakeArmorUnbreakable(Material.CHAINMAIL_HELMET, 1));
                break;
            case "2":
                AddClassCommonItems(player, 0);
                player.getInventory().setItem(1, MakeItem(Material.IRON_AXE, 1, Enchantment.DAMAGE_ALL, 13));
                player.getInventory().setItem(2, MakeItemUnbreakable(Material.SHIELD, 1));
                player.getInventory().setChestplate(MakeArmor(Material.IRON_CHESTPLATE, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
                player.getInventory().setLeggings(MakeArmorUnbreakable(Material.IRON_LEGGINGS, 1));
                player.getInventory().setBoots(MakeArmorUnbreakable(Material.CHAINMAIL_BOOTS, 1));
                break;
            case "3":
                AddClassCommonItems(player, 1);
                player.getInventory().setItem(1, MakeItem(Material.BOW, 1, Enchantment.ARROW_DAMAGE, 255));
                player.getInventory().setItem(8, new ItemStack(Material.ARROW, 1));
                player.getInventory().setChestplate(MakeArmor(Material.CHAINMAIL_CHESTPLATE, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
                player.getInventory().setLeggings(MakeArmorUnbreakable(Material.LEATHER_LEGGINGS, 1));
                player.getInventory().setHelmet(MakeArmorUnbreakable(Material.CHAINMAIL_HELMET, 1));
                break;
            case "4":
                AddClassCommonItems(player, 1);
                player.getInventory().setItem(1, BoomPVP.GetSwapEgg(2));
                player.getInventory().setItem(2, GetInvisWatch(1));
                player.getInventory().setChestplate(MakeArmor(Material.IRON_CHESTPLATE, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
                player.getInventory().setLeggings(MakeArmorUnbreakable(Material.IRON_LEGGINGS, 1));
                player.getInventory().setBoots(MakeArmorUnbreakable(Material.LEATHER_BOOTS, 1));
                break;
            default:
                return false;
        }
        return true;
    }

    public Boolean CommandSkipmap(CommandSender player) {
        if(player.isOp()){
            SwitchMap();
            return true;
        }
        return false;
    }

    public Location GetNewMap() {
        var lokace = BoomPVP.currentLocation;
        var rndLokace = listLokace.get(new Random().nextInt(listLokace.size()));
        while (rndLokace == lokace) {
            rndLokace = listLokace.get(new Random().nextInt(listLokace.size()));
        }
        return rndLokace;
    }

    public void SwitchMap() {
        BoomPVP.currentLocation = GetNewMap();
        if (Config.day_night_cycle)
            Objects.requireNonNull(Bukkit.getWorld("world")).setFullTime(dayTimeList[new Random().nextInt(dayTimeList.length)]);
        for (var player : Bukkit.getOnlinePlayers()) {
            player.teleport(BoomPVP.currentLocation);
            player.sendTitle(mapToName.get(BoomPVP.currentLocation), "", 5, 40, 5);
        }
        time = 0;
        border.setCenter(BoomPVP.currentLocation);
        border.setSize(Config.world_border_size);
    }

    public void AddClassCommonItems(Player player, int amplifier) {
        player.getInventory().clear();
        player.removePotionEffect(PotionEffectType.SPEED);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2999999, amplifier, true));
        player.getInventory().setItemInOffHand(new ItemStack(Material.ENDER_PEARL, 16));
        player.getInventory().setItem(0, MakeItem(Material.STICK, 1, Enchantment.KNOCKBACK, 5));
    }


    public void OnKillstreak(PlayerDeathEvent event) {
        Bukkit.getServer().broadcastMessage(ChatColor.WHITE + "\n" + "\uD83D\uDD25 " + ChatColor.RED + "" + event.getEntity().getKiller().getName() + ChatColor.WHITE + " has killstreak " + ChatColor.RED + killstreak.get(event.getEntity().getKiller()) + ChatColor.WHITE + "!");
    }

    public void OnKnockOff(PlayerDeathEvent event) {
        event.setDeathMessage("☠ " + ChatColor.GOLD + "" + Objects.requireNonNull(event.getEntity().getKiller()).getName() + ChatColor.WHITE + " \uD83D\uDDE1 " + ChatColor.GOLD + "" + Objects.requireNonNull(event.getEntity().getPlayer()).getName());
    }

    public void OnSwordKill(PlayerDeathEvent event) {
        event.setDeathMessage("☠ " + ChatColor.GOLD + "" + Objects.requireNonNull(event.getEntity().getKiller()).getName() + ChatColor.WHITE + " \uD83E\uDE93 " + ChatColor.GOLD + "" + event.getEntity().getPlayer().getName());
    }

    public void OnProjectileKill(PlayerDeathEvent event) {
        event.setDeathMessage("☠ " + ChatColor.GOLD + "" + Objects.requireNonNull(event.getEntity().getKiller()).getName() + ChatColor.WHITE + " \uD83C\uDFF9 " + ChatColor.GOLD + "" + event.getEntity().getPlayer().getName());
        event.getEntity().getKiller().getInventory().addItem(new ItemStack(Material.ARROW, 1));
    }

    public void SwapEgg(ProjectileHitEvent event) {
        Location trefeny = Objects.requireNonNull(event.getHitEntity()).getLocation();
        Player attacker = (Player) event.getEntity().getShooter();

        if (!(event.getHitEntity() instanceof Player))
            return;

        assert attacker != null;
        event.getHitEntity().teleport(attacker.getLocation());
        attacker.teleport(trefeny);
        Player pl = (Player) event.getHitEntity();
        pl.damage(1, attacker);
        attacker.playSound(attacker.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        attacker.spawnParticle(Particle.DRAGON_BREATH, attacker.getLocation(), 100);
    }

    public void EnterInvisibleMode(Player player, PlayerInteractEvent event) {
        inv.put(player, player.getInventory().getContents());
        player.getInventory().clear();
        player.getInventory().setItem(8, GetExitInvisWatch(1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, true));
        event.setCancelled(true);
        invStats.put(player, true);
        cantPVP.add(player);

        for (var pl : Bukkit.getOnlinePlayers()) {
            if (pl != player && classa.get(player) != 4) {
                pl.hidePlayer(player);
            }
        }
    }

    public void ExitInvisibleMode(Player player) {
        for (var otherPlayer : Bukkit.getOnlinePlayers()) {
            if (otherPlayer != player)
                otherPlayer.showPlayer(player);
        }
        player.removePotionEffect(PotionEffectType.GLOWING);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true));
        player.getInventory().setContents(inv.get(player));
        invStats.put(player, false);
        cantPVP.remove(player);
    }

    public void ClearDataFromHashMaps(Player player) {
        try {
            killy.remove(player);
            smrti.remove(player);
            killstreak.remove(player);
            inv.remove(player);
            classa.remove(player);
        } catch (Exception ignored) {
        }
    }

    public void LoadDataToHashMaps(Player player) {
        var data = Sql.GetASetData(player);
        killy.put(player, (Integer) data[1]);
        smrti.put(player, (Integer) data[2]);
        killstreak.put(player, (Integer) data[3]);
        invStats.put(player, false);
        BoomPVP.classa.put(player, 1);
    }

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
    public static ItemStack GetSwapEgg(int count) {
        ItemStack is = new ItemStack(Material.EGG, count);
        ItemMeta bm = is.getItemMeta();
        bm.setDisplayName(ChatColor.GOLD + "SwapEgg");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("I don't recommend throwing this on politicians...");
        bm.setLore(lore);
        is.setItemMeta(bm);
        return is;
    }

    public ItemStack GetExitInvisWatch(int count) {
        ItemStack is = new ItemStack(Material.MUSIC_DISC_13, count);
        ItemMeta bm = is.getItemMeta();
        bm.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "EXIT INVISIBLE MODE");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("whai ar u reading dis?");
        bm.setLore(lore);
        is.setItemMeta(bm);
        return is;
    }

    public ItemStack GetInvisWatch(int count) {
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