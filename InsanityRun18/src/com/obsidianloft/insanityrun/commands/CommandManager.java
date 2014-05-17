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

package com.obsidianloft.insanityrun.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.obsidianloft.insanityrun.GameManager;
import com.obsidianloft.insanityrun.InsanityRun;

@SuppressWarnings("deprecation")
public class CommandManager implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player) sender;

		if (player instanceof Player) {

			if (commandLabel.equalsIgnoreCase("irun")) {

				// IRUN HELP
				if (args.length == 0) {
					GameManager.showHelp(player);
					return true;
				}

				// Get subcommand
				String subCom = args[0];

				// IRUN SETPAY ARENA
				if (args.length == 3) {
					if (subCom.equalsIgnoreCase("spa") || subCom.equalsIgnoreCase("setpay") ) {
						if (player.hasPermission("insanityrun.setpay")) {
							String arenaName = args[1];
							int pay = Integer.parseInt(args[2]);
							GameManager.setPayArena(player,arenaName,pay);
							return true;
						}
						else {
							player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noCmdPerms") + " " + subCom);
							return true;
						}
					}					
				}

				// IRUN SETCHARGE ARENA
				if (args.length == 3) {
					if (subCom.equalsIgnoreCase("sch") || subCom.equalsIgnoreCase("setcharge") ) {
						if (player.hasPermission("insanityrun.setpay")) {
							String arenaName = args[1];
							int pay = Integer.parseInt(args[2]);
							GameManager.setChargeArena(player,arenaName,pay);
							return true;
						}
						else {
							player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noCmdPerms") + " " + subCom);
							return true;
						}
					}					
				}				

				// IRUN ADMIN JOIN PLAYERS
				if (args.length == 3) {
					if (subCom.equalsIgnoreCase("adj") || subCom.equalsIgnoreCase("adjoin") ) {
						if (player.hasPermission("insanityrun.create")) {
							String playerName = args[1];
							String arenaName = args[2];
							Player targetPlayer = InsanityRun.plugin.getServer().getPlayer(playerName);
							if (targetPlayer instanceof Player) {
								if (InsanityRun.plugin.getConfig().getString(arenaName + ".world") == null) {
									player.sendMessage(ChatColor.RED + arenaName + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noArena"));
									return true;
								}
								else {
									GameManager.joinGame(targetPlayer, arenaName);
									return true;
								}
							}

							return true;
						}
						else {
							player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noCmdPerms") + " " + subCom);
							return true;
						}
					}					
				}				

				// IRUN ADMIN LEAVE PLAYERS
				if (args.length == 3) {
					if (subCom.equalsIgnoreCase("adl") || subCom.equalsIgnoreCase("adleave") ) {
						if (player.hasPermission("insanityrun.create")) {
							String playerName = args[1];
							String arenaName = args[2];
							Player targetPlayer = InsanityRun.plugin.getServer().getPlayer(playerName);
							if (targetPlayer instanceof Player) {
								if (InsanityRun.plugin.getConfig().getString(arenaName + ".world") == null) {
									player.sendMessage(ChatColor.RED + arenaName + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noArena"));
									return true;
								}
								else {
									GameManager.leaveGame(targetPlayer, arenaName,InsanityRun.playerObject.get(playerName));
									return true;
								}
							}

							return true;
						}
						else {
							player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noCmdPerms") + " " + subCom);
							return true;
						}
					}					
				}				

				// IRUN LINK ARENAS
				if (args.length == 3) {
					if (subCom.equalsIgnoreCase("lnk") || subCom.equalsIgnoreCase("linkarenas") ) {
						if (player.hasPermission("insanityrun.create")) {
							String arenaName1 = args[1];
							String arenaName2 = args[2];

							// Do the arenas exist?
							if (InsanityRun.plugin.getConfig().getString(arenaName1 + ".world") == null) {
								player.sendMessage(ChatColor.RED + arenaName1 + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noArena"));
								return true;
							}
							if (InsanityRun.plugin.getConfig().getString(arenaName2 + ".world") == null) {
								player.sendMessage(ChatColor.RED + arenaName2 + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noArena"));
								return true;
							}

							// Link arenas
							GameManager.linkArenas(player,arenaName1,arenaName2);							
							return true;
						}
						else {
							player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noCmdPerms") + " " + subCom);
							return true;
						}
					}					
				}


				// IRUN LIST
				if (args.length == 1) {
					if (subCom.equalsIgnoreCase("list")) {
						if (player.hasPermission("insanityrun.create")) {
							player.sendMessage(ChatColor.YELLOW + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".listArenas") + ": " + InsanityRun.arenaList);
							return true;
						}
						else {
							player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noCmdPerms") + " " + subCom);
							return true;
						}
					}
				}

				// IRUN HELP
				if (args.length != 2) {
					GameManager.showHelp(player);
					return true;
				}

				// Grab info from arguments.
				String arenaName = args[1];

				// IRUN JOIN
				if (subCom.equalsIgnoreCase("join")) {
					GameManager.joinGame(player,arenaName);
					return true;
				}
				// IRUN LEAVE
				if (subCom.equalsIgnoreCase("leave")) {
					GameManager.leaveGame(player,arenaName, InsanityRun.playerObject.get(player.getName()));
					return true;
				}
				// IRUN CREATE ARENA
				if (subCom.equalsIgnoreCase("cre") || subCom.equalsIgnoreCase("create") || subCom.equalsIgnoreCase("select")) {
					if (player.hasPermission("insanityrun.create")) {
						GameManager.createArena(player,arenaName);
						return true;
					}
					else {
						player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noCmdPerms") + " " + subCom);
						return true;
					}
				}				
				// IRUN DELETE ARENA
				if (subCom.equalsIgnoreCase("del") || subCom.equalsIgnoreCase("delete") ) {
					if (player.hasPermission("insanityrun.delete")) {
						GameManager.deleteArena(player,arenaName);
						return true;
					}
					else {
						player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noCmdPerms") + " " + subCom);
						return true;
					}
				}
				// IRUN SETSPAWN ARENA
				if (subCom.equalsIgnoreCase("ssp") || subCom.equalsIgnoreCase("setspawn") ) {
					if (player.hasPermission("insanityrun.setspawn")) {
						GameManager.setSpawnArena(player,arenaName);
						return true;
					}
					else {
						player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noCmdPerms")+ " " + subCom);
						return true;
					}
				}
			}
			GameManager.showHelp(player);
			return true;
		}
		return false;
	}
}
