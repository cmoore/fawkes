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
    	if (event.getDamager().getType().equals(EntityType.PLAYER)) {
    		
    	}
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
    }
    
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
    	if (event.getEntityType().equals(EntityType.PLAYER)) {
    		if (event.getFoodLevel() < 10) {
    			Bukkit.getLogger().info("Adjusted food level.");
    			Player player = (Player) event.getEntity();
    			player.setFoodLevel(event.getFoodLevel() + 1);
    		}
    	}
    }
}
