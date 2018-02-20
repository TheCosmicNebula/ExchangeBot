package com.zeher.exchangebot.core.channel;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zeher.exchangebot.core.ExchangeBotCore;
import com.zeher.exchangebot.core.api.MessageAPI;

import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.permissions.Role;

public class Blacklist {
	
	public static void main(Message message, User messageUser, String messageUserName, Channel blacklist, Server serv, JSONArray blacklist_array, JSONArray user_array, Role primeadmin, Role admin, Role primeguardian, Role guardian, boolean is_drop_open) {
		if(message.getContent().contains("!bl")){
			if(MessageAPI.isMessageUserRole(message, primeadmin, admin, primeguardian, guardian, serv)){
				List<User> blacklistUser = message.getMentions();
    			User user = blacklistUser.get(0);
    			
    			addUserToBlacklist(message, user, messageUser, blacklist, serv, blacklist_array, user_array, is_drop_open);
    			ExchangeBotCore.saveSystem();
			} else {
				MessageAPI.messageUserNoPerm(message, messageUser);
			}
		}
		
		if(message.getContent().contains("!unbl")){
			if(MessageAPI.isMessageUserRole(message, primeadmin, admin, primeguardian, guardian, serv)){
				List<User> blacklistUser = message.getMentions();
    			User user = blacklistUser.get(0);
    			
    			removeUserFromBlacklist(message, user, messageUser, blacklist, blacklist_array);
    			ExchangeBotCore.saveSystem();
			} else {
				MessageAPI.messageUserNoPerm(message, messageUser);
			}
		}
		
		else if(message.getContent().equals("!chk_bl")){
			if(MessageAPI.isMessageUserRole(message, admin, primeadmin, guardian, primeguardian, serv)){
    			messageUser.sendMessage("You have to use `!chk_bl @user` to check the blacklist for a user.");
    			message.delete();
			} else {
				MessageAPI.messageUserNoPerm(message, messageUser);
			}
		}
	
		else if(message.getContent().contains("!chk_bl")){
			if(MessageAPI.isMessageUserRole(message, primeadmin, admin, primeguardian, guardian, serv)){
				List<User> blacklistUser = message.getMentions();
    			User user = blacklistUser.get(0);
    			if(ExchangeBotCore.checkUserIsReged(user)){
        			if(ExchangeBotCore.checkUserIsBlacked(user)){
        				messageUser.sendMessage("User: `" + user.getName() + "` is currently blacklisted.");
        				message.delete();
        			} else {
        				messageUser.sendMessage("User: `" + user.getName() + "` is not currently blacklisted");
        				message.delete();
        			}
    			} else {
    				messageUser.sendMessage("User: `" + user.getName() + "` is not registered.");
    				message.delete();
    			}
			} else {
				MessageAPI.messageUserNoPerm(message, messageUser);
			}
		}
		
		else if(message.getContent().contains("!str")){
			if(MessageAPI.isMessageUserRole(message, admin, primeadmin, guardian, primeguardian, serv)){
				List<User> blacklistUser = message.getMentions();
				User user = blacklistUser.get(0);
				strikeUser(message, user, messageUser, blacklist, serv, user_array, blacklist_array, is_drop_open);
				ExchangeBotCore.saveSystem();
			} else {
				MessageAPI.messageUserNoPerm(message, messageUser);
			}
		}
		
		else if(message.getContent().contains("!res_str")){
			String check;
			User clear = null;
			if(message.getMentions().size() > 0){
				List<User> clearUser = message.getMentions();
				clear = clearUser.get(0);
			}
			
			if(MessageAPI.isMessageUserRole(message, admin, primeadmin, guardian, primeguardian, serv)){
				if(message.getContent().contains(" ")){
					check = message.getContent().substring(message.getContent().indexOf(' ') + 1).trim();
					if(check.equals("all")){
						ExchangeBotCore.clearAllStrikes();
    					message.delete();
    					messageUser.sendMessage("All users strikes have been reset.");
    					System.out.println(messageUserName + "Reset all users strikes.");
					} else {
						if(clear != null){
							if(ExchangeBotCore.checkUserIsReged(clear)){
								ExchangeBotCore.clearUserStrikes(clear);
								ExchangeBotCore.saveSystem();
        						messageUser.sendMessage("User: `" + clear.getName() + "`'s strikes have been cleared.");
        						message.delete();
        					} else {
        						messageUser.sendMessage("User: `" + clear.getName() + "` is not registered.");
        						message.delete();
        					}
						} else {
    						messageUser.sendMessage("The `!res_str` commands are: "
        							+ "\n - In #blacklist - "
        							+ "\n \t `!res_str all` to reset all users strikes."
									+ "\n"
        							+ "\n \t `!res_str @usersdiscordname` to reset a specific users strikes.");
                			message.delete();
						}
					}
				} else {
					messageUser.sendMessage("The `!res_str` commands are: "
							+ "\n - In #blacklist - "
							+ "\n \t `!res_str-all` to reset all users strikes."
							+ "\n"
							+ "\n \t `!res_str @usersdiscordname` to reset a specific users strikes.");
        			message.delete();
				}
			} else {
				MessageAPI.messageUserNoPerm(message, messageUser);
			}
		}
		
		else if(message.getContent().contains("!chk_str")){
			List<User> checkUser = message.getMentions();
			User user = checkUser.get(0);
			
			if(MessageAPI.isMessageUserRole(message, admin, primeadmin, guardian, primeguardian, serv)){
				if(ExchangeBotCore.checkUserIsReged(user)){
					ExchangeBotCore.checkUsersStrikes(message, user, messageUser);
				} else {
					messageUser.sendMessage("User: `" + user.getName() + "` is not registered.");
					message.delete();
				}
			} else {
				MessageAPI.messageUserNoPerm(message, messageUser);
			}
		}
		
		else if(message.getContent().contains("!res_drop")){
			String check;
			User clear = null;
			if(message.getMentions().size() > 0){
				List<User> clearUser = message.getMentions();
				clear = clearUser.get(0);
			}
			if(message.getContent().contains(" ")){
				check = message.getContent().substring(message.getContent().indexOf(' ')+ 1).trim();
				if(MessageAPI.isMessageUserRole(message, guardian, primeguardian, serv)){
					if(check.equals("all")){
						ExchangeBotCore.clearAllDropCounts();
    					message.delete();
    					messageUser.sendMessage("All users drop counts have been reset.");
    					System.out.println(messageUserName + "Reset all users drop counts.");
					} else {
						if(clear != null){
							if(ExchangeBotCore.checkUserIsReged(clear)){
								ExchangeBotCore.clearUserDrops(clear);
        						messageUser.sendMessage("User: `" + clear.getName() + "`'s drop count has been reset.");
        						message.delete();
        					} else {
        						messageUser.sendMessage("User: `" + clear.getName() + "` is not registered.");
        						message.delete();
        					}
						}
					}
				} else {
					MessageAPI.messageUserNoPerm(message, messageUser);
				}
			}
		}
		else if (message.getContent().equals("!res")) {
			if (MessageAPI.isMessageUserRole(message, guardian, primeguardian, serv)) {
				messageUser.sendMessage("The `!res` commands are: "
						+ "\n \t `!res_drop all` to reset all users drop counts."
						+ "\n"
						+ "\n \t `!res_drop @usersdiscordname` to reset a specific users drop count.");
				message.delete();
			} else {
				MessageAPI.messageUserNoPerm(message, messageUser);
			}
		}

	}
	
