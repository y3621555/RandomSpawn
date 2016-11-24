package tw.mics.spigot.plugin.randomspawn.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tw.mics.spigot.plugin.randomspawn.RandomSpawn;
import tw.mics.spigot.plugin.randomspawn.config.Config;
import tw.mics.spigot.plugin.randomspawn.utils.SpawnLocationManager;

public class RspCommand implements CommandExecutor {
	RandomSpawn plugin;
	public RspCommand(RandomSpawn i){
		this.plugin = i;
	}

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if( (sender instanceof Player) && !sender.isOp() ) {
            sender.sendMessage("§4you have no permission");
            return true;
        }
        
        if(args.length != 1){
            return false;
        }
        
        Player p = this.plugin.getServer().getPlayer(args[0]);
        if(p == null){
            sender.sendMessage("Can't find that player");
            return true;
        }
        
        
        if(SpawnLocationManager.useNewSpawn()){
            SpawnLocationManager.teleportPlayerToNewSpawn(p);
        } else {
            p.teleport(SpawnLocationManager.getSpawnLocation());
            p.sendMessage(String.format(Config.LANG_WORLD_SPAWN_UPDATE_TIME.getString(), SpawnLocationManager.getTimeLeft()));
            
        }
            
        
        return true;
    }

}
