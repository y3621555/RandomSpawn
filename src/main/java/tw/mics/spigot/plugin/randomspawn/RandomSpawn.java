package tw.mics.spigot.plugin.randomspawn;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import tw.mics.spigot.plugin.randomspawn.command.RspCommand;
import tw.mics.spigot.plugin.randomspawn.config.Config;
import tw.mics.spigot.plugin.randomspawn.listener.BedClickListener;
import tw.mics.spigot.plugin.randomspawn.listener.GodListener;
import tw.mics.spigot.plugin.randomspawn.listener.PlayerRespawnListener;
import tw.mics.spigot.plugin.randomspawn.listener.SpawnProtectListener;

public class RandomSpawn extends JavaPlugin {
    private static RandomSpawn INSTANCE;
    public GodListener god;

    @Override
    public void onEnable() {
        INSTANCE = this;
        Config.load();
        new BedClickListener(this);
        new PlayerRespawnListener(this);
        new SpawnProtectListener(this);
        god = new GodListener(this);
        this.getCommand("rsp").setExecutor(new RspCommand(this));
    }

    @Override
    public void onDisable() {
        this.logDebug("Unregister Listener!");
        HandlerList.unregisterAll();
        this.logDebug("Unregister Schedule tasks!");
       // this.getServer().getScheduler().cancelAllTasks();
    }

    public static RandomSpawn getInstance() {
        return INSTANCE;
    }

    // log system
    public void log(String str, Object... args) {
        String message = String.format(str, args);
        getLogger().info(message);
    }

    public void logDebug(String str, Object... args) {
        if (Config.DEBUG.getBoolean()) {
            String message = String.format(str, args);
            getLogger().info("(DEBUG) " + message);
        }
    }
}
