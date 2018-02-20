package com.zeher.exchangebot.core.channel;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zeher.exchangebot.core.ExchangeBotCore;
import com.zeher.exchangebot.core.api.DropNameAPI;
import com.zeher.exchangebot.core.api.MessageAPI;
import com.zeher.exchangebot.core.util.BotUtils;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.permissions.Role;

public class ExchangeBotAdmin {

	public static void main(Message message, User messageUser, String messageUserId, DiscordAPI api, Role primedropper,
			Role dropper, Role primeadmin, Role admin, Role guardian, Role primeguardian, Server serv,
			JSONArray user_array, JSONArray dropper_array, JSONArray blacklist_array, JSONArray queue_array,
			JSONArray accepted_array, boolean is_drop_open, HashMap<String, String> active_droppers, String drop_entry_name) {

		if (message.getContent().equals("!shutdown")) {
			if (MessageAPI.isMessageUserRole(message, guardian, primeguardian, serv)) {
				message.delete();
				BotUtils.sleep(5);
				try {
					ExchangeBotCore.shutdown(api);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				MessageAPI.messageUserNoPerm(message, messageUser);
			}
		}

		else if (message.getContent().contains("!chk")) {
			String check;
			if (message.getContent().contains(" ")) {
				check = message.getContent().substring(message.getContent().indexOf(' ') + 1).trim();

				if (MessageAPI.isMessageUserRole(message, guardian, primeguardian, serv)) {
					if (check.equals("users")) {
						System.out.println(" - Checking Users - ");
						System.out.println(" - " + user_array.length() + " Total Users - ");
						for (int i = 0; i < user_array.length(); i++) {
							JSONObject test = user_array.getJSONObject(i);

							String discordName = test.getString("Discord_Name");
							String gtaName = test.getString("Social_Club");
							String userId = test.getString("User_Id");
							Integer drops = test.getInt("Drops");

							System.out.println("Discord: " + discordName + " | Social club: " + gtaName + " | UserId: "	+ userId + " | Drops: " + drops + " |");
							BotUtils.sleep(1);
						}
					} else if (check.equals("droppers")) {
						System.out.println(" - " + dropper_array.length() + " Total Droppers - ");
						for (int i = 0; i < dropper_array.length(); i++) {
							JSONObject test = dropper_array.getJSONObject(i);

							String discordName = test.getString("Discord_Name");
							String gtaName = test.getString("Social_Club");
							String userId = test.getString("User_Id");

							System.out.println("Discord: " + discordName + " | Social club: " + gtaName + " | UserId: " + userId + " |");
							BotUtils.sleep(1);
						}
					} else if (check.equals("queue")) {
						System.out.println(" - Checking Queue - ");
						System.out.println(" - " + queue_array.length() + " Total Users in queue - ");
						for (int i = 0; i < queue_array.length(); i++) {
							JSONObject test = queue_array.getJSONObject(i);

							String discordName = test.getString("Discord_Name");
							String gtaName = test.getString("Social_Club");
							String userId = test.getString("User_Id");
							Integer drops = test.getInt("Drops");

							System.out.println("Discord: " + discordName + " | Social club: " + gtaName + " | UserId: " + userId + " | Drops: " + drops + " |");
							
							BotUtils.sleep(1);
						}
					} else if (check.equals("accepted")) {
						System.out.println(" - Checking accepted Users - ");
						System.out.println(" - " + accepted_array.length() + " Total accepted Users - ");
						for (int i = 0; i < accepted_array.length(); i++) {
							JSONObject test = accepted_array.getJSONObject(i);

							String discordName = test.getString("Discord_Name");
							String gtaName = test.getString("Social_Club");
							String userId = test.getString("User_Id");
							Integer drops = test.getInt("Drops");

							System.out.println("Discord: " + discordName + " | Social club: " + gtaName + " | UserId: " + userId + " | Drops: " + drops + " |");
							
							BotUtils.sleep(1);
						}
					} else if (check.equals("blacklist")) {
						System.out.println(" - Checking blacklisted Users - ");
						System.out.println(" - " + blacklist_array.length() + " Total blacklisted Users - ");
						for (int i = 0; i < blacklist_array.length(); i++) {
							JSONObject test = blacklist_array.getJSONObject(i);

							String discordName = test.getString("Discord_Name");
							String gtaName = test.getString("Social_Club");
							String userId = test.getString("User_Id");
							Integer drops = test.getInt("Drops");

							System.out.println("Discord: " + discordName + " | Social club: " + gtaName + " | UserId: " + userId + " | Drops: " + drops + " |");
							BotUtils.sleep(1);
						}
					} else if (check.equals("all")) {
						System.out.println(" - Checking All - ");
						System.out.println("Number of registered users: " + user_array.length());
						System.out.println("Number of registered droppers: " + dropper_array.length());
						System.out.println("Number of blacklisted users: " + blacklist_array.length());
						if (is_drop_open) {
							System.out.println(" - Drop open - ");
						} else {
							System.out.println(" - Drop closed - ");
						}
						
						System.out.println("Active droppers: " + active_droppers.size());
						System.out.println("Number of users in the queue: " + queue_array.length());
						System.out.println("Number of accepted users: " + accepted_array.length());

					} else {
						if (ExchangeBotCore.checkUserIsRegedUserName(check)) {
							ExchangeBotCore.checkUserInfo(check, messageUser);
							message.delete();
						} else {
							messageUser.sendMessage("User: `" + check + "` is not registered.");
							message.delete();
						}
					}
					message.delete();
				} else {
					MessageAPI.messageUserNoPerm(message, messageUser);
				}
			} else {
				if (MessageAPI.isMessageUserRole(message, dropper, primedropper, serv)) {
					messageUser.sendMessage(
							"Check commands are: \n`drop` - Checks all droppers on file. \n`use` - Checks all users on file. \n`queue` - Checks all users in the queue. \n`accept` - Check all accepted users. \n`bl` - Check all blacklisted users. \n`all` - Checks all files.");
					message.delete();
				} else {
					messageUser.sendMessage("Use `!chk reg` to check your currently registered username.");
					message.delete();
				}
			}
		}
		
		else if(message.getContent().contains("!removeuser")){
			if(MessageAPI.isMessageUserRole(message, guardian, primeguardian, serv)){
			String check;
				if(message.getContent().contains(" ")){
					check = message.getContent().substring(message.getContent().indexOf(' ')+ 1).trim();
						if(ExchangeBotCore.checkUserIsRegedUserName(check)){
    						if(is_drop_open){
    							ExchangeBotCore.removeUserFromQueueAdmin(message, messageUser, check, serv);
    						} else {
    							messageUser.sendMessage("There is no open drop.");
    							message.delete();
    						}
    					} else {
    						messageUser.sendMessage("User: `" + check + "` is not currently registered.");
    						message.delete();
    					}
    				} else {
    					messageUser.sendMessage("You have to use `!removeuser usersname`.");
    					message.delete();
    				}
				} else {
					MessageAPI.messageUserNoPerm(message, messageUser);
				}
			}
		
		else if(message.getContent().equals("!changename")) {
			if(MessageAPI.isMessageUserRole(message, guardian, primeguardian, serv)) {
				String name = DropNameAPI.chooseName();
				messageUser.sendMessage("Drop entry name has been changed to: `" + name + "`.");
				drop_entry_name = name;
				
			} else {
				MessageAPI.messageUserNoPerm(message, messageUser);
			}
		}

		else if (message.getContent().equals("!save")) {
			if (MessageAPI.isMessageUserRole(message, guardian, primeguardian, serv)) {
				System.out.println(" - Saved System - ");
				ExchangeBotCore.saveSystem();
				message.delete();
				messageUser.sendMessage(ExchangeBotCore.string_save);
			} else {
				MessageAPI.messageUserNoPerm(message, messageUser);
			}
		}

		else if (message.getContent().equals("!load")) {
			if (MessageAPI.isMessageUserRole(message, guardian, primeguardian, serv)) {
				System.out.println(" - Loaded System - ");
				ExchangeBotCore.loadSystem();
				message.delete();
				messageUser.sendMessage(ExchangeBotCore.string_load);
			} else {
				MessageAPI.messageUserNoPerm(message, messageUser);
			}
		}

		else if (message.getContent().contains("!purge")) {
			String content;
			if (MessageAPI.isMessageUserRole(message, guardian, primeguardian, serv)) {
				if (message.getContent().contains(" ")) {
					content = message.getContent().substring(message.getContent().indexOf(' ') + 1).trim();
					if (content.equals("drop")) {
						ExchangeBotCore.cleardropper_array();
						messageUser.sendMessage(" - Dropperbase Purged - ");
					} else if (content.equals("use")) {
						ExchangeBotCore.clearuser_array();
						messageUser.sendMessage(" - Userbase Purged - ");
					} else if (content.equals("queue")) {
						ExchangeBotCore.closeQueue();
						messageUser.sendMessage(" - Queue Purged - ");
					} else if (content.equals("accept")) {
						ExchangeBotCore.clearAcceptArray();
						messageUser.sendMessage(" - Accepted users Purged - ");
					} else if (content.equals("blacklist")) {
						ExchangeBotCore.clearBlacklist();
						messageUser.sendMessage(" - Blacklist Purged - ");
					}
					message.delete();
				} else {
					messageUser
							.sendMessage("Purge commands are: " + "\n \t `!purge drop` - Purges all droppers on file. "
									+ "\n" + "\n \t `!purge use` - Purges all users on file. " + "\n"
									+ "\n \t `!purge queue` - Purges all users in the queue. " + "\n"
									+ "\n \t `!purge accept` - Purges all accepted users. " + "\n"
									+ "\n \t `!purge bl` - Purges all blacklisted users.");
					message.delete();
				}
			} else {
				MessageAPI.messageUserNoPerm(message, messageUser);
			}
		}
	}
}
