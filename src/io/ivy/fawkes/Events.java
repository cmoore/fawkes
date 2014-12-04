// -*- Mode: jde; eval: (hs-hide-level 2) -*-

package io.ivy.fawkes;

import java.util.Random;
import java.util.List;


import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.metadata.*;

import org.bukkit.entity.*;

import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.world.*;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.ivy.fawkes.Utils;

public class Events implements Listener {

    private final Fawkes fawkes;

    public Events(Fawkes instance) {
        fawkes = instance;
    }

    public static int random_chance(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
    
    public static int mob_level() {
       return random_chance(1,9);
    }

    private int find_mob_level(Entity entity) {
        if (entity.getType().equals(EntityType.PLAYER)) {
            return ((Player)entity).getLevel();
        }
        
        List<MetadataValue> level_values = entity.getMetadata("ivy.level");
        
        if (level_values.isEmpty()) {
            return 1;
        } else {
            return level_values.get(0).asInt();
        }
    }
    
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {

        if (event.getCreatureType() != null) {
            Entity entity = event.getEntity();
            
            if (event.getCreatureType().equals(CreatureType.ENDERMAN) ||
                event.getCreatureType().equals(CreatureType.CREEPER) ||
                event.getCreatureType().equals(CreatureType.SPIDER)) {
                event.setCancelled(true);
            }
            
            if (event.getCreatureType().equals(CreatureType.ZOMBIE)) {
                
                if (random_chance(1,1000) > 999) {
                    entity.setCustomName("§cCrazel");
                    entity.setCustomNameVisible(true);
                    entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, "10"));
                } else {
                    entity.setCustomName("§9Skeleton");
                    entity.setCustomNameVisible(true);
                    entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, String.valueOf(mob_level())));
                }
            }
            
            if (event.getCreatureType().equals(CreatureType.SKELETON)) {
                if (random_chance(1,1000) > 999) {
                    entity.setCustomName("§cMr. Skellington");
                    entity.setCustomNameVisible(true);
                    entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, "10"));
                } else {
                    entity.setCustomName("§9Skeleton");
                    entity.setCustomNameVisible(true);
                    entity.setMetadata("ivy.level", new FixedMetadataValue(fawkes, String.valueOf(mob_level())));
                }
            }
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        
        if (event.getDamager().getType().equals(EntityType.PLAYER) ||
            event.getEntity().getType().equals(EntityType.PLAYER)) {

            
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
            
            float damage_adjust = 1;
                            
            fawkes.getLogger().info(damager + "(" + String.valueOf(find_mob_level(damager)) + ") HIT " +
                                    target + "(" + String.valueOf(find_mob_level(target)) + ") for " +
                                    String.valueOf(pre_damage) + "/" + String.valueOf(end_damage) + " mod: " +
                                    String.valueOf(new_damage_level));
        }
    }

    // private int damage_bonus(int level_attacker, int level_target, int base_hit) {
        
        
    // }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        
        if (event.getEntity().getKiller() != null) {
            
            Entity entity = event.getEntity();
            Player player = event.getEntity().getKiller();
            
            fawkes.getLogger().info(player.getName() + " killed a " + entity.getType().toString() + ".");
            if (entity.hasMetadata("ivy.level")) {
                player.giveExp(1);
            }
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
    			fawkes.getLogger().info("Food level: " + String.valueOf(food_level));
    		}
    	}
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getClickedBlock().getState().getType().equals(Material.SIGN_POST)) {
                fawkes.getLogger().info("You wot?");
                Sign the_sign = (Sign) event.getClickedBlock().getState();
                if (the_sign.getLine(1).equals("chests")) {
                    List<Chest> chests = Utils.find_all_chests(event.getPlayer().getWorld());
                    fawkes.getLogger().info("I found " + String.valueOf(chests.size()) + " chests.");
                }
            }
        }
    }

    @EventHandler
    private void onChunkLoad(ChunkLoadEvent event){
        Chunk c = event.getChunk();
        for(BlockState b:c.getTileEntities()) {
            Bukkit.broadcastMessage(String.join(" ", (String) b.getWorld().getName(), Integer.toString(b.getX()), Integer.toString(b.getY()), Integer.toString(b.getZ()), Integer.toString(b.getBlock().getTypeId()), Byte.toString(b.getBlock().getData()), b.getData().getItemType().name()));
        }
    }


}
