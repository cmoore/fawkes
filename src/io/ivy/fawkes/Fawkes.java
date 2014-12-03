
package io.ivy.fawkes;

import io.ivy.fawkes.beton.events.*;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import pl.betoncraft.betonquest.BetonQuest;

public class Fawkes extends JavaPlugin {

    private final Events events = new Events(this);
    
    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(events, this);

        BetonQuest.getInstance().registerEvents("ivy.testevent", TestEvent.class);
        BetonQuest.getInstance().registerEvents("ivy.questcomplete", QuestComplete.class);
    }

    @Override
    public void onDisable() {
    }
}
