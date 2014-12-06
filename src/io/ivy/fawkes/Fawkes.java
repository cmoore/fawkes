// -*- Mode: jde; eval: (hs-hide-level 2) -*-

package io.ivy.fawkes;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;
import io.ivy.fawkes.beton.events.*;
import io.ivy.fawkes.cmd.*;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.*;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import pl.betoncraft.betonquest.BetonQuest;

public class Fawkes extends JavaPlugin {

  public boolean verbose = false;
  
  @Override
  public void onEnable() {
    PluginManager pm = getServer().getPluginManager();
    pm.registerEvents(new Events(this), this);
    pm.registerEvents(new Spawner(this), this);
    
    getCommand("fks").setExecutor(new Fks(this));
    
    // We name them ivy.x to differentiate them from the built-in or
    // scripted events.
    BetonQuest.getInstance().registerEvents("ivy.testevent", TestEvent.class);
    BetonQuest.getInstance().registerEvents("ivy.questcomplete", QuestComplete.class);
  }
  
  @Override
  public void onDisable() {
  }
  
  public WorldGuardPlugin getWorldGuard() {
	  Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	  if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
		  this.log("PANIC: Can't get a reference to the WorldGuard plugin instance!");
		  this.log("THIS PLUGIN REQUIRES WORLDGUARD");
		  return null;
	  }
	  
	  return (WorldGuardPlugin) plugin;
  }

  public String region_for_entity(Entity entity) {
    Vector pt = toVector(entity.getLocation());

    RegionManager region_manager = getWorldGuard().getRegionManager(entity.getLocation().getWorld());

    ApplicableRegionSet px = region_manager.getApplicableRegions(pt);
    if (px.size() < 1) {
      return null;
    } else {
      return px.iterator().next().getId();
    }
  }
  
  public void log(String message) {
	    if (this.verbose) {
	      this.getLogger().info(message);
	    }
  }
}
