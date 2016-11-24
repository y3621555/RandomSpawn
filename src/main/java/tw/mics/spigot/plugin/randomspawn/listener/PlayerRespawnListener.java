package tw.mics.spigot.plugin.randomspawn.listener;

import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

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
        //TODO 在邊界外
        //TODO 床上有岩漿
        //TODO 床上被堵死
        //TODO 床沒有權限
        return true;
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

            //床安全性確認
            if(event.getPlayer().getBedSpawnLocation() != null && isPlayerBedSave(event.getPlayer())){
                return; //有床且床安全
            }

            //隨機重生
            if(Config.PLAYER_RANDOM_SPAWN_THE_END_PORTAL.getBoolean()){
                Player p = event.getPlayer();
                if(SpawnLocationManager.useNewSpawn()){
                    SpawnLocationManager.teleportPlayerToNewSpawn(p);
                } else {
                    event.setTo(SpawnLocationManager.getSpawnLocation());
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