	public static void strikeUser(Message message, User user, User messageUser, Channel blacklist, Server serv, JSONArray user_array, JSONArray blacklist_array, boolean is_drop_open){
		if(ExchangeBotCore.checkUserIsReged(user)){
			if(!ExchangeBotCore.checkUserIsBlacked(user)){
				for(int i = 0; i < user_array.length(); i++){
					JSONObject userObj = user_array.getJSONObject(i);
					if(userObj.getString("User_Id").equals(user.getId())){
						JSONObject userObj2 = new JSONObject();
						userObj2.put("Discord_Name", userObj.getString("Discord_Name"));
						userObj2.put("Social_Club", userObj.getString("Social_Club"));
						userObj2.put("User_Id", userObj.getString("User_Id"));
						userObj2.put("Drops", userObj.getInt("Drops"));
						userObj2.put("Strikes", userObj.getInt("Strikes") + 1);
						
						user_array.put(i, userObj2);
						
						blacklist.sendMessage("User: `" + user.getName() + "` has been issued a strike."
								+ "\nIssued by: " + messageUser.getMentionTag() + ".");
						messageUser.sendMessage("User: `" + user.getName() + "` has been issued a strike.");
						message.delete();
						
						if (userObj.getInt("Strikes") >= 2) {
							addUserToBlacklist(message, user, messageUser, blacklist, serv, blacklist_array, user_array, is_drop_open);
						}
					}
				}
			} else {
				messageUser.sendMessage("User: `" + user.getName() + "` is already blacklisted.");
				message.delete();
			}
		} else {
			messageUser.sendMessage("User: `" + user.getName() + "` is not registered.");
			message.delete();
		}
	}

