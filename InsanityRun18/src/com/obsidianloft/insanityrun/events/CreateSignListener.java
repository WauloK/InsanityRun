/* Insanity Run by Jason Oakley aka WauloK -  a fun "Temple Run" type game for Minecraft Bukkit & Spigot
    Copyright (C) 2014  Jason Oakley

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.obsidianloft.insanityrun.events;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;

import com.obsidianloft.insanityrun.InsanityRun;

public class CreateSignListener implements Listener {
	@EventHandler
	public void sign(SignChangeEvent event) {
		String gameName = InsanityRun.gameName;
		String line1 = event.getLine(0);
		String line2 = event.getLine(1);
		String line3 = event.getLine(2);
		Player player = event.getPlayer();
		
		// InsanityRun JOIN sign
		if (line1.equalsIgnoreCase("[insanityrun]") || line1.equalsIgnoreCase("[irun]")) {
			if (line2.equalsIgnoreCase("join")) {
				if (line3.equalsIgnoreCase("")) { 
					player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noSignArenaName"));
					return;
							}
				// Player does not have permission to place sign
				if (!player.hasPermission("insanityrun.sign")) {
					player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noSignPerms"));
					return;
				}
				// Does the arena in fact exist?
				if (InsanityRun.plugin.getConfig().getString(line3 + ".world") == null) {
					player.sendMessage(ChatColor.RED + line3 + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noArena"));
					return;
				}				
				// Update the colours and state of the sign to look official
				String arenaName = line3;
				event.setLine(0, ChatColor.BLUE + "[" + gameName + "]");
				event.setLine(1, ChatColor.YELLOW + "Join Game");
				event.setLine(2, ChatColor.GREEN + line3);
				event.setLine(3, ChatColor.AQUA + "Playing: 0");
				
				// Save sign location to config.yml
				Location loc = event.getBlock().getLocation();
				InsanityRun.plugin.getConfig().set(arenaName + ".sign.x", loc.getX());
				InsanityRun.plugin.getConfig().set(arenaName + ".sign.y", loc.getY());
				InsanityRun.plugin.getConfig().set(arenaName + ".sign.z", loc.getZ());
				InsanityRun.plugin.getConfig().set(arenaName + ".sign.world", loc.getWorld().getName());
				InsanityRun.plugin.saveConfig();
			}
			
			// Leave Game sign
			if (line2.equalsIgnoreCase("leave")) {
				if (line3.equalsIgnoreCase("")) { 
					player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noSignArenaName"));
					return;
							}
				// Player does not have permission to place sign
				if (!player.hasPermission("insanityrun.sign")) {
					player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noSignPerms"));
					return;
				}
				// Does the arena in fact exist?
				if (InsanityRun.plugin.getConfig().getString(line3 + ".world") == null) {
					player.sendMessage(ChatColor.RED + line3 + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noArena"));
					return;
				}				
				// Update the colours and state of the sign to look official
				event.setLine(0, ChatColor.BLUE + "[" + gameName + "]");
				event.setLine(1, ChatColor.YELLOW + "Leave Game");
				event.setLine(2, ChatColor.GREEN + line3);
			}
			
			// Info sign
			if (line2.equalsIgnoreCase("info")) {
				if (line3.equalsIgnoreCase("")) { 
					player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noSignArenaName"));
					return;
							}
				// Player does not have permission to place sign
				if (!player.hasPermission("insanityrun.sign")) {
					player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noSignPerms"));
					return;
				}
				// Does the arena in fact exist?
				if (InsanityRun.plugin.getConfig().getString(line3 + ".world") == null) {
					player.sendMessage(ChatColor.RED + line3 + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noArena"));
					return;
				}				
				// Update the colours and state of the sign to look official
				event.setLine(0, ChatColor.BLUE + "[" + gameName + "]");
				event.setLine(1, ChatColor.YELLOW + "Arena Info");
				event.setLine(2, ChatColor.GREEN + line3);
			}
			
			// High scores sign
			// Times Game sign
			if (line2.equalsIgnoreCase("times")) {
				if (line3.equalsIgnoreCase("")) { 
					player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noSignArenaName"));
					return;
							}
				// Player does not have permission to place sign
				if (!player.hasPermission("insanityrun.sign")) {
					player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noSignPerms"));
					return;
				}
				// Does the arena in fact exist?
				if (InsanityRun.plugin.getConfig().getString(line3 + ".world") == null) {
					player.sendMessage(ChatColor.RED + line3 + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noArena"));
					return;
				}				
				// Update the colours and state of the sign to look official
				String arenaName = line3;
				event.setLine(0, ChatColor.BLUE + "[" + gameName + "]");
				event.setLine(1, ChatColor.YELLOW + "Fastest Run");
				event.setLine(2, ChatColor.GREEN + line3);
				event.setLine(3, "Nobody --:--");
				
				// Save sign location to config.yml
				Location loc = event.getBlock().getLocation();
				InsanityRun.plugin.getConfig().set(arenaName + ".fastsign.x", loc.getX());
				InsanityRun.plugin.getConfig().set(arenaName + ".fastsign.y", loc.getY());
				InsanityRun.plugin.getConfig().set(arenaName + ".fastsign.z", loc.getZ());
				InsanityRun.plugin.getConfig().set(arenaName + ".fastsign.world", loc.getWorld().getName());
				InsanityRun.plugin.getConfig().set(arenaName + ".fastest.1.name", "Nobody");
				InsanityRun.plugin.getConfig().set(arenaName + ".fastest.2.name", "Nobody");
				InsanityRun.plugin.getConfig().set(arenaName + ".fastest.3.name", "Nobody");
				InsanityRun.plugin.getConfig().set(arenaName + ".fastest.4.name", "Nobody");
				InsanityRun.plugin.getConfig().set(arenaName + ".fastest.5.name", "Nobody");
				InsanityRun.plugin.getConfig().set(arenaName + ".fastest.1.time", 1500000);
				InsanityRun.plugin.getConfig().set(arenaName + ".fastest.2.time", 1600000);
				InsanityRun.plugin.getConfig().set(arenaName + ".fastest.3.time", 1700000);
				InsanityRun.plugin.getConfig().set(arenaName + ".fastest.4.time", 1800000);
				InsanityRun.plugin.getConfig().set(arenaName + ".fastest.5.time", 1900000);
				InsanityRun.plugin.getConfig().set(arenaName + ".fastest.1.coins", 0);
				InsanityRun.plugin.getConfig().set(arenaName + ".fastest.2.coins", 0);
				InsanityRun.plugin.getConfig().set(arenaName + ".fastest.3.coins", 0);
				InsanityRun.plugin.getConfig().set(arenaName + ".fastest.4.coins", 0);
				InsanityRun.plugin.getConfig().set(arenaName + ".fastest.5.coins", 0);
				
				InsanityRun.plugin.saveConfig();
			}
			
		}		
	}
}
