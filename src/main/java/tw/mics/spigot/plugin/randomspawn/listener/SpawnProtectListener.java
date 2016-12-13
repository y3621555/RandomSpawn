package tw.mics.spigot.plugin.randomspawn.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import tw.mics.spigot.plugin.randomspawn.RandomSpawn;
import tw.mics.spigot.plugin.randomspawn.config.Config;
import tw.mics.spigot.plugin.randomspawn.utils.SpawnLocationManager;


public class SpawnProtectListener extends MyListener {
    public SpawnProtectListener(RandomSpawn instance)
    {
        super(instance);
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Block b = e.getBlock();
        e.setCancelled(isProtect(b.getLocation()));
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        Block b = e.getBlock();
        e.setCancelled(isProtect(b.getLocation()));
    }
    
    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e){
        Block b = e.getBlockClicked().getLocation()
                .add(e.getBlockFace().getModX(),e.getBlockFace().getModY(),e.getBlockFace().getModZ())
                .getBlock();
        e.setCancelled(isProtect(b.getLocation()));
    }
    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent e){
        Block b = e.getBlockClicked().getLocation()
                .add(e.getBlockFace().getModX(),e.getBlockFace().getModY(),e.getBlockFace().getModZ())
                .getBlock();
        e.setCancelled(isProtect(b.getLocation()));
    }
    
    private boolean isProtect(Location l){
        Location spawn = SpawnLocationManager.getSpawnLocation();
        if(
            spawn != null &&
            l.getWorld() == spawn.getWorld() &&
            l.distance(spawn) < Config.PLAYER_RANDOM_SPAWN_PROTECT_REDIS.getInt()
        ){
            return true;
        }
        return false;
    }
}
