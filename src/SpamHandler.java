package net.simpvp.NoSpam;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpamHandler {

	/* This hashmap is to include lists of messages by all players
	 * 
	 * Key is the player UUID, the value is an array of that player's chat times */
	private static HashMap<UUID, ArrayDeque<Long>> hashTimes = new HashMap<UUID, ArrayDeque<Long>>();

	/* This hashmap will hold all the muted players. <Player UUID, time they're unmuted as System.nanoTime / 1000 000> */
	private static HashMap<UUID, Long> mutedPlayers = new HashMap<UUID, Long>();

	/** This function should be called every time a player sends a chat message or a command identified as a chat message.
	 * It will check the message, and if the player is muted / should be muted then it will return true (and add them to the muted players list.)
	 * 
	 * If message should be cancelled, return true.
	 * If message should be allowed through, return false.*/
	static boolean isSpamming(Player player) {

		UUID uuid = player.getUniqueId();
		String sPlayer = player.getName();

		if ( isMuted(uuid) ) {
			NoSpam.instance.getLogger().info(sPlayer + " tried to talk but is muted.");
			return true;
		}

		long curtime = System.nanoTime() / 1000000;

		/* The counters for the amount of chat messages within respective timeframe (long, med or short) */
		int longPeriodCount = 0;
		int medPeriodCount = 0;
		int shortPeriodCount = 0;

		ArrayDeque<Long> arrayTimes = hashTimes.get(uuid);

		if ( arrayTimes == null ) arrayTimes = new ArrayDeque<Long>(); // If it doesn't already exist

		arrayTimes.add(curtime);

		while ( arrayTimes.getLast() - arrayTimes.getFirst() > 15000 ) arrayTimes.removeFirst(); // Remove all entries older than 15 seconds

		Iterator<Long> iter = arrayTimes.iterator();

		/* Iterate over the player's previous messages */
		while ( iter.hasNext()) {

			Long timeSinceMsg = curtime - iter.next();

			/* If the message was less than .4 seconds ago */
			if ( timeSinceMsg < 400 ) shortPeriodCount++;

			/* If the message was less than 1 second ago */
			if ( timeSinceMsg < 1000 ) medPeriodCount++;

			/* Since we already delete all entries over 15 seconds old, we can implicitly assume that it should increase the 15-sec counter */
			longPeriodCount++;

		}

		/* If the count in the respective timeframe is greater than entered, player is spamming */
		if ( shortPeriodCount > 2 || medPeriodCount > 3 || longPeriodCount > 7 ) {

			/* Remove the player's entry in hashTimes, as the player is now muted the chat timestamps are now entirely irrelevant */
			hashTimes.remove(uuid);

			/* Mute player for one minute */
			mutePlayer(uuid, 1);

			/* Tell console */
			NoSpam.instance.getLogger().info(sPlayer + " has been automuted for spam " + shortPeriodCount + " " + medPeriodCount + " " + longPeriodCount);

			/* Tell all admins that this player has been muted */
			for ( Player onlinePlayer : NoSpam.instance.getServer().getOnlinePlayers() ) {

				if ( onlinePlayer.isOp() ) onlinePlayer.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "[NoSpam: Automuting " + sPlayer + " for 1 minute]");

			}

			return true;

		} else {
			/* Player is not spamming with this message, but we still register their messages */
			hashTimes.put(uuid, arrayTimes);

			return false;

		}

	}

	/** Mutes player for seconds */
	public static void mutePlayer(UUID uuid, int minutes) {

		long curtime = System.nanoTime() / 1000000;

		long expireTime;

		/* If a mutedFor value of 0 is passed, it means the player is to be
		 * muted forever (until next server restart */
		if ( minutes == 0 ) expireTime = 0;
		else expireTime = curtime + minutes * 60 * 1000;

		mutedPlayers.put(uuid, expireTime);

	}

	/** Unmutes player.
	 * 
	 *  Returns true if player was unmuted. 
	 *  Returns false if players was not unmuted (meaning player was not muted to begin with.) */
	public static boolean unmutePlayer(UUID uuid) {
		if ( mutedPlayers.get(uuid) == null ) return false;
		else {
			mutedPlayers.remove(uuid);
			return true;
		}

	}

	/** Checks whether player is muted.
	 * 
	 * Returns true if player is muted.
	 * Returns false is player is not muted. */
	public static boolean isMuted(UUID uuid) {

		/* If the player is not in the mutedPlayers hashmap at all, then we
		 * can safely assume that they're not muted */
		if ( mutedPlayers.get(uuid) == null ) return false;

		long curtime = System.nanoTime() / 1000000;

		/* If the time the mute expires is bigger than the current time ( == in the future)
		 *  or if it's 0 (mute forever) then return true (cancel message) */
		if ( mutedPlayers.get(uuid) > curtime || mutedPlayers.get(uuid) == 0 ) return true;
		/* else their mute has expired and we can unmute them and return false by default */
		else unmutePlayer(uuid);

		return false;

	}

}

