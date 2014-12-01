
package io.ivy.fawkes;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Fawkes extends JavaPlugin {

    private final Events events = new Events(this);
    
    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(events, this);
    }

    @Override
    public void onDisable() {
    }
}
