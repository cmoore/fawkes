
package io.ivy.fawkes.cmd;

import java.util.ArrayList;

import org.bukkit.entity.*;
import org.bukkit.command.*;

import io.ivy.fawkes.Fawkes;

import redis.clients.jedis.*;


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
      }        
      
    }
    return false;
  }
}    
