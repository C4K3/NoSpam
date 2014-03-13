package org.c4k3.NoSpam;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.entity.Player;

public class Handler {
	
	/* This hashmap is to include lists of messages by all players
	 * 
	 * Key is the player name, the value is an array of that player's chat times */
	static HashMap<String, ArrayDeque<Long>> hashTimes = new HashMap<String, ArrayDeque<Long>>();
	
	/* This hashmap will hold all the muted players. k = playername, v = time until unmute */
	static HashMap<String, Long> mutedPlayers = new HashMap<String, Long>();
	
	static boolean isSpamming(Player player) {
		/** Called every time player sends a message. If these are too frequent, mute the player */
		
		if ( player.isOp() == true ) return false;
				
		// Set the counters for the amount of chat messages within respective timeframe (long, med or short) to 0
		int longPeriodCount = 0;
		int medPeriodCount = 0;
		int shortPeriodCount = 0;
		
		String splayer = player.getName();
		
		long curtime = System.nanoTime() / 1000000;
		
		if ( mutedPlayers.get(splayer) != null ) {
			
			/* If the time the mute expires is bigger than the current time ( == in the future) then return true (cancel message) */
			if ( mutedPlayers.get(splayer) > curtime ) {
				NoSpam.instance.getLogger().info(splayer + " tried to talk but is muted");
				return true;
			}
			
			/* Else (mute is over 60 seconds old) remove player from the mutedPlayers HashMap */
			else mutedPlayers.remove(splayer);
		}
		
		ArrayDeque<Long> arrayTimes = hashTimes.get(splayer);
		
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
			
			/* Since we delete all entries over 15 seconds old, we can implicitly assume that it should increase the 15-sec counter */
			longPeriodCount++;
			
		}
				
		/* If the count in the respective timeframe is greater than entered, player is spamming */
		if ( shortPeriodCount > 1 || medPeriodCount > 2 || longPeriodCount > 6 ) {
			
			/* Remove the player's entry in hashTimes, as the player is now muted the chat timestamps are now entirely irrelevant */
			hashTimes.remove(splayer);
			
			long expireTime = curtime + 60000; // Unmute is 60 seconds in future
			
			mutedPlayers.put(splayer, expireTime);
			
			NoSpam.instance.getLogger().info(splayer + " has been muted for spam " + shortPeriodCount + " " + medPeriodCount + " " + longPeriodCount);
			
			return true;
			
		} else {
			/* Player is not spamming with this message, but we still register their messages */
			hashTimes.put(splayer, arrayTimes);
			
			return false;
			
		}
	}

}
