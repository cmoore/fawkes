// -*- Mode: jde; eval: (hs-hide-level 2) -*-

package io.ivy.fawkes;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.*;
import org.bukkit.entity.*;

import io.ivy.fawkes.Utils;

public class Spawner implements Listener {

  private final Fawkes fawkes;
  
  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent event) {

    if (event.getCreatureType() != null) {
      Entity entity = event.getEntity();
      
      if (event.getCreatureType().equals(CreatureType.ENDERMAN) ||
          event.getCreatureType().equals(CreatureType.CREEPER) ||
          event.getCreatureType().equals(CreatureType.SPIDER)) {
        event.setCancelled(true);
      }

      int level = mob_level();
      
      if (event.getCreatureType().equals(CreatureType.ZOMBIE)) {
        
        if (random_chance(1,1000) > 999) {
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
        if (random_chance(1,1000) > 999) {
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
