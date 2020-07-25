package net.simpvp.NoSpam;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/** Class is responsible for listening to player deaths
 * and counting deaths that still have a death message
 * as the same as a chat message. */
public class PlayerDeath implements Listener {

	public PlayerDeath(NoSpam plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		String msg = event.getDeathMessage();
		if (msg == null || msg.isEmpty()) {
			return;
		}

		Player player = event.getEntity();

		boolean cancel = SpamHandler.isSpamming(player);
		if (cancel) {
			NoSpam.instance.getLogger().info(event.getDeathMessage());
			event.setDeathMessage(null);
		}
	}

}

