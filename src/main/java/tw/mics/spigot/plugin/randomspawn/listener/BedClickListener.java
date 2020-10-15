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

import java.util.Arrays;

public class BedClickListener extends MyListener {
    private final static Material[] bed = {
            Material.WHITE_BED,
            Material.ORANGE_BED,
            Material.MAGENTA_BED,
            Material.LIGHT_GRAY_BED,
            Material.LIGHT_BLUE_BED,
            Material.YELLOW_BED,
            Material.LIME_BED,
            Material.PINK_BED,
            Material.GRAY_BED,
            Material.CYAN_BED,
            Material.PURPLE_BED,
            Material.BLUE_BED,
            Material.BROWN_BED,
            Material.GREEN_BED,
            Material.RED_BED,
            Material.BLACK_BED,
    };

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
                    Arrays.asList(bed).contains(b.getType()) &&
                b.getWorld().getEnvironment() == Environment.NORMAL &&
                !SpawnLocationManager.checkPlayerSpawn(b.getLocation(), p)
            ){
                p.setBedSpawnLocation(b.getLocation());
                p.sendMessage(Config.LANG_SPAWN_SET.getString());
                event.setCancelled(true);
                return;
            }
        }
    }
}
