package org.c4k3.NoSpam;

import org.bukkit.plugin.java.JavaPlugin;

public class NoSpam extends JavaPlugin {

	public static JavaPlugin instance;
	
	@Override
	public void onEnable(){
		instance = this;
		new ChatListener(this);
		new CommandListener(this);
	}
	
	@Override
	public void onDisable(){
		//onDisable
		
	}
	
}