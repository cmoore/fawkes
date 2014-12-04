
package io.ivy.fawkes;

import java.lang.Iterable;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.BlockState;


public class Utils {
    public static List<Chest> find_all_chests(World world) {
        List<Chest> chests = new ArrayList();
        int tile_entities = 0;
        Chunk[] chunks = world.getLoadedChunks();

        Bukkit.getLogger().info("Found " + String.valueOf(chunks.length) + " chunks.");
        
        for (Chunk c : chunks) {
            
            for (BlockState b : c.getTileEntities()) {
                tile_entities++;

                // Bukkit.getLogger().info( String.join(" ",(String) b.getWorld().getName(),
                //                                      Integer.toString(b.getX()),
                //                                      Integer.toString(b.getY()),
                //                                      Integer.toString(b.getZ()),
                //                                      Integer.toString(b.getBlock().getTypeId()),
                //                                      Byte.toString(b.getBlock().getData()),
                //                                      b.getData().getItemType().name()));
                
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
}
