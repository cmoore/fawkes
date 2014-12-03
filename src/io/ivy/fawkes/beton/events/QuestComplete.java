
package io.ivy.fawkes.beton.events;

import pl.betoncraft.betonquest.core.QuestEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class QuestComplete extends QuestEvent implements Listener {
    public QuestComplete(String player_id, String instructions) {
        super(player_id, instructions);
        Bukkit.getLogger().info("Add X experience.");
    }
}

