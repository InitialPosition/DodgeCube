package net.redcocoa.dodgecube;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class DodgeCube extends JavaPlugin {
    public static final Logger LOGGER = Logger.getLogger("DodgeCube");

    @Override
    public void onEnable() {
        // initialize ban db
        BanDB.initBanDatabase();

        // create necessary listeners
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerDeathListener(), this);
        pluginManager.registerEvents(new PlayerLoginListener(), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
