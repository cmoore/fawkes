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
import org.bukkit.projectiles.ProjectileSource;

public class Events implements Listener {

    private final Fawkes fawkes;

    public Events(Fawkes instance) {
        fawkes = instance;
    }

    public static int mob_level() {
       return random_chance(1,20);
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
            fawkes.getLogger().info("BZZZZZT");
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
        
        fawkes.getLogger().info(damager + "(" + find_mob_level(damager) + ") HIT " +
                                target + "(" + find_mob_level(target) + ") for " +
                                pre_damage + "/" + end_damage + " -> " + new_damage_level);
        event.setDamage(new_damage_level);
    }

    // private int damage_bonus(int level_attacker, int level_target, int base_hit) {
        
        
    // }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            
            Entity entity = event.getEntity();
            Player player = event.getEntity().getKiller();

            int player_level = player.getLevel();
            int mob_level = find_mob_level(entity);

            double experience_to_add = (mob_level - player_level) * .5;
            int xp_to_add = Integer.valueOf((int) Math.round(experience_to_add));
            
            if (experience_to_add < 0)
                experience_to_add = 0;
            
            fawkes.getLogger().info(player.getName() + "(" + player_level + ") killed " + entity.getType().toString() + "(" + mob_level + ") for " + experience_to_add + " / " + xp_to_add + " xp.");

            if (entity.hasMetadata("ivy.level")) {
                player.giveExp(Integer.valueOf((int) Math.round(experience_to_add)));
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

    // @EventHandler
    // private void onChunkLoad(ChunkLoadEvent event){
    //     Chunk c = event.getChunk();
    //     for(BlockState b:c.getTileEntities()) {
    //         Bukkit.broadcastMessage(String.join(" ", (String) b.getWorld().getName(), Integer.toString(b.getX()), Integer.toString(b.getY()), Integer.toString(b.getZ()), Integer.toString(b.getBlock().getTypeId()), Byte.toString(b.getBlock().getData()), b.getData().getItemType().name()));
    //     }
    // }
}
