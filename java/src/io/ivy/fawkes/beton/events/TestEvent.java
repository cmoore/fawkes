
package io.ivy.fawkes.beton.events;

import pl.betoncraft.betonquest.core.QuestEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class TestEvent extends QuestEvent implements Listener {
    public TestEvent(String playerID, String instructions) {
        super(playerID, instructions);
        Bukkit.getLogger().info("This is a test.");
    }
}
