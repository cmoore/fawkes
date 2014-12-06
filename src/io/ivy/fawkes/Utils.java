// -*- Mode: jde; eval: (hs-hide-level 2) -*-

package io.ivy.fawkes;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.*;
import org.bukkit.block.*;

import redis.clients.jedis.*;


public class Utils {
	public static int mob_level() {
    return random_chance(1,20);
  }
  
  public static int random_chance(int min, int max) {
    Random rand = new Random();
    return rand.nextInt((max - min) + 1) + min;
  }
  
  public static List<Chest> find_all_chests(World world) {
    List<Chest> chests = new ArrayList<Chest>();
    int tile_entities = 0;
    Chunk[] chunks = world.getLoadedChunks();
    
    Bukkit.getLogger().info("Found " + String.valueOf(chunks.length) + " chunks.");
    
    for (Chunk c : chunks) {
      for (BlockState b : c.getTileEntities()) {
        tile_entities++;
        
        Bukkit.getLogger().info( String.join(" ",(String) b.getWorld().getName(),
                                             Integer.toString(b.getX()),
                                             Integer.toString(b.getY()),
                                             Integer.toString(b.getZ()),
                                             Integer.toString(b.getBlock().getTypeId()),
                                             Byte.toString(b.getBlock().getData()),
                                             b.getData().getItemType().name()));
        
        Bukkit.getLogger().info("U WOT M8: " + b.toString());
        if (b instanceof Furnace) {
          Bukkit.getLogger().info("DING DING DING");
        }
        
        if (b.getType().equals(Material.CHEST)) {
          Bukkit.getLogger().info("Looky there.");
          chests.add((Chest) b);
        }
      }
    }
    
    Bukkit.getLogger().info("Found " + String.valueOf(tile_entities) + " TileEntities.");
    return chests;
  }

  public static Jedis open_database() {
    return new Jedis("192.168.0.210");
  }
}
