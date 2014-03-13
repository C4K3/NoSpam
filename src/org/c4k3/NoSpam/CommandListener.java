package org.c4k3.NoSpam;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {
	
	public CommandListener(NoSpam plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
	
	@EventHandler(priority=EventPriority.LOW, ignoreCancelled=false)
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		
		Player player = event.getPlayer();
		
		String message = event.getMessage();

		/* Important: The spaces afterwards are necessary. Otherwise we get something like this being activated on /world because of /w */
		if ( message.startsWith("/tell ") || message.startsWith("/me ") || message.startsWith("/msg ") || message.startsWith("/w ") ) {
			
			boolean cancel = Handler.isSpamming(player); // The Handler class checks if it's spam
			
			if ( cancel ) event.setCancelled(true);
			
		}
		
	}
	
}
