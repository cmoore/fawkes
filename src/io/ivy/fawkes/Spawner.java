// -*- Mode: jde; eval: (hs-hide-level 2) -*-

package io.ivy.fawkes;


import org.bukkit.entity.*;
import org.bukkit.metadata.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import io.ivy.fawkes.Utils;

public class Spawner implements Listener {

  private final Fawkes fawkes;

  public Spawner(Fawkes instance) {
    fawkes = instance;
  }
  
  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent event) {

    fawkes.log("Creature spawning...");
    
    Entity the_entity = event.getEntity();

    String this_region_id = fawkes.region_for_entity(the_entity);
    
    fawkes.log("Spawn in region: " + this_region_id);
    
    if (event.getCreatureType() != null) {
      Entity entity = event.getEntity();

      if (entity.getType().equals(CreatureType.ENDERMAN) ||
          entity.getType().equals(CreatureType.CREEPER) ||
          entity.getType().equals(CreatureType.SPIDER)) {
        fawkes.log("Spawn cancelled.");
        event.setCancelled(true);
      }
      
      int level = Utils.mob_level();
      
      if (event.getCreatureType().equals(CreatureType.ZOMBIE)) {
        
        if (Utils.random_chance(1,1000) > 999) {
          entity.setCustomName("§e(10) §cCrazel");
          entity.setCustomNameVisible(true);
          entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, "10"));
          fawkes.log("Spawned zombie *(10)");
          return;
        } else {
          entity.setCustomName("§e(" + level + ") §9Zombie");
          entity.setCustomNameVisible(true);
          entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, String.valueOf(level)));
          fawkes.log("Spawned zombie (" + level + ")");
          return;
        }
      }
      
      if (event.getCreatureType().equals(CreatureType.SKELETON)) {
        if (Utils.random_chance(1,1000) > 999) {
          entity.setCustomName("§e(10) §cMr. Skellington");
          entity.setCustomNameVisible(true);
          entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, "10"));
          fawkes.log("Spawned skeleton *(10)");
          return;
        } else {
          entity.setCustomName("§e(" + level + ") §9Skeleton");
          entity.setCustomNameVisible(true);
          entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, String.valueOf(level)));
          fawkes.log("Spawned skeleton (" + level + ")");
          return;
        }
      }

      fawkes.log("Spawner fell through: " + the_entity.getType().toString());
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
