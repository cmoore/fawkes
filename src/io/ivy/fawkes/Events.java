// -*- Mode: jde; eval: (hs-hide-level 2) -*-

package io.ivy.fawkes;

import java.util.List;



import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.metadata.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.inventory.ItemStack;

public class Events implements Listener {

  private final Fawkes fawkes;

  public Events(Fawkes instance) {
    fawkes = instance;
  }

  private int find_mob_level(Entity entity) {
    
    if (entity.hasMetadata("NPC")) {
      return 5;
    }
    
    if (entity.getType().equals(EntityType.LIGHTNING)) {
      return 5;
    }
        
    if (entity.getType().equals(EntityType.PLAYER)) {
      return ((Player)entity).getLevel();
    }
    
    if (entity instanceof Projectile) {
      Projectile p = (Projectile) entity;
      if (p.getShooter() instanceof Player) {
        return find_mob_level((Player)p.getShooter());
      }
    }
    
    if (entity instanceof LightningStrike) {
      fawkes.log("BZZZZZT");
    }
    
    List<MetadataValue> level_values = entity.getMetadata("ivy.level");
    
    if (level_values.isEmpty()) {
      return 1;
    } else {
      return level_values.get(0).asInt();
    }
  }
  
  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    
    Entity damager = event.getDamager();
    Entity target = event.getEntity();
    
    Double pre_damage = Double.valueOf(event.getDamage());
    Double end_damage = Double.valueOf(event.getFinalDamage());
    
    String damager_type = damager.getType().toString();
    String target_type = target.getType().toString();
    
    int damager_level = find_mob_level(damager);
    int target_level = find_mob_level(target);
    
    double level_difference = damager_level - target_level;
    
    double new_damage_level = end_damage + (level_difference * .5);
    
    if (new_damage_level < 0) {
      new_damage_level = 0;
    }
    
    fawkes.log(damager + "(" + find_mob_level(damager) + ") HIT " +
               target + "(" + find_mob_level(target) + ") for " +
               pre_damage + "/" + end_damage + " -> " + new_damage_level);
    event.setDamage(new_damage_level);
  }
  
  @EventHandler
  public void onEntityDeath(EntityDeathEvent event) {
    
    if (event.getEntity().getKiller() != null) {
      
      Entity entity = event.getEntity();
      Player player = event.getEntity().getKiller();
      
      int player_level = player.getLevel();
      int mob_level = find_mob_level(entity);
      
      
      
      // http://www.wowwiki.com/Formulas:Mob_XP

      double base_mob_xp = 0;

      // knobs to turn...
      if (mob_level < 6) {
        base_mob_xp = mob_level * .5;
      } else {
        base_mob_xp = mob_level * .4;
      }

      double experience_to_add = 0;

      if (player_level < mob_level) {
        experience_to_add = base_mob_xp * (1 + 0.05 * (mob_level - player_level));
      }

      if (player_level > mob_level) {
        experience_to_add = base_mob_xp * (1 - (player_level - mob_level) / find_zero_difference(player_level));
      }

      if (player_level == mob_level) {
        experience_to_add = ((player_level * 5) + 45) * .2;
      }
      
      double end_xp = Math.round(experience_to_add * .4);
      
      fawkes.log(player.getName() + "(" + player_level + ") killed " + entity.getType().toString() + "(" + mob_level + ") XP: " + base_mob_xp + " <> " + experience_to_add + " -> " + end_xp);

      int ok_go = Integer.valueOf((int) Math.round(end_xp));
      
      player.giveExp(ok_go);
      player.sendMessage("You recieve " + ok_go + " experience!");

    }
  }
  
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
  }
  
  @EventHandler
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    
    if (event.getEntityType().equals(EntityType.PLAYER)) {
      if (event.getFoodLevel() < 10) {
        Player player = (Player) event.getEntity();
        int food_level = event.getFoodLevel();
        fawkes.log("Food level: " + String.valueOf(food_level));
      }
    }
  }


  public int find_zero_difference(int cl) {
    // cl = character level.
    
    if (cl <= 7)
      return 5;
    if (cl >= 8 && cl < 9)
      return 6;
    if (cl >= 10 && cl <= 11)
      return 7;
    if (cl >= 12 && cl <= 15)
      return 8;
    
    if (cl >= 16 && cl <= 19)
      return 9;
    if (cl >= 20 && cl <= 29)
      return 11;
    if (cl >= 30 && cl <= 39)
      return 12;
    if (cl >= 40 && cl <= 44)
      return 13;
    if (cl >= 45 && cl <= 49)
      return 14;
    if (cl >= 50 && cl <= 54)
      return 15;
    if (cl >= 55 && cl <= 59)
      return 16;
    if (cl >= 60 && cl <= 79)
      return 17;
    return 18;
  }

  private void mark_chest_regular(Chest chest) {
	  chest.setMetadata("ivy.loot", new FixedMetadataValue(fawkes, "small"));
    fawkes.log("Little loots.");
  }

  private void mark_chest_large(Chest chest) {
	  chest.setMetadata("ivy.loot", new FixedMetadataValue(fawkes, "medium"));
    fawkes.log("Kinda big loots.");
  }

  private void mark_chest_murca(Chest chest) {
	  chest.setMetadata("ivy.loot", new FixedMetadataValue(fawkes, "murca"));
    fawkes.log("Fat ass fuckin' loots!");
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.getPlayer().hasPermission("fawkes.fks")) {
      if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
        if (event.getClickedBlock().getType().equals(Material.CHEST)) {
          if (event.getPlayer().getItemInHand() != null) {
            
            ItemStack held = event.getPlayer().getItemInHand();
            Chest chest = (Chest) event.getClickedBlock().getState();
            
            if (held.getType().equals(Material.STICK)) {

              String loot_type = held.getItemMeta().getLore().get(0);

              if (loot_type.equals("regular")) {
                mark_chest_regular(chest);
              }
              
              if (loot_type.equals("large")) {
                mark_chest_large(chest);
              }

              if (loot_type.equals("murca")) {
                mark_chest_murca(chest);
              }
              
              if (loot_type.equals("view")) {
            	  fawkes.log("Loot Tag: " + chest.getMetadata("ivy.loot"));
              }
            }
          }
        }
      }
    }
  }
}
