
package io.ivy.fawkes.cmd;

import org.bukkit.entity.*;
import org.bukkit.command.*;

import io.ivy.fawkes.Fawkes;

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

    sender.sendMessage("I see you.");
    
    if (sender.hasPermission("fawkes.fks")) {
      String name = command.getName();
      
      if (name.equalsIgnoreCase("fks")) {
        if (args[0] != null && args[1] != null) {
          sender.sendMessage("FARTTART: Called with " + args[0] + " and " + args[1]);
        }
      }
    }
    return true;
  }
}    
