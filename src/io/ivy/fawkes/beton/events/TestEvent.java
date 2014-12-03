
package io.ivy.fawkes.beton.events;

import pl.betoncraft.betonquest.core.QuestEvent;

import org.bukkit.event.Listener;

public class TestEvent extends QuestEvent implements Listener {
    public TestEvent(String playerID, String instructions) {
        super(playerID, instructions);
        System.out.println("This is a test.");
    }
}