	public static void removeUserFromBlacklist(Message message, User user, User messageUser, Channel blacklist, JSONArray blacklist_array){
		if(ExchangeBotCore.checkUserIsReged(user)){
			if(ExchangeBotCore.checkUserIsBlacked(user)){
				for(int i = 0; i < blacklist_array.length(); i++){
					JSONObject test = blacklist_array.getJSONObject(i);
					if(test.getString("User_Id").equals(user.getId())){
						String discordName = test.getString("Discord_Name");
						String gtaName = test.getString("Social_Club");
						
						blacklist_array.remove(i);
						ExchangeBotCore.clearUserStrikes(user);
						messageUser.sendMessage("User: | `" + discordName + "` | Sc: `" + gtaName + "` | has been removed from the blacklist.");
						blacklist.sendMessage("Removed user from blacklist: | `" + discordName + "` | SC: `" + gtaName + "` | \nIssued by: " + messageUser.getMentionTag() + ".");
						System.out.println("Removed user from blacklist: | " + discordName + " | SC: " + gtaName + " | Issued by: " + messageUser.getName() + " |");
						
						message.delete();
					}
				}
			} else {
				messageUser.sendMessage("User: `" + user.getName() + "` isn't blacklisted.");
				message.delete();
			}
		} else {
			messageUser.sendMessage("User: `" + user.getName() + "` is not registered.");
			message.delete();
		}

	}
	
	public static void addUserToBlacklist(Message message, User user, User messageUser, Channel blacklist, Server serv, JSONArray blacklist_array, JSONArray user_array, boolean is_drop_open){
		if(ExchangeBotCore.checkUserIsReged(user)){
			if(!ExchangeBotCore.checkUserIsBlacked(user)){
				for(int i = 0; i < user_array.length(); i++){
					JSONObject test = user_array.getJSONObject(i);
					if(test.getString("User_Id").equals(user.getId())){
						String discordName = test.getString("Discord_Name");
						String gtaName = test.getString("Social_Club");
						Integer drops = test.getInt("Drops");
						int strikes = test.getInt("Strikes");
						
						blacklist_array.put(test);
						messageUser.sendMessage("User: | `" + discordName + "` | SC: `" + gtaName + "` | Drops: `" + drops + "` | Strikes: `" + strikes +"` | has been added to the blacklist.");
						blacklist.sendMessage("Added user to blacklist: | `" + discordName + "` | SC: `" + gtaName + "` | Drops: `" + drops +"` | Strikes: `" + strikes + "` | \nIssued by: " + messageUser.getMentionTag() + ".");
						System.out.println("Added user to blacklist: | " + discordName + " | SC: " + gtaName + " | Drops: " + drops + " | Issued by: " + messageUser.getName() + " |");
						message.delete();
					
						if(is_drop_open){
							ExchangeBotCore.removeUserFromQueueAdmin(message, messageUser, discordName, serv);
						}
					}
				}
			} else {
				messageUser.sendMessage("User: `" + user.getName() + "` is already blacklisted.");
				message.delete();
			}
		} else {
			messageUser.sendMessage("User: `" + user.getName() + "` is not registered.");
			message.delete();
		}
	}
	
}
