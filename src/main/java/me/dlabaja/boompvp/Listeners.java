package me.dlabaja.boompvp;

import me.dlabaja.boompvp.utils.Config;
import me.dlabaja.boompvp.utils.Sql;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class Listeners implements Listener {

    public BoomPVP boomPVP = new BoomPVP();

    public void OnPlayerDeath(Player player) {
        boomPVP.RemoveFiredProjectiles(player);
        if (boomPVP.isInvisible.get(player)) {
            boomPVP.ExitInvisibleMode(player);
        }
        boomPVP.ClearInventory(player);
        boomPVP.AddItems(player);
        player.teleport(BoomPVP.currentLocation);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1f);

        BoomPVP.smrti.replace(player, BoomPVP.smrti.get(player) + 1);
    }

    @EventHandler
    public void OnPlayerThrowItem(PlayerDropItemEvent event) {
        if (!event.getPlayer().isOp() && !Config.throw_items)
            event.setCancelled(true);
    }

    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "[" + ChatColor.RED + "" + ChatColor.BOLD + "-" + ChatColor.WHITE + "" + ChatColor.BOLD + "] " + event.getPlayer().getName());
        boomPVP.SaveData(event.getPlayer());
        boomPVP.ClearDataFromHashMaps(event.getPlayer());
    }

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().getInventory().clear();
        boomPVP.SetKit(event.getPlayer(), 1);
        event.getPlayer().setGameMode(GameMode.ADVENTURE);
        event.setJoinMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "[" + ChatColor.GREEN + "" + ChatColor.BOLD + "+" + ChatColor.WHITE + "" + ChatColor.BOLD + "] " + event.getPlayer().getName());
        if (!Sql.PlayerExists(event.getPlayer().getName()))
            Sql.Execute(Sql.AddPlayer(event.getPlayer().getName()));
        boomPVP.LoadDataToHashMaps(event.getPlayer());
        event.getPlayer().teleport(BoomPVP.currentLocation);
        boomPVP.NewScoreboard(event.getPlayer());


        event.getPlayer().setHealth(20);
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true));
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0, true));
    }

    @EventHandler
    public void OnPlayerMoveVoid(PlayerMoveEvent event) {
        if (event.getPlayer().getLocation().getY() <= Config.min_height)
            event.getPlayer().setHealth(0);
        if (event.getPlayer().getLocation().getY() >= Config.max_height) {
            if (event.getPlayer().getHealth() <= Config.height_damage)
                event.getPlayer().setHealth(0);
            event.getPlayer().setHealth(event.getPlayer().getHealth() - Config.height_damage);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_HURT, 1f, 1f);
        }
    }

    @EventHandler
    public void OnPlayerLaunchProjectile(ProjectileLaunchEvent event) {
        if (event.getEntity().getLocation().getY() >= Config.spawn_height)
            event.setCancelled(true);
    }

    @EventHandler
    public void OnPlayerKill(PlayerDeathEvent event) {
        event.setDeathMessage("");
        if (event.getEntity().getKiller() == event.getEntity().getPlayer() || event.getEntity().getKiller() == null) {
            boomPVP.OnSuicide(event);
        } else {
            boomPVP.OnNotSuicide(event);
        }

        BoomPVP.killstreak.replace(event.getEntity().getPlayer(), 0);
        OnPlayerDeath(event.getEntity().getPlayer());
        boomPVP.NewScoreboard(event.getPlayer());

    }

    @EventHandler
    public void OnInventoryClick(InventoryClickEvent event) {
        try {
            if (Objects.requireNonNull(event.getCurrentItem()).getType().equals(Material.ENDER_PEARL) || Objects.requireNonNull(event.getCurrentItem()).getType().equals(Material.ARROW) || Objects.requireNonNull(event.getCurrentItem()).getType().equals(Material.FIREWORK_ROCKET) || Objects.requireNonNull(event.getCurrentItem()).getType().equals(Material.EGG)) {
                event.setCancelled(true);
            }
        } catch (Exception ignored) {
        }
    }

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
        if ((event.getCause() == EntityDamageEvent.DamageCause.FALL && !Config.fall_damage) || event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL || event.getEntity().getLocation().getY() >= Config.spawn_height)
            event.setCancelled(true);
        if (event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.LAVA)
            if (event.getEntity().getType() == EntityType.PLAYER)
                ((Player) event.getEntity()).setHealth(0);
        if (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK)
            event.getEntity().setFireTicks(0);
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