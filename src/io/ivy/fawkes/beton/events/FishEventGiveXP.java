
package io.ivy.fawkes.beton.events;

import pl.betoncraft.betonquest.core.QuestEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;

public class FishEventGiveXP extends QuestEvent implements Listener {
  public FishEventGiveXP(String player_id, String instructions) {
    super(player_id, instructions);
    Bukkit.getLogger().info("Add some xp for " + player_id);
    Player the_player = Bukkit.getServer().getPlayer(player_id);
    the_player.giveExp(5);
    the_player.sendMessage("You received 5 experience!");
  }
}
