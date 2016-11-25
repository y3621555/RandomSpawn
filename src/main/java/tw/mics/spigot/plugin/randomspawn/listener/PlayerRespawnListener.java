package tw.mics.spigot.plugin.randomspawn.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import tw.mics.spigot.plugin.cupboard.CupboardAPI;
import tw.mics.spigot.plugin.randomspawn.RandomSpawn;
import tw.mics.spigot.plugin.randomspawn.config.Config;
import tw.mics.spigot.plugin.randomspawn.utils.SpawnLocationManager;


public class PlayerRespawnListener extends MyListener {
    public PlayerRespawnListener(RandomSpawn instance)
    {
        super(instance);
    }
    
    //床安全確認
    private boolean isPlayerBedSave(Player p){
        Location l = p.getBedSpawnLocation();
        WorldBorder border = l.getWorld().getWorldBorder();
        int max_x = border.getCenter().add(border.getSize()/2, 0, 0).getBlockX();
        int min_x = border.getCenter().add(-border.getSize()/2, 0, 0).getBlockX();
        int max_z = border.getCenter().add(0, 0, border.getSize()/2).getBlockZ();
        int min_z = border.getCenter().add(0, 0, -border.getSize()/2).getBlockZ();
        if(
                l.getBlockX() > max_x ||
                l.getBlockX() < min_x ||
                l.getBlockZ() > max_z ||
                l.getBlockZ() < min_z
        ){
            //Spawn border check
            p.setBedSpawnLocation(null);
          p.sendMessage(Config.LANG_BED_OUTSIDE_BORDER.getString());
            return false;
        }
        boolean limit;
        try {
            Class.forName("tw.mics.spigot.plugin.cupboard.CupboardAPI");
            limit = CupboardAPI.checkIsLimit(l,p);
        } catch (ClassNotFoundException e) {
            limit = false;
        }
        if(limit){
            //Spawn location check
            p.setBedSpawnLocation(null);
            p.sendMessage(Config.LANG_BED_NO_ACCESS.getString());
            return false;
        }
        if(
            l.getBlock().getType() == Material.LAVA ||
            l.getBlock().getType() == Material.STATIONARY_LAVA ||
            l.clone().add(0,1,0).getBlock().getType() == Material.LAVA ||
            l.clone().add(0,1,0).getBlock().getType() == Material.STATIONARY_LAVA
        ){
            p.setBedSpawnLocation(null);
          p.sendMessage(Config.LANG_BED_HAS_LAVA.getString());
            return false;
        } else {
            return true; //床附近沒問題
        }
    }
    
    //第一次加入
    @EventHandler
    public void onPlayerFirstJoin(PlayerJoinEvent event){
        Player p = event.getPlayer();
        if(Config.PLAYER_RANDOM_SPAWN_FIRSTJOIN.getBoolean() && 
                !p.hasPlayedBefore())
        {
            if(SpawnLocationManager.useNewSpawn()){
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                    @Override
                    public void run() {
                        p.sendMessage(Config.LANG_WORLD_SPAWN_EXPIRED.getString());
                    }
                });
                SpawnLocationManager.teleportPlayerToNewSpawn(p);
            } else {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                    @Override
                    public void run() {
                        p.teleport(SpawnLocationManager.getSpawnLocation());
                        p.sendMessage(String.format(Config.LANG_WORLD_SPAWN_UPDATE_TIME.getString(), SpawnLocationManager.getTimeLeft()));
                    }
                });
            }
        }
    }
    
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event){
		Player p = event.getPlayer();
        
        //床復活確認
        if(event.isBedSpawn() && isPlayerBedSave(p)){
            return; //有床且床安全
        }
        
        //隨機重生
        if(Config.PLAYER_RANDOM_SPAWN_RESPAWN.getBoolean()){
            if(SpawnLocationManager.useNewSpawn()){
                p.sendMessage(Config.LANG_WORLD_SPAWN_EXPIRED.getString());
                SpawnLocationManager.teleportPlayerToNewSpawn(event.getPlayer());
            } else {
                event.setRespawnLocation(SpawnLocationManager.getSpawnLocation());
                p.sendMessage(String.format(Config.LANG_WORLD_SPAWN_UPDATE_TIME.getString(), SpawnLocationManager.getTimeLeft()));
            }
        }
	}
	
	//終界門
	@EventHandler
    public void onPortalTeleport(PlayerTeleportEvent event){
        if( event.getCause() == TeleportCause.END_PORTAL && event.getTo().getWorld().getEnvironment() == Environment.NORMAL ){
            Player p = event.getPlayer();
            
            //床安全性確認
            if(p.getBedSpawnLocation() != null && isPlayerBedSave(event.getPlayer())){
                return; //有床且床安全
            }

            //隨機重生
            if(Config.PLAYER_RANDOM_SPAWN_THE_END_PORTAL.getBoolean()){
                if(SpawnLocationManager.useNewSpawn()){
                    p.sendMessage(Config.LANG_WORLD_SPAWN_EXPIRED.getString());
                    SpawnLocationManager.teleportPlayerToNewSpawn(p);
                } else {
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                        @Override
                        public void run() {
                            p.teleport(SpawnLocationManager.getSpawnLocation());
                            p.sendMessage(String.format(Config.LANG_WORLD_SPAWN_UPDATE_TIME.getString(), SpawnLocationManager.getTimeLeft()));
                        }
                    });
                }
            }
        }
    }
}
