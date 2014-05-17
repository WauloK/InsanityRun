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

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.obsidianloft.insanityrun.GameManager;
import com.obsidianloft.insanityrun.InsanityRun;
import com.obsidianloft.insanityrun.iPlayer;


public class TaskManager implements Runnable {

	@Override
	@SuppressWarnings("deprecation")
	public void run() {
		ArrayList<String> playersToKickForIdling = new ArrayList<String>();
		
		Iterator<String> iterator = InsanityRun.playerObject.keySet().iterator();
		while(iterator.hasNext()) {
			int lastX,lastZ;
			int locX,locZ;
			String playerName = iterator.next();
			iPlayer playerObject = InsanityRun.playerObject.get(playerName);
			Player player = Bukkit.getServer().getPlayer(playerName);
			Location loc = player.getLocation();
			int tempIdleCount=0;
			
			lastX=playerObject.getIdleX();
			lastZ=playerObject.getIdleZ();
			locX=(int) loc.getX();
			locZ=(int) loc.getZ();
			playerObject.setIdleX(locX);
			playerObject.setIdleZ(locZ);
			
			if ((locX == lastX) && (locZ == lastZ)) {
				tempIdleCount = playerObject.getIdleCount();
				tempIdleCount++;
				playerObject.setIdleCount(tempIdleCount);
			}
			else {
				playerObject.setIdleCount(0);
			}
			
			if (playerObject.getFrozen()) {
				playerObject.setIdleCount(0);
			}
			if (!playerObject.getInGame()) {
				playerObject.setIdleCount(0);
			}			
			if (tempIdleCount >= InsanityRun.idleKickTime) {
				if (!playerObject.getFrozen() && playerObject.getInGame()) {
					playersToKickForIdling.add(playerName);
				}
			}
		}
		// If players in idlekick list, kick them
		for (String idlePlayers:playersToKickForIdling) {
			Player player = InsanityRun.plugin.getServer().getPlayer(idlePlayers);
			iPlayer playerObject = InsanityRun.playerObject.get(idlePlayers);
			String arenaName = playerObject.getInArena();
			player.teleport(playerObject.getSignClickLoc());
			GameManager.gameOver(player, playerObject.getInArena(), playerObject);
			InsanityRun.plugin.getServer().getPlayer(idlePlayers).sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".idleKickText"));
			GameManager.refundMoney(arenaName,idlePlayers);
		}
	}
}
