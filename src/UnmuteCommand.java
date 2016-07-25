package net.simpvp.NoSpam;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnmuteCommand implements CommandExecutor {

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

		/* There should always be just one argument */
		if ( args.length != 1 ) {
			if ( player == null ) NoSpam.instance.getLogger().info("Incorrect amount of arguments.");
			else player.sendMessage(ChatColor.RED + "Incorrect amount of arguments.");
			return true;
		}

		@SuppressWarnings("deprecation") // We don't store the player or name, we store the UUID (about 10 lines down)
		Player tPlayer = NoSpam.instance.getServer().getPlayer(args[0]);

		if ( tPlayer == null ) {
			String message = "No such online player.";
			if ( player == null ) NoSpam.instance.getLogger().info(message);
			else player.sendMessage(ChatColor.RED + message);
			return true;
		}

		UUID uuid = tPlayer.getUniqueId();

		/* If the player could be unmuted */
		if ( SpamHandler.unmutePlayer(uuid) ) {
			NoSpam.instance.getLogger().info(tPlayer.getName() + " was unmuted by " + sender.getName() + ".");
			if ( player != null )player.sendMessage(ChatColor.AQUA + tPlayer.getName() + " was unmuted.");

			for ( Player onlinePlayer : NoSpam.instance.getServer().getOnlinePlayers() ) {

				if ( onlinePlayer.isOp() && onlinePlayer != player ) onlinePlayer.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "[" + sender.getName() + ": Unmuted " + tPlayer.getName() + "]");

			}

		} else {
			/* If the player could not be unmuted (== they were not muted to begin with) */
			if ( player == null ) NoSpam.instance.getLogger().info("Unable to unmute \"" + tPlayer.getName() + "\", no such muted player.");
			else player.sendMessage(ChatColor.RED + "Unable to unmute \"" + tPlayer.getName() + "\", no such muted player.");
		}

		return true;

	}

}

