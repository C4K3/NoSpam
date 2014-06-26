package org.c4k3.NoSpam;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand implements CommandExecutor {
	
	String message;
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player player = null;
		if (sender instanceof Player){
			player = (Player) sender;
		}
		
		/* Only console and OPs are allowed to use this command */
		if ( player != null && !player.isOp() ) {
			player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
			return true;
		}
		
		/* This command should always have either 1 or 2 arguments (player + length of mute) */
		if ( args.length == 0 || args.length > 2 ) {
			if ( player == null ) NoSpam.instance.getLogger().info("Incorrect amount of arguments.");
			else player.sendMessage(ChatColor.RED + "Incorrect amount of arguments.");
			return true;
		}
		
		String tPlayer;
		
		try {
			tPlayer = NoSpam.instance.getServer().getPlayer(args[0]).getName();
		} catch(NullPointerException e) {
			/* This will happen if bukkit is not able to get player, but then we just assume
			 * that player was an exactly named offline player */
			tPlayer = args[0];
		}
		
		/* By default we assume muteMinnutes is 0 (forever) */
		int muteMinutes = 0;
		
		/* If a second argument was specified, that is the amount of minutes to mute for. */
		if ( args.length > 1 ) {
			muteMinutes = Integer.valueOf(args[1]);
		}
		
		SpamHandler.mutePlayer(tPlayer, muteMinutes);
		
		NoSpam.instance.getLogger().info(tPlayer + " was muted for " + muteMinutes + " minutes by " + sender.getName() + ".");
		if ( player != null ) player.sendMessage(ChatColor.AQUA + tPlayer + " was muted for " + muteMinutes + " minutes.");
				
		if ( muteMinutes == 1 ) message = ChatColor.GRAY + "" + ChatColor.ITALIC + "[" + sender.getName() + ": Muted " + tPlayer + " for " + muteMinutes + " minute]";
		else message = ChatColor.GRAY + "" + ChatColor.ITALIC + "[" + sender.getName() + ": Muted " + tPlayer + " for " + muteMinutes + " minutes]";
		
		for ( Player onlinePlayer : NoSpam.instance.getServer().getOnlinePlayers() ) {
			
			if ( onlinePlayer.isOp() && onlinePlayer != player ) onlinePlayer.sendMessage(message);
			
		}
		
		return true;
		
	}

}
