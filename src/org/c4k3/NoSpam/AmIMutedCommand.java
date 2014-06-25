package org.c4k3.NoSpam;

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
		
		String sPlayer = player.getName();
		
		if ( SpamHandler.isMuted(sPlayer) ) sender.sendMessage("You are muted.");
		else sender.sendMessage("You are not muted.");
		
		return true;
		
	}

}
