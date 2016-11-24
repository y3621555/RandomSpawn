package tw.mics.spigot.plugin.randomspawn.listener;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import tw.mics.spigot.plugin.randomspawn.RandomSpawn;

public class GodListener extends MyListener {
    HashSet<Player> god_list;
    public GodListener(RandomSpawn instance)
    {
        super(instance);
        god_list = new HashSet<Player>();
    }
    
    public void setGod(Player p, boolean god){
        if(god){
            god_list.add(p);
        } else {
            god_list.remove(p);
        }
    }
    
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event){
		if(!(event.getEntity() instanceof Player)) return;
        if(god_list.contains(event.getEntity())){
            event.setCancelled(true);
        }
	}
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        if(god_list.contains(event.getPlayer())){
            Location l_from = event.getFrom();
            Location l = event.getTo();
            l.setX(l_from.getX());
            l.setZ(l_from.getZ());
            event.setTo(l);
        }
    }
	
	@EventHandler
	public void onEntityTarget(EntityTargetLivingEntityEvent event){
        if(!(event.getTarget() instanceof Player)) return;
        if(god_list.contains(event.getTarget())){
            event.setCancelled(true);
        }
	}
}
