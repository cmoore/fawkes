// -*- Mode: jde; eval: (hs-hide-level 2) -*-

package io.ivy.fawkes;


import org.bukkit.entity.*;
import org.bukkit.metadata.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import redis.clients.jedis.Jedis;
import io.ivy.fawkes.Utils;

public class Spawner implements Listener {

  private final Fawkes fawkes;

  public Spawner(Fawkes instance) {
    fawkes = instance;
  }
  
  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent event) {
    
    Entity the_entity = event.getEntity();

    String this_region_id = fawkes.region_for_entity(the_entity);
    String this_region = fawkes.region_for_entity(the_entity);

    if (this_region == null) {
      this_region = "global";
    }

    
    Jedis j = fawkes.pool.getResource();
    String min = j.get("fawkes.regions." + this_region + ".min");
    String max = j.get("fawkes.regions." + this_region + ".max");
    j.close();
    
    if (event.getCreatureType() != null) {
      Entity entity = event.getEntity();
      
      int level = 1;
      
      if (min != null && max != null) {
    	  int min_level = Integer.parseInt(min);
    	  int max_level = Integer.parseInt(max);
    	  level = Utils.random_chance(min_level, max_level);
      }

      if (event.getCreatureType().equals(CreatureType.ZOMBIE)) {
        
        if (Utils.random_chance(1,1000) > 999) {
          entity.setCustomName("§e(10) §cCrazel");
          entity.setCustomNameVisible(true);
          entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, "10"));
          fawkes.log("Spawned zombie *(10) in " + this_region);
          return;
        } else {
          entity.setCustomName("§e(" + level + ") §9Zombie");
          entity.setCustomNameVisible(true);
          entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, String.valueOf(level)));
          fawkes.log("Spawned zombie (" + level + ") in " + this_region);
          return;
        }
      }
      
      if (event.getCreatureType().equals(CreatureType.SKELETON)) {
        if (Utils.random_chance(1,1000) > 999) {
          entity.setCustomName("§e(10) §cMr. Skellington");
          entity.setCustomNameVisible(true);
          entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, "10"));
          fawkes.log("Spawned skeleton *(10) in " + this_region);
          return;
        } else {
          entity.setCustomName("§e(" + level + ") §9Skeleton");
          entity.setCustomNameVisible(true);
          entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, String.valueOf(level)));
          fawkes.log("Spawned skeleton (" + level + ") in " + this_region);
          return;
        }
      }
      entity.setCustomName("§e(" + level + ") §9" + entity.getType().toString());
      entity.setCustomNameVisible(true);
      entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, String.valueOf(level)));
      fawkes.log("Spawned " + entity.getType().toString() + " (" + level + ") in " + this_region);
    }
  }

  // @EventHandler
  // public void on_block_click(PlayerInteractEvent event) {
  //   fawkes.log("Click....");
  //   if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
  //     if (event.getClickedBlock().getType().equals(BlockType.SIGN_POST)) {
  //       Sign sign = (Sign) event.getClickedBlock();
  //       if (sign.getLine(0).equals("[fawkes]")) {
  //         int low_end = Integer.parseInt(sign.getLine(1));
  //         int high_end = Integer.parseInt(sign.getLine(2));
  //         fawkes.log("Marking this zone as level " + low_end + " to " + high_end);
  //       }
  //     }
  //   }
  // }
}
