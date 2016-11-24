package tw.mics.spigot.plugin.randomspawn.config;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import tw.mics.spigot.plugin.randomspawn.RandomSpawn;

public enum Config {
    DEBUG("debug", false, "is plugin show debug message?"),
    PLAYER_RANDOM_SPAWN_RESPAWN("random-spawn.enable.respawn", true, ""),
    PLAYER_RANDOM_SPAWN_FIRSTJOIN("random-spawn.enable.first-join", true, ""),
    PLAYER_RANDOM_SPAWN_THE_END_PORTAL("random-spawn.enable.the-end-portal", true, ""),
    PLAYER_RANDOM_SPAWN_WORLD("random-spawn.world", "world", ""),
    PLAYER_RANDOM_SPAWN_NEW_LOCATION_TIME("random-spawn.update-time", 1800, ""),

    LANG_SPAWN_SET("lang.spawn-set", "重生點已紀錄。", ""),
    LANG_WORLD_SPAWN_UPDATED("lang.world-spawn-updated", "世界的重生點已經更新。", ""),
    LANG_WORLD_SPAWN_UPDATE_TIME("lang.world-spawn-update-time", "還有 %.0f 秒世界的重生點就會更新。", ""), 
    LANG_BED_OUTSIDE_BORDER("lang.bed-outside-border", "床在邊界外, 重生點已移除", ""), 
    LANG_BED_HAS_LAVA("lang.bed-has-lava", "床被岩漿覆蓋, 重生點已移除", ""),
    LANG_BED_NO_ACCESS("lang.bed-no-access", "床沒有權限, 重生點已移除", "");

    private final String path;
	private final Object value;
	private final String description;
	private static YamlConfiguration cfg;
	private static final File f = new File(RandomSpawn.getInstance().getDataFolder(), "config.yml");
	
	private Config(String path, Object val, String description) {
	    this.path = path;
	    this.value = val;
	    this.description = description;
	}
	
	public String getPath() {
	    return path;
	}
	
	public String getDescription() {
	    return description;
	}
	
	public Object getDefaultValue() {
	    return value;
	}

	public boolean getBoolean() {
	    return cfg.getBoolean(path);
	}
	
	public int getInt() {
	    return cfg.getInt(path);
	}
	
	public double getDouble() {
	    return cfg.getDouble(path);
	}
    
    public String getString() {
        return cfg.getString(path);
    }
    
    public List<String> getStringList() {
        return cfg.getStringList(path);
    }
	
	public static void load() {
		boolean save_flag = false;
		
		RandomSpawn.getInstance().getDataFolder().mkdirs();
        String header = "";
		cfg = YamlConfiguration.loadConfiguration(f);

        for (Config c : values()) {
            if(c.getDescription().toLowerCase().equals("removed")){
                if(cfg.contains(c.getPath())){
                    save_flag = true;
                    cfg.set(c.getPath(), null);
                }
                continue;
            }
            if(!c.getDescription().isEmpty()){
                header += c.getPath() + ": " + c.getDescription() + System.lineSeparator();
            }
            if (!cfg.contains(c.getPath())) {
            	save_flag = true;
                c.set(c.getDefaultValue(), false);
            }
        }
        cfg.options().header(header);
        
        if(save_flag){
        	save();
    		cfg = YamlConfiguration.loadConfiguration(f);
        }
	}
	
	public static void save(){
		try {
			cfg.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void set(Object value, boolean save) {
	    cfg.set(path, value);
	    if (save) {
            save();
	    }
	}
}
