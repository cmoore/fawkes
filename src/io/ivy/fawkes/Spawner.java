// -*- Mode: jde; eval: (hs-hide-level 2) -*-

package io.ivy.fawkes;


import static com.sk89q.worldguard.bukkit.BukkitUtil.*;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldedit.Vector;

import org.bukkit.Bukkit;
import org.bukkit.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.*;

import io.ivy.fawkes.Utils;
import com.sk89q.worldguard.protection.flags.StateFlag;

import redis.clients.jedis.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import com.sk89q.worldedit.blocks.BlockType;

public class Spawner implements Listener {

  private final Fawkes fawkes;

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
  }
  
  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent event) {

    fawkes.getLogger().info("Creature spawning...");
    
    Entity the_entity = event.getEntity();
    
    WorldGuardPlugin plugin = getWorldGuard();

    Vector pt = toVector(the_entity.getLocation());

    RegionManager region_manager = plugin.getRegionManager(the_entity.getLocation().getWorld());
    ApplicableRegionSet regions = region_manager.getApplicableRegions(pt);

    for (ProtectedRegion region : regions) {
      fawkes.getLogger().info("Spawn in region: " + region.getId());
    }
    
    if (event.getCreatureType() != null) {
      Entity entity = event.getEntity();
      
      if (event.getCreatureType().equals(CreatureType.ENDERMAN) ||
          event.getCreatureType().equals(CreatureType.CREEPER) ||
          event.getCreatureType().equals(CreatureType.SPIDER)) {
        fawkes.getLogger().info("Spawn cancelled.");
        event.setCancelled(true);
      }
      
      int level = Utils.mob_level();
      
      if (event.getCreatureType().equals(CreatureType.ZOMBIE)) {
        
        if (Utils.random_chance(1,1000) > 999) {
          entity.setCustomName("§e(10) §cCrazel");
          entity.setCustomNameVisible(true);
          entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, "10"));
          fawkes.getLogger().info("Spawned zombie *(10)");
          return;
        } else {
          entity.setCustomName("§e(" + level + ") §9Zombie");
          entity.setCustomNameVisible(true);
          entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, String.valueOf(level)));
          fawkes.getLogger().info("Spawned zombie (" + level + ")");
          return;
        }
      }
      
      if (event.getCreatureType().equals(CreatureType.SKELETON)) {
        if (Utils.random_chance(1,1000) > 999) {
          entity.setCustomName("§e(10) §cMr. Skellington");
          entity.setCustomNameVisible(true);
          entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, "10"));
          fawkes.getLogger().info("Spawned skeleton *(10)");
          return;
        } else {
          entity.setCustomName("§e(" + level + ") §9Skeleton");
          entity.setCustomNameVisible(true);
          entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, String.valueOf(level)));
          fawkes.getLogger().info("Spawned skeleton (" + level + ")");
          return;
        }
      }

      fawkes.getLogger().info("Spawner fell through: " + the_entity.getType().toString());
    }
  }

  // @EventHandler
  // public void on_block_click(PlayerInteractEvent event) {
  //   fawkes.getLogger().info("Click....");
  //   if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
  //     if (event.getClickedBlock().getType().equals(BlockType.SIGN_POST)) {
  //       Sign sign = (Sign) event.getClickedBlock();
  //       if (sign.getLine(0).equals("[fawkes]")) {
  //         int low_end = Integer.parseInt(sign.getLine(1));
  //         int high_end = Integer.parseInt(sign.getLine(2));
  //         fawkes.getLogger().info("Marking this zone as level " + low_end + " to " + high_end);
  //       }
  //     }
  //   }
  // }
}
