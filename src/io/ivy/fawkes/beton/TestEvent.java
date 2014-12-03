
package io.ivy.fawkes.beton;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import pl.betoncraft.betonquest.core.QuestEvent;

public class TestEvent extends QuestEvent implements Listener {
    public TestEvent(String playerID, String instructions) {
        super(playerID, instructions);
        getLogger().info("TEST EVENT: " + instructions);
    }
}
