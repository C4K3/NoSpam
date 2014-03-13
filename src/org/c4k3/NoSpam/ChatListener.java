package org.c4k3.NoSpam;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
	
	public ChatListener(NoSpam plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
	
	@EventHandler(priority=EventPriority.LOW, ignoreCancelled=false) // Gets all chat events
	public void onPlayerChat(AsyncPlayerChatEvent event){
		
		Player player = event.getPlayer();
		
		boolean cancel = Handler.isSpamming(player); // The Handler class is the one that really checks if people are spamming
				
		if ( cancel ) event.setCancelled(true);
				
	}

}