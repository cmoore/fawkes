// -*- Mode: jde; eval: (hs-hide-level 2) -*-

package io.ivy.fawkes;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;

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
    Chunk[] chunks = world.getLoadedChunks();
    
    for (Chunk c : chunks) {
      for (BlockState b : c.getTileEntities()) {
        if (b.getType().equals(Material.CHEST)) {
          chests.add((Chest) b);
        }
      }
    }
    return chests;
  }
}
