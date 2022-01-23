package me.dlabaja.boompvp;

import com.mongodb.client.model.FindOneAndUpdateOptions;
import me.dlabaja.boompvp.utils.BoomPVPPrvky;
import me.dlabaja.boompvp.utils.MongoBoomPVP;
import me.dlabaja.boompvp.utils.MongoData;
import me.dlabaja.boompvp.utils.Sql;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.SQLException;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.setOnInsert;

public class BoomPVP implements Listener {

    public BoomPVPPrvky _boomPVPPrvky = new BoomPVPPrvky();

    //Volám když hráč umře
    public void OnPlayerDeath(Player player) {
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

        if (_boomPVPPrvky.invStats.get(player)) {
            ExitInvisibleMode(player);
        }

        //odstraní perly a další věci z inv
        player.setHealth(20);
        if (player.getInventory().getItemInOffHand().getType().equals(Material.ENDER_PEARL) || player.getInventory().getItemInOffHand().getType().equals(Material.ARROW)) {
            player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        }
        player.getInventory().remove(Material.ENDER_PEARL);
        player.getInventory().remove(Material.ARROW);
        player.getInventory().remove(Material.EGG);
        player.getInventory().remove(Material.FIREWORK_ROCKET);

        //přidá věci podle třídy kterou má hráč
        if (Objects.requireNonNull(player).getKiller() != null) {
            if (Objects.requireNonNull(player.getLastDamageCause()).getCause() != EntityDamageEvent.DamageCause.PROJECTILE)
                Objects.requireNonNull(player.getKiller()).getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 4));
            if (BoomPVPPrvky.classa.get(Objects.requireNonNull(player.getKiller()).getName()).equals("1"))
                player.getKiller().getInventory().addItem(new ItemStack(Material.FIREWORK_ROCKET, 1));
            if (BoomPVPPrvky.classa.get(player.getKiller().getName()).equals("4"))
                player.getKiller().getInventory().addItem(_boomPVPPrvky.SwapEgg(1));
        }

        if (player.getInventory().getItemInOffHand().getType().equals(Material.AIR)) {
            player.getInventory().setItemInOffHand(new ItemStack(Material.ENDER_PEARL, 16));
        } else
            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 16));
        if (BoomPVPPrvky.classa.get(player.getName()).equals("1"))
            player.getInventory().addItem(new ItemStack(Material.FIREWORK_ROCKET, 1));
        if (BoomPVPPrvky.classa.get(player.getName()).equals("3"))
            player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
        if (BoomPVPPrvky.classa.get(player.getName()).equals("4")) {
            player.getInventory().addItem(_boomPVPPrvky.SwapEgg(2));
        }
        player.teleport(BoomPVPPrvky.currentLocation);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1f);

        //replacne hashmapu
        _boomPVPPrvky.smrti.replace(player, _boomPVPPrvky.smrti.get(player) + 1);
    }

    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "[" + ChatColor.RED + "" + ChatColor.BOLD + "-" + ChatColor.WHITE + "" + ChatColor.BOLD + "] " + event.getPlayer().getName());
        MongoData.collDoc.replaceOne(eq("name", event.getPlayer().getName()), new Document("name", event.getPlayer().getName())
                .append("kills", _boomPVPPrvky.killy.get(event.getPlayer()))
                .append("deaths", _boomPVPPrvky.smrti.get(event.getPlayer()))
                .append("killstreak", 1));
        _boomPVPPrvky.killy.remove(event.getPlayer());
        _boomPVPPrvky.smrti.remove(event.getPlayer());
        _boomPVPPrvky.killstreak.remove(event.getPlayer());
        _boomPVPPrvky.inv.remove(event.getPlayer());
        BoomPVPPrvky.classa.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event) throws SQLException {
        event.setJoinMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "[" + ChatColor.GREEN + "" + ChatColor.BOLD + "+" + ChatColor.WHITE + "" + ChatColor.BOLD + "] " + event.getPlayer().getName());
        Document doc = new Document("name", event.getPlayer().getName())
                .append("kills", 0)
                .append("deaths", 0)
                .append("killstreak", 0);
        if (!Sql.PlayerExists(event.getPlayer().getName()))
            Sql.Execute(Sql.AddPlayer(event.getPlayer().getName()));
        //load dat a uložení do hashmap
        /*MongoData.collDoc.findOneAndUpdate(eq("name", event.getPlayer().getName()), setOnInsert(doc), new FindOneAndUpdateOptions().upsert(true));

        MongoBoomPVP findDoc = MongoData.coll.find(eq("name", event.getPlayer().getName())).first();
        _boomPVPPrvky.killy.put(event.getPlayer(), Objects.requireNonNull(findDoc).getKills());
        _boomPVPPrvky.smrti.put(event.getPlayer(), findDoc.getDeaths());
        _boomPVPPrvky.killstreak.put(event.getPlayer(), 0);
        _boomPVPPrvky.invStats.put(event.getPlayer(), false);
        BoomPVPPrvky.classa.put(event.getPlayer().getName(), "1");*/

        event.getPlayer().teleport(BoomPVPPrvky.currentLocation);


        //nový scoreboard
        NewScoreboard(event.getPlayer());

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
        if (event.getPlayer().getLocation().getY() <= -10)
            event.getPlayer().setHealth(0);
        if (event.getPlayer().getLocation().getY() >= 57) {
            if(event.getPlayer().getHealth() <= 0.25)
                event.getPlayer().setHealth(0);
            event.getPlayer().setHealth(event.getPlayer().getHealth() - 0.25);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_HURT, 1f, 1f);
        }

        if (Objects.requireNonNull(event.getTo()).getBlock().getType().equals(Material.LAVA) && event.getPlayer().getGameMode().equals(GameMode.SURVIVAL))
            event.getPlayer().setHealth(0);

    }

    //pokud vystřelí na spawnu, šíp se vrátí
    @EventHandler
    public void OnPlayerLaunchProjectile(ProjectileLaunchEvent event) {
        if (event.getEntity().getLocation().getY() >= 51)
            event.setCancelled(true);
    }

    @EventHandler
    public void OnPearlDamage(PlayerTeleportEvent event) {

    }

    //Aktivuje se při smrti hráče a dá útočníkovi věci + vygeneruje podle posledního damage death message
    @EventHandler
    public void OnPlayerKill(PlayerDeathEvent event) {

        event.setDeathMessage("");
        _boomPVPPrvky.killstreak.replace(event.getEntity().getPlayer(), 0);
        String deathmsg;
        if (event.getEntity().getKiller() == event.getEntity().getPlayer() || event.getEntity().getKiller() == null) {
            deathmsg = "☠ " + ChatColor.GOLD + "" + event.getEntity().getPlayer().getName() + ChatColor.WHITE + " umřel";
            NewScoreboard(event.getEntity().getPlayer());
        } else {
            if (Objects.requireNonNull(event.getEntity().getLastDamageCause()).getDamage() <= 1)
                deathmsg = "☠ " + ChatColor.GOLD + "" + event.getEntity().getKiller().getName() + ChatColor.WHITE + " \uD83D\uDDE1 " + ChatColor.GOLD + "" + event.getEntity().getPlayer().getName();
            else
                deathmsg = "☠ " + ChatColor.GOLD + "" + event.getEntity().getKiller().getName() + ChatColor.WHITE + " \uD83E\uDE93 " + ChatColor.GOLD + "" + event.getEntity().getPlayer().getName();
            if (Objects.requireNonNull(event.getEntity().getLastDamageCause()).getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                deathmsg = "☠ " + ChatColor.GOLD + "" + event.getEntity().getKiller().getName() + ChatColor.WHITE + " \uD83C\uDFF9 " + ChatColor.GOLD + "" + event.getEntity().getPlayer().getName();
                event.getEntity().getKiller().getInventory().addItem(new ItemStack(Material.ARROW, 1));
            }
            Objects.requireNonNull(event.getEntity().getKiller()).playSound(event.getEntity().getKiller().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.5f);
            _boomPVPPrvky.killy.replace(event.getEntity().getKiller(), _boomPVPPrvky.killy.get(event.getEntity().getKiller()) + 1);
            _boomPVPPrvky.killstreak.replace(event.getEntity().getKiller(), _boomPVPPrvky.killstreak.get(event.getEntity().getKiller()) + 1);
            if (_boomPVPPrvky.killstreak.get(event.getEntity().getKiller()) % 5 == 0)
                deathmsg = deathmsg + ChatColor.WHITE + "\n" + "\uD83D\uDD25 " + ChatColor.RED + "" + event.getEntity().getKiller().getName() + ChatColor.WHITE + " má killstreak " + ChatColor.RED + _boomPVPPrvky.killstreak.get(event.getEntity().getKiller()) + ChatColor.WHITE + " zabití!";
            NewScoreboard(event.getEntity().getKiller());
        }
        event.setDeathMessage(deathmsg);
        OnPlayerDeath(event.getEntity().getPlayer());
        NewScoreboard(event.getEntity().getPlayer());

    }

    //pokud v inventáři klikne na perlu, perla se vrátí zpět. Takhle při smrti nemůže hráč duplikovat itemy
    @EventHandler
    public void OnInventoryClick(InventoryClickEvent event) {
        try {
            if (Objects.requireNonNull(event.getCurrentItem()).getType().equals(Material.ENDER_PEARL) || event.getCurrentItem().getType().equals(Material.ARROW) || Objects.requireNonNull(event.getCurrentItem()).getType().equals(Material.FIREWORK_ROCKET) || Objects.requireNonNull(event.getCurrentItem()).getType().equals(Material.EGG)) {
                event.setCancelled(true);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //Logika SwapEggu
    @EventHandler
    public void OnEggUse(ProjectileHitEvent event) {
        List<String> classNames = new ArrayList<>(List.of("Pilot", "Archer", "Troller", "Fighter"));
        if (event.getEntityType().equals(EntityType.EGG) && event.getHitEntity() != null && !classNames.contains(event.getHitEntity().getName())) {
            Location trefeny = event.getHitEntity().getLocation();
            Player attacker = (Player) event.getEntity().getShooter();

            if (!(event.getHitEntity() instanceof Player))
                return;

            event.getHitEntity().teleport(attacker.getLocation());
            attacker.teleport(trefeny);
            Player pl = (Player) event.getHitEntity();
            pl.damage(1, attacker);
            attacker.playSound(attacker.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            attacker.spawnParticle(Particle.DRAGON_BREATH, attacker.getLocation(), 100);
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
        player.getInventory().setContents(_boomPVPPrvky.inv.get(player));
        _boomPVPPrvky.invStats.put(player, false);
        _boomPVPPrvky.cantPVP.remove(player);
    }

    @EventHandler
    public void OnInvisWatchUse(PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.CLOCK)) {
            _boomPVPPrvky.inv.put(event.getPlayer(), event.getPlayer().getInventory().getContents());
            event.getPlayer().getInventory().clear();
            event.getPlayer().getInventory().setItem(8, _boomPVPPrvky.ExitInvisWatch(1));
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3, true));
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, true));
            event.setCancelled(true);
            _boomPVPPrvky.invStats.put(event.getPlayer(), true);
            _boomPVPPrvky.cantPVP.add(event.getPlayer());

            for (var player : Bukkit.getOnlinePlayers()) {
                if (player != event.getPlayer()) {
                    if (BoomPVPPrvky.classa.get(player.getName()).equals("4"))
                        player.showPlayer(event.getPlayer());
                    else {
                        player.hidePlayer(event.getPlayer());
                    }
                }
            }
        }
        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.MUSIC_DISC_13)) {
            ExitInvisibleMode(event.getPlayer());
        }
    }

    @EventHandler
    public void OnPVPInInvisMode(EntityDamageByEntityEvent event) {
        if (_boomPVPPrvky.cantPVP.contains(event.getDamager())) {
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


    //nový scoreboard
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
        if (_boomPVPPrvky.smrti.get(event) == 0)
            kd = 0;
        else {
            kd = Math.round((((float) _boomPVPPrvky.killy.get(event)) / ((float) _boomPVPPrvky.smrti.get(event))) * 100) / 100f;
        }

        sc.getScore(String.format("%13s%-6s", ChatColor.GOLD + "Kills:            " + ChatColor.BOLD + "  " + ChatColor.RESET + ChatColor.WHITE, _boomPVPPrvky.killy.get(event))).setScore(5);
        sc.getScore(String.format("%13s%-6s", ChatColor.GOLD + "Deaths:           " + ChatColor.WHITE, _boomPVPPrvky.smrti.get(event))).setScore(4);
        sc.getScore(String.format("%13s%-6s", ChatColor.GOLD + "K/D:               " + ChatColor.WHITE, kd)).setScore(3);
        sc.getScore("").setScore(2);
        sc.getScore(ChatColor.GOLD + "Killstreak:        " + ChatColor.WHITE + _boomPVPPrvky.killstreak.get(event)).setScore(1);
        event.setScoreboard(board);
    }
}