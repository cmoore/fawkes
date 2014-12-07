// -*- Mode: jde; eval: (hs-hide-level 2) -*-

package io.ivy.fawkes.cmd;

import org.bukkit.entity.*;
import org.bukkit.command.*;

import redis.clients.jedis.*;

import io.ivy.fawkes.Fawkes;
import io.ivy.fawkes.Utils;

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
    }
    return false;
  }
}
