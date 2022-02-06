package me.dlabaja.boompvp;

import me.dlabaja.boompvp.utils.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {

    BoomPVP boomPVP = new BoomPVP();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        try {
            if (command.getName().equalsIgnoreCase("boomkit"))  //boomkit [1-4]
                return CommandBoomkit((Player) sender, args);
            if (command.getName().equalsIgnoreCase("skipmap")) {  //skipmap
                return CommandSkipmap(sender);
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    public Boolean CommandBoomkit(Player player, String[] args) {
        var volba = args[0];
        return boomPVP.SetKit(player, Integer.parseInt(volba));
    }

    public Boolean CommandSkipmap(CommandSender player) {
        if(player.isOp()){
            BoomPVP.time = Config.time;
            return true;
        }
        return false;
    }
}


