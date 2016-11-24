package tw.mics.spigot.plugin.randomspawn.listener;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import tw.mics.spigot.plugin.randomspawn.RandomSpawn;
import tw.mics.spigot.plugin.randomspawn.config.Config;
import tw.mics.spigot.plugin.randomspawn.utils.SpawnLocationManager;

public class BedClickListener extends MyListener {
	public BedClickListener(RandomSpawn instance)
	{
	    super(instance);
	}
	
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event){
        if(event.isCancelled())return;
        Block b = event.getClickedBlock();
        Player p = event.getPlayer();
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(    
                b.getType() == Material.BED_BLOCK &&
                b.getWorld().getEnvironment() == Environment.NORMAL &&
                !SpawnLocationManager.checkPlayerSpawn(b.getLocation(), p) 
            ){
                p.setBedSpawnLocation(b.getLocation(), true);
                p.sendMessage(Config.LANG_SPAWN_SET.getString());
                event.setCancelled(true);
                return;
            }
        }
    }
}
