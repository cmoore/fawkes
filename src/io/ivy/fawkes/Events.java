// -*- Mode: jde; eval: (hs-hide-level 2) -*-

package io.ivy.fawkes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

public class Events implements Listener {

    private final Fawkes fawkes;

    public Events(Fawkes instance) {
        fawkes = instance;
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        String damager_name;
        String damagee_name;

        if (event.getDamager().getType().equals(EntityType.PLAYER) ||
            event.getEntity().getType().equals(EntityType.PLAYER)) {

            Double pre_damage = Double.valueOf(event.getDamage());
            Double end_damage = Double.valueOf(event.getFinalDamage());

            String damager = event.getDamager().getType().toString();
            String damagee = event.getEntity().getType().toString();
            
            Bukkit.getLogger().info(damager + " HIT " + damagee + " for " + String.valueOf(pre_damage) + "/" + String.valueOf(end_damage));
        }
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Entity entity = event.getEntity();
            Player player = entity.getKiller();
            Bukkit.getLogger().info(player.getName() + " killed a " + entity.getType().toString + ".");
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
          int new_food_level = food_level + 2;
          
    			player.setFoodLevel(event.getFoodLevel() + 5);
    			Bukkit.getLogger().info("Adjusted food level from " + String.valueOf(food_level) + " to " + String.valueOf(new_food_level));
          
    		}
    	}
    }
}
