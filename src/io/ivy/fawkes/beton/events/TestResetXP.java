
package io.ivy.fawkes.beton.events;

import pl.betoncraft.betonquest.core.QuestEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;

public class TestResetXP extends QuestEvent implements Listener {
  public TestResetXP(String player_id, String instructions) {
    super(player_id, instructions);
    Player player = Bukkit.getServer().getPlayer(player_id);
    
    player.setLevel(1);
  }
}

      
