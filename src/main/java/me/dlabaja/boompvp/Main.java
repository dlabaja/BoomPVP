package me.dlabaja.boompvp;

import me.dlabaja.boompvp.utils.Config;
import me.dlabaja.boompvp.utils.Setup;
import me.dlabaja.boompvp.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Objects;

import static me.dlabaja.boompvp.BoomPVP.time;
import static me.dlabaja.boompvp.utils.Utils.log;

public final class Main extends JavaPlugin {
    BoomPVP boompvp = new BoomPVP();

    @Override
    public void onEnable() {
        log = getLogger();
        log.info("BoomPVP ON");

        new Setup().Setup();

        //adds mapps to plugin
        boompvp.listLokace.addAll(BoomPVP.maps.keySet());
        for (int i = 0; i < BoomPVP.maps.keySet().size(); i++) {
            var name = BoomPVP.maps.get(boompvp.listLokace.get(i));
            boompvp.mapToName.put(boompvp.listLokace.get(i), name.substring(1, name.length() - 1)); //removes the " and puts it into
        }

        var timer = Config.time;
        Commands cmd = new Commands();
        Objects.requireNonNull(this.getCommand("boomkit")).setExecutor(cmd);
        Objects.requireNonNull(this.getCommand("skipmap")).setExecutor(cmd);
        getServer().getPluginManager().registerEvents(new Listeners(), this);

        BoomPVP.currentLocation = boompvp.GetNewMap();

        boompvp.border = BoomPVP.currentLocation.getWorld().getWorldBorder();
        boompvp.border.setDamageAmount(Config.world_border_damage);
        boompvp.border.setDamageBuffer(0);
        boompvp.border.setCenter(BoomPVP.currentLocation);
        boompvp.border.setSize(Config.world_border_size);

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () -> {
            for (var player : Bukkit.getOnlinePlayers()) {
                //timer - from 300 to 0
                //time - inversed timer, 0 - 300
                player.setLevel(timer - time);
                if (timer - time <= 5)
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                if (time == 1)
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }

            if (timer - time == Config.border_shrinking_time) {
                boompvp.border.setSize(Config.world_border_min_size, timer - time);
            }

            if (time == timer) {
                boompvp.SwitchMap();
            }
            time++;
        }, 0, 20);
    }

    @Override
    public void onDisable() {
        Utils.log.info("BoomPVP OFF");
        for (var player : Bukkit.getOnlinePlayers()) {
            boompvp.SaveData(player);
            boompvp.ClearDataFromHashMaps(player);
            player.kick(Component.text("BoomPVP reloading..."));
        }
    }
}

