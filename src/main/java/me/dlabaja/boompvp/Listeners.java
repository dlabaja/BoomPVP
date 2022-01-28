package me.dlabaja.boompvp;

import me.dlabaja.boompvp.utils.BoomPVP;
import me.dlabaja.boompvp.utils.Config;
import me.dlabaja.boompvp.utils.Sql;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.SQLException;
import java.util.Objects;

public class Listeners implements Listener {

    public BoomPVP boomPVP = new BoomPVP();

    //Volám když hráč umře
    public void OnPlayerDeath(Player player) {
       boomPVP.RemoveFiredProjectiles(player);
        if (boomPVP.invStats.get(player)) {
            boomPVP.ExitInvisibleMode(player);
        }
        boomPVP.ClearInventory(player);
        boomPVP.AddItems(player);
        player.teleport(BoomPVP.currentLocation);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1f);

        //replacne hashmapu
        boomPVP.smrti.replace(player, boomPVP.smrti.get(player) + 1);
    }

    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "[" + ChatColor.RED + "" + ChatColor.BOLD + "-" + ChatColor.WHITE + "" + ChatColor.BOLD + "] " + event.getPlayer().getName());
        Sql.SaveData(event.getPlayer(), boomPVP);
        boomPVP.ClearDataFromHashMaps(event.getPlayer());
    }

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event) throws SQLException {
        event.getPlayer().setGameMode(GameMode.ADVENTURE);
        event.setJoinMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "[" + ChatColor.GREEN + "" + ChatColor.BOLD + "+" + ChatColor.WHITE + "" + ChatColor.BOLD + "] " + event.getPlayer().getName());
        if (!Sql.PlayerExists(event.getPlayer().getName()))
            Sql.Execute(Sql.AddPlayer(event.getPlayer().getName()));
        boomPVP.LoadDataToHashMaps(event.getPlayer());
        event.getPlayer().teleport(BoomPVP.currentLocation);
        boomPVP.NewScoreboard(event.getPlayer());

        event.getPlayer().getInventory().clear();
        event.getPlayer().setHealth(20);
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "boomkit " + event.getPlayer().getName() + " 1");
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true));
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0, true));
    }

    //hráč spadne do voidu
    @EventHandler
    public void OnPlayerMoveVoid(PlayerMoveEvent event) {
        //pokud spadne pod y = -10, zabije se
        if (event.getPlayer().getLocation().getY() <= Config.min_height)
            event.getPlayer().setHealth(0);
        if (event.getPlayer().getLocation().getY() >= Config.max_height) {
            if (event.getPlayer().getHealth() <= Config.height_damage)
                event.getPlayer().setHealth(0);
            event.getPlayer().setHealth(event.getPlayer().getHealth() - Config.height_damage);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_HURT, 1f, 1f);
        }

        if (Objects.requireNonNull(event.getTo()).getBlock().getType().equals(Material.LAVA) && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            event.getPlayer().setHealth(0);

    }

    //pokud vystřelí na spawnu, šíp se vrátí
    @EventHandler
    public void OnPlayerLaunchProjectile(ProjectileLaunchEvent event) {
        if (event.getEntity().getLocation().getY() >= Config.spawn_height)
            event.setCancelled(true);
    }

    //Aktivuje se při smrti hráče a dá útočníkovi věci + vygeneruje podle posledního damage death message
    @EventHandler
    public void OnPlayerKill(PlayerDeathEvent event) {
        event.setDeathMessage("");
        if (event.getEntity().getKiller() == event.getEntity().getPlayer() || event.getEntity().getKiller() == null) {
            boomPVP.OnSuicide(event);
        } else {
            boomPVP.OnNotSuicide(event);
        }

        boomPVP.killstreak.replace(event.getEntity().getPlayer(), 0);
        OnPlayerDeath(event.getEntity().getPlayer());
        boomPVP.NewScoreboard(event.getPlayer());

    }

    //pokud v inventáři klikne na perlu, perla se vrátí zpět. Takhle při smrti nemůže hráč duplikovat itemy
    @EventHandler
    public void OnInventoryClick(InventoryClickEvent event) {
        if (Objects.requireNonNull(event.getCurrentItem()).getType().equals(Material.ENDER_PEARL) || event.getCurrentItem().getType().equals(Material.ARROW) || Objects.requireNonNull(event.getCurrentItem()).getType().equals(Material.FIREWORK_ROCKET) || Objects.requireNonNull(event.getCurrentItem()).getType().equals(Material.EGG)) {
            event.setCancelled(true);
        }
    }

    //Logika SwapEggu
    @EventHandler
    public void OnEggUse(ProjectileHitEvent event) {
        if (event.getEntityType().equals(EntityType.EGG) && event.getHitEntity() != null) {
            boomPVP.SwapEgg(event);
        }
    }

    @EventHandler
    public void OnInvisWatchUse(PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.CLOCK)) {
            boomPVP.EnterInvisibleMode(event.getPlayer(), event);
        }
        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.MUSIC_DISC_13)) {
            boomPVP.ExitInvisibleMode(event.getPlayer());
        }
    }

    @EventHandler
    public void OnDamage(EntityDamageEvent event) {
        if ((event.getCause() == EntityDamageEvent.DamageCause.FALL && !Config.fall_damage) || event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL)
            event.setCancelled(true);
        if (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.LAVA)
            if (event.getEntity().getType() == EntityType.PLAYER) {
                ((Player) event.getEntity()).setHealth(0);
            }
        if (event.getEntity().getLocation().getY() >= Config.spawn_height)
            event.setCancelled(true);
    }

    @EventHandler
    public void OnPVPInInvisMode(EntityDamageByEntityEvent event) {
        if (boomPVP.cantPVP.contains(event.getDamager())) {
            event.setCancelled(true);
        }
    }

    //Nespawne kuře při hodu
    @EventHandler
    public void OnChickenSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType().equals(EntityType.CHICKEN)) {
            event.getEntity().remove();
        }
    }
}