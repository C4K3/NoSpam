package org.c4k3.NoSpam;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpamHandler {

	/* This hashmap is to include lists of messages by all players
	 * 
	 * Key is the player name, the value is an array of that player's chat times */
	static HashMap<String, ArrayDeque<Long>> hashTimes = new HashMap<String, ArrayDeque<Long>>();

	/* This hashmap will hold all the muted players. <Player name, time they're unmuted as System.nanoTime / 1000 000> */
	static HashMap<String, Long> mutedPlayers = new HashMap<String, Long>();

	/** This function should be called every time a player sends a chat message or a command identified as a chat message.
	 * It will check the message, and if the player is muted / should be muted then it will return true (and add them to the muted players list.)
	 * 
	 * If message should be cancelled, return true.
	 * If message should be allowed through, return false.*/
	static boolean isSpamming(Player player) {

		if ( player.isOp() ) return false; // OPs can spam all they want

		String sPlayer = player.getName();

		if ( isMuted(sPlayer) ) {
			NoSpam.instance.getLogger().info(sPlayer + " tried to talk but is muted.");
			return true;
		}

		long curtime = System.nanoTime() / 1000000;
		
		/* The counters for the amount of chat messages within respective timeframe (long, med or short) */
		int longPeriodCount = 0;
		int medPeriodCount = 0;
		int shortPeriodCount = 0;
	
		ArrayDeque<Long> arrayTimes = hashTimes.get(sPlayer);

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
		if ( shortPeriodCount > 1 || medPeriodCount > 2 || longPeriodCount > 6 ) {

			/* Remove the player's entry in hashTimes, as the player is now muted the chat timestamps are now entirely irrelevant */
			hashTimes.remove(sPlayer);
			
			/* Mute player for one minute */
			mutePlayer(sPlayer, 1);

			/* Tell console */
			NoSpam.instance.getLogger().info(sPlayer + " has been muted for spam " + shortPeriodCount + " " + medPeriodCount + " " + longPeriodCount);

			/* Tell all admins that this player has been muted */
			for ( Player tPlayer : NoSpam.instance.getServer().getOnlinePlayers() ) {

				if ( tPlayer.isOp() ) tPlayer.sendMessage("[" + ChatColor.GOLD + "NoSpam" + ChatColor.RESET + "] Automuting " + sPlayer + " for 60 seconds.");

			}

			return true;

		} else {
			/* Player is not spamming with this message, but we still register their messages */
			hashTimes.put(sPlayer, arrayTimes);

			return false;

		}

	}
	
	/** Mutes player for seconds */
	public static void mutePlayer(String player, int minutes) {
		
		long curtime = System.nanoTime() / 1000000;
		
		long expireTime;
		
		/* If a mutedFor value of 0 is passed, it means the player is to be
		 * muted forever (until next server restart */
		if ( minutes == 0 ) expireTime = 0;
		else expireTime = curtime + minutes * 60 * 1000;
		
		mutedPlayers.put(player, expireTime);
		
	}
	
	/** Unmutes player.
	 * 
	 *  Returns true if player was unmuted. 
	 *  Returns false if players was not unmuted (meaning player was not muted to begin with.) */
	public static boolean unmutePlayer(String player) {
		if ( mutedPlayers.get(player) == null ) return false;
		else {
			mutedPlayers.remove(player);
			return true;
		}
		
	}
	
	/** Checks whether player is muted.
	 * 
	 * Returns true if player is muted.
	 * Returns false is player is not muted. */
	public static boolean isMuted(String player) {
		
		/* If the player is not in the mutedPlayers hashmap at all, then we
		 * can safely assume that they're not muted */
		if ( mutedPlayers.get(player) == null ) return false;
		
		long curtime = System.nanoTime() / 1000000;
		
		/* If the time the mute expires is bigger than the current time ( == in the future)
		 *  or if it's 0 (mute forever) then return true (cancel message) */
		if ( mutedPlayers.get(player) > curtime || mutedPlayers.get(player) == 0 ) return true;
		/* else their mute has expired and we can unmute them and return false by default */
		else unmutePlayer(player);
		
		return false;
		
	}

}
