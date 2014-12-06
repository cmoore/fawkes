
package io.ivy.fawkes;

import io.ivy.fawkes.beton.events.*;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;

import pl.betoncraft.betonquest.BetonQuest;

public class Fawkes extends JavaPlugin {
    
    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new Events(this), this);
        pm.registerEvents(new Spawner(this), this);
        
        // We name them ivy.x to differentiate them from the built-in or
        // scripted events.
        BetonQuest.getInstance().registerEvents("ivy.testevent", TestEvent.class);
        BetonQuest.getInstance().registerEvents("ivy.questcomplete", QuestComplete.class);
    }

    @Override
    public void onDisable() {
    }
}
