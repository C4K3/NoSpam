package net.simpvp.NoSpam;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AmIMutedCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player player = null;
		if (sender instanceof Player){
			player = (Player) sender;
		}

		/* Console can't be muted */
		if ( player == null ) {
			NoSpam.instance.getLogger().info("You can't be muted, silly!");
			return true;
		}

		UUID uuid = player.getUniqueId();

		if ( SpamHandler.isMuted(uuid) ) sender.sendMessage("Yes.");
		else sender.sendMessage("No.");

		return true;

	}

}

