// -*- Mode: jde; eval: (hs-hide-level 2) -*-

package io.ivy.fawkes.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.*;
import org.bukkit.block.Chest;
import org.bukkit.command.*;

import redis.clients.jedis.*;
import io.ivy.fawkes.Fawkes;
import io.ivy.fawkes.LootTables;
import io.ivy.fawkes.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Fks implements CommandExecutor {

  private final Fawkes fawkes;

  public Fks(Fawkes instance) {
    fawkes = instance;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if (!(sender instanceof Player)) {
      sender.sendMessage("Must be used as a player.");
      return true;
    }

    if (sender.hasPermission("fawkes.fks")) {
      
      String sub_command = args[0];

      if (sub_command.equals("verbose")) {
        
        if (fawkes.verbose == true) {
          fawkes.verbose = false;
          sender.sendMessage("Info messages disabled.");
          
          return true;
        } else {
          fawkes.verbose = true;
          sender.sendMessage("Info messages enabled.");
          
          return true;
        }
      }

      if (sub_command.equals("range")) {
        
        if (args.length < 3) {
          return false;
        }

        String min_range = args[1];
        String max_range = args[2];

        String region = fawkes.region_for_entity((Entity)sender);

        if (region != null) {
          fawkes.log("range called with: " + min_range + " " + max_range + " in " + region);

          Jedis j = fawkes.pool.getResource();
          j.set("fawkes.regions." + region + ".min", min_range);
          j.set("fawkes.regions." + region + ".max", max_range);
          fawkes.log("Region levels set!");
          j.close();
          sender.sendMessage("Region level range set.");
          return true;
        } else {
          sender.sendMessage("You aren't in a region!");
          return false;
        }
      }

      if (sub_command.equals("reloot")) {
    	  Player player = (Player) sender;
    	  List<Chest> chests = Utils.find_all_chests(player.getWorld());
    	  
    	  for (Chest chest : chests) {
    		  
			  Inventory inventory = chest.getInventory();
    		  int i = Utils.random_chance(2, 8);
    		  
    		  for (int x = 0; x <= i; x++) {
    			  ItemStack istack = new ItemStack(LootTables.get_random_material(), 1);
    			  
    			  inventory.addItem(istack);
    		  }
    	  }
      }
      if (sub_command.equals("sticks")) {
        
        Player player = (Player) sender;

        ItemStack regular_stick = new ItemStack(Material.STICK, 1);
        ArrayList<String> regular_lore = new ArrayList<String>();
        ItemMeta regular_meta = regular_stick.getItemMeta();
        
        regular_lore.add("regular");
        regular_meta.setLore(regular_lore);
        regular_meta.setDisplayName("Stick of Loots.");
        regular_stick.setItemMeta(regular_meta);
        
        ItemStack high_stick = new ItemStack(Material.STICK, 1);
        ArrayList<String> high_lore = new ArrayList<String>();
        ItemMeta high_meta = high_stick.getItemMeta();
        
        high_lore.add("large");
        high_meta.setLore(high_lore);
        high_meta.setDisplayName("Stick of Midrange loots.");
        high_stick.setItemMeta(high_meta);
        
        ItemStack boss_stick = new ItemStack(Material.STICK, 1);
        ArrayList<String> boss_lore = new ArrayList<String>();
        ItemMeta boss_meta = boss_stick.getItemMeta();

        boss_lore.add("murca");
        boss_meta.setLore(boss_lore);
        boss_meta.setDisplayName("Stick of fat-ass loots.");
        boss_stick.setItemMeta(boss_meta);
        
        ItemStack view_stick = new ItemStack(Material.STICK, 1);
        ArrayList<String> view_lore = new ArrayList<String>();
        ItemMeta view_meta = view_stick.getItemMeta();
        
        view_lore.add("view");
        view_meta.setLore(view_lore);
        view_meta.setDisplayName("Stick of Viewing");
        view_stick.setItemMeta(view_meta);
        
        
        player.getInventory().addItem(regular_stick);
        player.getInventory().addItem(high_stick);
        player.getInventory().addItem(boss_stick);
        player.getInventory().addItem(view_stick);
        
        return true;
      }
    }
    return false;
  }
}
