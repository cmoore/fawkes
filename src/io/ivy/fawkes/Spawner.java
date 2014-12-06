// -*- Mode: jde; eval: (hs-hide-level 2) -*-

package io.ivy.fawkes;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import static com.sk89q.worldguard.bukkit.BukkitUtil.*;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.ApplicableRegionSet;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.*;

import io.ivy.fawkes.Utils;

public class Spawner implements Listener {

  private final Fawkes fawkes;
  private WorldGuardPlugin world_guard;
  
  private WorldGuardPlugin getWorldGuard() {
	  Plugin plugin = fawkes.getServer().getPluginManager().getPlugin("WorldGuard");
	  if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
		  fawkes.getLogger().info("PANIC: Can't get a reference to the WorldGuard plugin instance!");
		  fawkes.getLogger().info("THIS PLUGIN REQUIRES WORLDGUARD");
		  return null;
	  }
	  
	  return (WorldGuardPlugin) plugin;
  }
  
  
  
  public Spawner(Fawkes instance) {
	  fawkes = instance;
	  Plugin plugin = fawkes.getServer().getPluginManager().getPlugin("WorldGuard");
	  if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
		  return;
	  }
	  world_guard = (WorldGuardPlugin) plugin;
  }
  
  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent event) {

	  String region_name = world_guard.getName();
	  fawkes.getLogger().info("REGION NAME: " + region_name);
	  
    if (event.getCreatureType() != null) {
      Entity entity = event.getEntity();
      
      if (event.getCreatureType().equals(CreatureType.ENDERMAN) ||
          event.getCreatureType().equals(CreatureType.CREEPER) ||
          event.getCreatureType().equals(CreatureType.SPIDER)) {
        event.setCancelled(true);
      }

      int level = Utils.mob_level();
      
      if (event.getCreatureType().equals(CreatureType.ZOMBIE)) {
        
        if (Utils.random_chance(1,1000) > 999) {
          entity.setCustomName("§e(10) §cCrazel");
          entity.setCustomNameVisible(true);
          entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, "10"));
        } else {
          entity.setCustomName("§e(" + level + ") §9Zombie");
          entity.setCustomNameVisible(true);
          entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, String.valueOf(level)));
        }
      }
      
      if (event.getCreatureType().equals(CreatureType.SKELETON)) {
        if (Utils.random_chance(1,1000) > 999) {
          entity.setCustomName("§e(10) §cMr. Skellington");
          entity.setCustomNameVisible(true);
          entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, "10"));
        } else {
          entity.setCustomName("§e(" + level + ") §9Skeleton");
          entity.setCustomNameVisible(true);
          entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, String.valueOf(level)));
        }
      }
    }
  }
}
