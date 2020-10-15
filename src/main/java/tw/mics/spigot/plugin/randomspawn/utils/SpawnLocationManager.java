package tw.mics.spigot.plugin.randomspawn.utils;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tw.mics.spigot.plugin.randomspawn.RandomSpawn;
import tw.mics.spigot.plugin.randomspawn.config.Config;

public class SpawnLocationManager {
    private static Location last_spawnLocation;
    private static long last_spawnLocationTime = 0;
    
    public static boolean useNewSpawn(){
        if(getTimeLeft() < 0){
            return true;
        }
        return false;
    }
    
    public static Location getSpawnLocation() {
        return last_spawnLocation;
    }
    
    public static void teleportPlayerToNewSpawn(Player p){
        //傳送到新世界重生點
        new SpawnFinder(p);
    }

    public static Double getTimeLeft(){
        long time_diff = last_spawnLocationTime + Config.PLAYER_RANDOM_SPAWN_NEW_LOCATION_TIME.getInt() * 1000 - System.currentTimeMillis();
        return Double.valueOf(time_diff / 1000.0D);
    }
    
    public static boolean checkPlayerSpawn(Location l, Player p){
        if(l == null || p.getBedSpawnLocation() == null) return false;
        if(l.getWorld() != p.getBedSpawnLocation().getWorld()) return false;
        Double dist = l.distance(p.getBedSpawnLocation());
        if(dist <= 2.24)
            return true;
        return false;
    }
    
    private static Material[] blockBlockList = {
            Material.STATIONARY_LAVA,
            Material.LAVA,
            Material.STATIONARY_WATER,
            Material.WATER,
    };
    
    private static Biome[] blockBiomeList = {
            Biome.OCEAN,
            Biome.DEEP_OCEAN,
            Biome.FROZEN_OCEAN,
            Biome.RIVER,
            Biome.SWAMPLAND,
    };
    
    private static class SpawnFinder implements Runnable{
        static boolean flag_spawn_finding;
        boolean flag_not_me_finding;
        boolean flag_finded;
        World world;
        Player player;
        double max_distance;
        double center_x;
        double center_z;
        Location location;
        int id;
        int gen_x, gen_z;
        final static int VIEW_DISTANCE = 8;
        final static int CHUNK_PER_TICK = 2;
        
        SpawnFinder(Player p){
            player = p;
            world = Bukkit.getServer().getWorld(Config.PLAYER_RANDOM_SPAWN_WORLD.getString());
            if(Config.PLAYER_RANDOM_SPAWN_USE_BORDER.getBoolean()){
                WorldBorder wb = world.getWorldBorder();
                max_distance = wb.getSize();
                center_x = wb.getCenter().getX();
                center_z = wb.getCenter().getZ();
            } else {
                max_distance = Config.PLAYER_RANDOM_SPAWN_MAX_DISTANCE.getInt();
                center_x = Config.PLAYER_RANDOM_SPAWN_CENTER_X.getInt();
                center_z = Config.PLAYER_RANDOM_SPAWN_CENTER_Z.getInt();
            }
            flag_finded = false;
            gen_x = -VIEW_DISTANCE;
            gen_z = -VIEW_DISTANCE;
            
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RandomSpawn.getInstance(), new Runnable(){
                @Override
                public void run() {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 72000, 1));
                }
            });
            RandomSpawn.getInstance().god.setGod(p, true);

            if(flag_spawn_finding){
                flag_not_me_finding = true;
            } else {
                flag_not_me_finding = false;
                flag_spawn_finding = true;
            }
            id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(RandomSpawn.getInstance(),
                    this, 1, 1);
        }
        
        @Override
        public void run() {
            if(flag_not_me_finding){
                if(!flag_spawn_finding){
                    cancelEffectAndTeleport(last_spawnLocation);
                }
            } else if(flag_finded){
                for(int i = 0; i < CHUNK_PER_TICK; i++){
                    world.getChunkAt(location.getChunk().getX()+gen_x, location.getChunk().getZ()+gen_z).load(true);
                    gen_x++;
                    if(gen_x > VIEW_DISTANCE){
                        gen_x = -VIEW_DISTANCE;
                        gen_z++;
                        if(gen_z > VIEW_DISTANCE){
                            cancelEffectAndTeleport(location);
                            flag_spawn_finding = false;
                            last_spawnLocation = location;
                            last_spawnLocationTime = System.currentTimeMillis();
                            return;
                        }
                    }
                }
            }else if(findNext()){
                world.setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
                location.add(0.5, 0, 0.5);
                flag_finded = true;
            }
        }
        
        boolean findNext(){
            location = world.getHighestBlockAt( (int)(center_x + getRandom(max_distance)), (int)(center_z + getRandom(max_distance))).getLocation();
            
            //if outside border
            if(location.getX() > max_distance) location.setX(max_distance);
            if(location.getY() > max_distance) location.setY(max_distance);
            if(location.getX() < -max_distance) location.setX(-max_distance);
            if(location.getY() < -max_distance) location.setY(-max_distance);
            
            //if bad Biome or lava
            if(Arrays.asList(blockBiomeList).contains(location.getBlock().getBiome())) return false;
            if(Arrays.asList(blockBlockList).contains(location.getBlock().getType())) return false;
            if(Arrays.asList(blockBlockList).contains(location.clone().add(0,-1,0).getBlock().getType())) return false;
            if(Arrays.asList(blockBlockList).contains(location.clone().add(0,-2,0).getBlock().getType())) return false;
            return true;
        }
        
        private void cancelEffectAndTeleport(Location l){
            Bukkit.getServer().getScheduler().cancelTask(id);
            
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            RandomSpawn.getInstance().god.setGod(player, false);
            
            player.sendMessage(Config.LANG_WORLD_SPAWN_UPDATED.getString());
            player.teleport(l);
        }
        
        private int getRandom(double max) {
            return getRandom((int)max);
        }
        
        private int getRandom(int max){
            return new Random().nextInt(max + 1) - (max / 2);
        }
        
    }
    
}