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

import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.obsidianloft.insanityrun.GameManager;
import com.obsidianloft.insanityrun.InsanityRun;

public class ClickSignListener implements Listener {
	public static int MinutesToCountDown=5;
	public static int SecondsToCountDown=5;
	
	@EventHandler
	public void interact(PlayerInteractEvent event) {
		String gameName = InsanityRun.gameName;
		Player player = event.getPlayer();
		Action action = event.getAction();
		Block clickedBlock = event.getClickedBlock();
		
		// Stop accidental destruction of our game signs!
		// Even stops admins with Gamemode 1!! WEEEOOOO!
		if (action == Action.LEFT_CLICK_BLOCK) {
			if(!(clickedBlock.getType()==Material.SIGN || clickedBlock.getType()==Material.SIGN_POST || clickedBlock.getType()==Material.WALL_SIGN)) return;
			Sign sign = (Sign) clickedBlock.getState();
			String line1 = sign.getLine(0);
			if (line1.contains(gameName)) {
			event.setCancelled(true);
			}
		}
		
		// Player Right-clicked the sign to join
		if (action == Action.RIGHT_CLICK_BLOCK) {
			if(!(clickedBlock.getType()==Material.SIGN || clickedBlock.getType()==Material.SIGN_POST || clickedBlock.getType()==Material.WALL_SIGN)) return;
			Sign sign = (Sign) clickedBlock.getState();
			String line1 = sign.getLine(0);
			String line2 = sign.getLine(1);
			String line3 = sign.getLine(2);
			
			if (!line1.contains(gameName)) { return; }
			// Player clicked InsanityRun "JOIN GAME" sign
			if (line2.contains("Join Game")) {
				line3=ChatColor.stripColor(line3);
				GameManager.joinGame(player,line3);
			}
			if (line2.contains("Leave Game")) {
				line3=ChatColor.stripColor(line3);
				GameManager.leaveGame(player,line3,InsanityRun.playerObject.get(player.getName()));
			}
			if (line2.contains("Arena Info")) {
				String arenaName = ChatColor.stripColor(line3);
				if (InsanityRun.plugin.getConfig().getString(arenaName + ".world") == null || InsanityRun.plugin.getConfig().getString(arenaName + ".world").equals("")) {
					player.sendMessage(ChatColor.RED + arenaName + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noArena"));
					return;
				}
				player.sendMessage(ChatColor.YELLOW + "[Insanity Run]");
				player.sendMessage(ChatColor.GREEN + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".arenaNameText") + ChatColor.WHITE + arenaName);
				player.sendMessage(ChatColor.GREEN + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".arenaChargeText") + ChatColor.WHITE + InsanityRun.plugin.getConfig().getInt(arenaName + ".charge"));
				player.sendMessage(ChatColor.GREEN + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".arenaPayText") + ChatColor.WHITE + InsanityRun.plugin.getConfig().getInt(arenaName + ".pay"));
				if (InsanityRun.playersInThisArena.get(arenaName)!=null) {
					player.sendMessage(ChatColor.GREEN + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".playersInArena") + ChatColor.WHITE + InsanityRun.playersInThisArena.get(arenaName));
				}
				else
				{
					player.sendMessage(ChatColor.GREEN + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".playersInArena") + ChatColor.WHITE + "0");
				}
				if (InsanityRun.plugin.getConfig().getString(arenaName + ".link") != null) {
					player.sendMessage(ChatColor.GREEN + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".arenaLinkText") + ChatColor.WHITE + InsanityRun.plugin.getConfig().getString(arenaName + ".link"));
				}
			}
			
			// Player clicked "Fastest Run" sign
			if (line2.contains("Fastest Run")) {
				String arenaName = ChatColor.stripColor(line3);
				if (InsanityRun.plugin.getConfig().getString(arenaName + ".world") == null || InsanityRun.plugin.getConfig().getString(arenaName + ".world").equals("")) {
					player.sendMessage(ChatColor.RED + arenaName + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noArena"));
					return;
				}
				player.sendMessage(ChatColor.YELLOW + "[Insanity Run] " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".fastestRunsText"));
				for (int i = 1; i < 6; i++) {
					player.sendMessage(ChatColor.GREEN + "" + i + ". " + formatIntoHHMMSS(InsanityRun.plugin.getConfig().getLong(arenaName + ".fastest."+i+".time")) + " - " + InsanityRun.plugin.getConfig().getString(arenaName + ".fastest."+i+".name") + " (" + InsanityRun.plugin.getConfig().getString(arenaName + ".fastest."+i+".coins") + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".gameCurrency") + ")");
				}
			}
		}
	}
	// Convert time
	private static String formatIntoHHMMSS(Long millisecs)
	{
		int secs = (int) (millisecs / 1000);
		int remainder = secs % 3600;
		int minutes = remainder / 60;
		int seconds = remainder % 60;

		return new StringBuilder().append(minutes).append(":").append(seconds < 10 ? "0" : "").append(seconds).toString();
	}
}
