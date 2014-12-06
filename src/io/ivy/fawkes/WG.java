
package io.ivy.fawkes;

import static com.sk89q.worldguard.bukkit.BukkitUtil.*;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.*;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import io.ivy.fawkes.Fawkes;


public class WG {

  private static WorldGuardPlugin getWorldGuard(Fawkes fawkes) {
	  Plugin plugin = fawkes.getServer().getPluginManager().getPlugin("WorldGuard");
	  if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
		  fawkes.log("PANIC: Can't get a reference to the WorldGuard plugin instance!");
		  fawkes.log("THIS PLUGIN REQUIRES WORLDGUARD");
		  return null;
	  }
	  
	  return (WorldGuardPlugin) plugin;
  }

  public static Region region_for_entity(Entity entity) {
    Vector pt = toVector(entity.getLocation());

    RegionManager region_manager = plugin.getRegionManager(the_entity.getLocation().getWorld());
    region_manager.getApplicableRegions(pt).iterator().next();
    
  }

}
