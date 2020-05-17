package net.simpvp.NoSpam;

import java.util.UUID;

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
		if (player != null && !player.isOp()) {
			player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
			return true;
		}

		/* This command should always have either 1 or 2 arguments (player + length of mute) */
		if (args.length == 0 || args.length > 3) {
			String message = "Incorrect number of arguments.";
			if ( player == null ) NoSpam.instance.getLogger().info(message);
			else player.sendMessage(ChatColor.RED + message);
			return true;
		}
		boolean announce = false;
		for (String arg : args) {
			if (arg.equalsIgnoreCase("-a")) {
				announce = true;
			}
		}

		@SuppressWarnings("deprecation") // We don't store the player or name, we store the UUID (about 10 lines down)
		Player tPlayer = NoSpam.instance.getServer().getPlayer(args[0]);

		if (tPlayer == null) {
			String message = "No such online player.";
			if ( player == null ) NoSpam.instance.getLogger().info(message);
			else player.sendMessage(ChatColor.RED + message);
			return true;
		}

		UUID uuid = tPlayer.getUniqueId();

		/* By default we assume muteMinutes is 0 (forever) */
		int muteMinutes = 0;

		/* If a second argument was specified, that is the amount of minutes to mute for. */
		if (args.length > 1) {
			muteMinutes = Integer.valueOf(args[1]);
		}

		SpamHandler.mutePlayer(uuid, muteMinutes);

		String minutes;
		if (muteMinutes == 1) {
			minutes = "minute";
		} else {
			minutes = "minutes";
		}

		NoSpam.instance.getLogger().info(tPlayer.getName() + " was muted for " + muteMinutes + " " + minutes + " by " + sender.getName() + ".");
		if (player != null) player.sendMessage(ChatColor.AQUA + tPlayer.getName() + " was muted for " + muteMinutes + " " + minutes + ".");
		if (announce) {
			tPlayer.sendMessage(ChatColor.RED + "You have been muted for " + muteMinutes + " " + minutes + ".");
		}

		message = ChatColor.GRAY + "" + ChatColor.ITALIC + "[" + sender.getName() + ": Muted " + tPlayer.getName() + " for " + muteMinutes + " " + minutes + "]";
		for (Player onlinePlayer : NoSpam.instance.getServer().getOnlinePlayers()) {
			if (onlinePlayer.isOp() && onlinePlayer != player) {
				onlinePlayer.sendMessage(message);
			}
		}

		return true;

	}

}

