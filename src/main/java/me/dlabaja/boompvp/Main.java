package me.dlabaja.boompvp;

import me.dlabaja.boompvp.utils.BoomPVP;
import me.dlabaja.boompvp.utils.Config;
import me.dlabaja.boompvp.utils.Setup;
import me.dlabaja.boompvp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

import static me.dlabaja.boompvp.utils.Utils.log;

public final class Main extends JavaPlugin {

    int time = 0;
    WorldBorder border;
    BoomPVP _boomPVP = new BoomPVP();

    @Override
    public void onEnable() {
        log = getLogger();
        log.info("BoomPVP ON");
        new Setup().Setup();

        _boomPVP.listLokace.addAll(BoomPVP.maps.keySet());
        for (int i = 0; i < BoomPVP.maps.keySet().size(); i++) {
            _boomPVP.mapToName.put(_boomPVP.listLokace.get(i), BoomPVP.maps.get(_boomPVP.listLokace.get(i)));
        }

        var timer = Config.time;
        Commands cmd = new Commands();
        Objects.requireNonNull(this.getCommand("boomkit")).setExecutor(cmd);
        getServer().getPluginManager().registerEvents(new Listeners(), this);

        border = Objects.requireNonNull(Bukkit.getWorld("world")).getWorldBorder();
        border.setDamageAmount(Config.world_border_damage);
        border.setDamageBuffer(0);
        GetFirstMap(_boomPVP.listLokace);

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (var player : Bukkit.getOnlinePlayers()) {
                    player.setLevel(timer - time);
                    if (timer - time <= 5)
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    if (time == 1)
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                }

                if (time == timer - (timer / 5)) {
                    border.setSize(Config.world_border_min_size, timer - time);
                }

                if (time == timer) {
                    SwitchMap(_boomPVP.listLokace);
                    time = 0;
                }
                time++;
            }
        }, 0, 20);
    }

    @Override
    public void onDisable() {
        Utils.log.info("BoomPVP OFF");
    }

    public void GetFirstMap(List<Location> listLokace) {
        BoomPVP.currentLocation = GetNewMap(listLokace);
        border.setCenter(BoomPVP.currentLocation);
        border.setSize(Config.world_border_size);
    }

    public Location GetNewMap(List<Location> listLokace) {
        var lokace = BoomPVP.currentLocation;
        var rndLokace = listLokace.get(new Random().nextInt(listLokace.size()));
        while (rndLokace == lokace) {
            rndLokace = listLokace.get(new Random().nextInt(listLokace.size()));
        }
        return rndLokace;
    }

    public void SwitchMap(List<Location> listLokace) {
        BoomPVP.currentLocation = GetNewMap(listLokace);
        System.out.println(BoomPVP.currentLocation);
        Objects.requireNonNull(Bukkit.getWorld("world")).setFullTime(_boomPVP.dayTimeList[new Random().nextInt(_boomPVP.dayTimeList.length)]);
        for (var player : Bukkit.getOnlinePlayers()) {
            player.teleport(BoomPVP.currentLocation);
            //player.sendTitle(_boomPVPPrvky.mapToName.get(BoomPVPPrvky.currentLocation), "", 5, 40, 5);
        }
        border.setCenter(BoomPVP.currentLocation);
        border.setSize(Config.world_border_size);
    }
}

