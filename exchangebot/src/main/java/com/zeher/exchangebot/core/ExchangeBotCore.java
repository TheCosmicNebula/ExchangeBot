package com.zeher.exchangebot.core;

import java.io.File;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.FutureCallback;
import com.zeher.exchangebot.core.util.BotUtils;
import com.zeher.exchangebot.core.api.DropNameAPI;
import com.zeher.exchangebot.core.api.MessageAPI;
import com.zeher.exchangebot.core.channel.Blacklist;
import com.zeher.exchangebot.core.channel.ExchangeBotAdmin;
import com.zeher.exchangebot.core.channel.SupportGeneral;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.permissions.Role;
import de.btobastian.javacord.listener.message.MessageCreateListener;

public class ExchangeBotCore {
	
	private static final String token = "MjcyODU5Mzk4MzU4MDQwNTc2.C2bZfg.x1mP7GeZZdaPJU93xpg-IQVa8Z0";
	
	public static String drop_entry_name;
	public static boolean is_drop_open = false;
	
	private static HashMap<String, String> active_droppers = new HashMap<String, String>();
	//static HashMap<String, String> active_droppersName = new HashMap<String, String>();
	
	private static JSONArray dropper_array = new JSONArray();
	private static JSONArray user_array = new JSONArray();
	private static JSONArray queue_array = new JSONArray();
	private static JSONArray accepted_array = new JSONArray();
	private static JSONArray blacklist_array = new JSONArray();
	
	private static JSONArray settings_array = new JSONArray();
	
	private static String announcement_id = "269111444526727170";
	
	public static String string_queue_position_prefix = "You are position ";
	public static String string_queue_position_suffix = " in the current queue";
	public static String string_queue_add = "You've been added to the current queue.";
	public static String string_queue_re_add = "You're already in the current queue.";
	public static String string_queue_remove = "You've been removed from the current queue.";
	public static String string_queue_leave = "You've left the current queue.";
	public static String string_queue_re_leave = "You've already left the current queue";
	public static String string_queue_closed = "@here" + " The queue has now closed. Try again next time!";
	
	public static String string_drop_end = "@here" + " This free drop lobby is now CLOSED.";
	public static String string_drop_closed = "There is no open drop right now, check `#announcements` for the next one!";
	public static String string_drop_num_check = "There is no open queue to check on.";
	public static String string_drop_already_closed = "There is no open drop to close.";
	public static String string_drop_already_open = "There is already a free drop lobby open. No messages sent.";
	
	public static String string_no_social = "We dont take kindly to putting social-ids in chat. Don't do it again.";
	
	public static String string_save = "System saved.";
	public static String string_load = "System loaded.";
	
	public static void main(String[] args) throws IOException{
		DiscordAPI api = Javacord.getApi(token, true);
		
		loadSystem();
		DropNameAPI.loadPrefix();
		DropNameAPI.loadSuffix();
		System.out.println(" - Loading System - ");
		
		api.setWaitForServersOnStartup(false);
		api.connect(new FutureCallback<DiscordAPI>() {
			
            public void onSuccess(DiscordAPI api) {
            	drop_entry_name = DropNameAPI.chooseName();
            	api.setGame("| !commands |");
                api.registerListener(new MessageCreateListener() {
                	
					@SuppressWarnings({ "unused" })
					public void onMessageCreate(DiscordAPI api, Message message) {
                    	Channel channel = message.getChannelReceiver();
                    	
                    	String channelname = "";
                    	String channelid = "";
                    	Server serv = null;
                    	User messageUser = message.getAuthor();
                    	String messageUserName = message.getAuthor().getName();
                    	String messageUserId = messageUser.getId();
                    	
                    	if (channel != null && !message.isPrivateMessage()){
                    		channelname = channel.getName();
                    		channelid = channel.getId();
                    		serv = channel.getServer();
                    	} else if (message.isPrivateMessage()){ }
                    	
                    	savePrefix();
                    	saveSuffix();
                    	
                    //Channels
                    	//System.out.println("| " + channelname + " | " + channelid + " |");
                    	
                    	Channel blacklist = serv.getChannelById("270985073161928717");
                    	Channel exchangebot_admin = serv.getChannelById("414829540066459659");
                    	
                    //Roles
                    	Role prisoner = serv.getRoleById("269825011093602305");
                    	
                    	Role member = serv.getRoleById("269116615134543873");
                    	Role primemember = serv.getRoleById("269116009670115330");

                    	Role dropper = serv.getRoleById("269115471855353856");
                    	Role primedropper = serv.getRoleById("269133327108276224");

                    	Role admin = serv.getRoleById("269115740664233984");
                    	Role primeadmin = serv.getRoleById("269115630131609600");
                    	
                    	Role guardian = serv.getRoleById("269115334714195970");
                    	Role primeguardian = serv.getRoleById("269115229315661824");
                    	
                    //System Strings
                    	String string_queue_num_check = "There are currently " + queue_array.length() + " users in the queue.";
                    	String string_queue_join = "Type `!join " + drop_entry_name + "` to join the current queue.";
                    	String string_queue_not = "You aren't in the current queue. " + string_queue_join;
                    	
                    	String string_drop_start = "@here" + " - A free drop lobby has been opened! Join the queue by using: `!join " + drop_entry_name + "` in `#bot-commands`.";
                    	
                    	String string_drop_start_sendname = "Alright! Opening a free drop lobby for " + messageUser.getMentionTag() + "\n \t Your SC name will be given to users and they will add you in-game. \n \t Use `!add X` to add X users to the drop lobby. \n \t Use `!qlength` to see the current queue length.";
                    	String string_drop_start_ = "Alright! Opening a free drop lobby for " + messageUser.getMentionTag() + "\n \t You will have to add users, with the SC names I give you. \n \t Use `!add X` to add X users to the drop lobby. \n \t Use `!qlength` to see the current queue length.";
                    	
                    	
					//TODO Support-General/Random
                    	if (channelname.equals("support-general") || channelname.equals("random")) {
							SupportGeneral.main(message, messageUser, dropper_array, user_array);
                    	}	
						
                    //TODO BlackList
						else if(channelname.equals("blacklist")){
                    		Blacklist.main(message, messageUser, messageUserName, blacklist, serv, blacklist_array, user_array, primeadmin, primeadmin, primeguardian, primeguardian, is_drop_open);
                    	}
                    //TODO ExchangeBot-Admin
                    	else if(channelname.equals("exchangebot-admin")) {
                    		ExchangeBotAdmin.main(message, messageUser, messageUserId, api, primedropper, primedropper, primeadmin, primeadmin, guardian, primeguardian, serv, user_array, dropper_array, blacklist_array, queue_array, accepted_array, is_drop_open, active_droppers, drop_entry_name);
                    	}
                    	
                    //TODO Bot-Commands
                    	else if(channelname.equals("bot-commands")){
                        //User commands
                    		if(message.getContent().contains("!join")){
                    			if(is_drop_open){
                    				if(message.getContent().contains("!join " + drop_entry_name)){
                    					if(checkUserIsReged(messageUser)){
                    						if(messageUser.getRoles(serv).contains(prisoner)){
                    							messageUser.sendMessage("You arent allowed to join the current queue! You are a prisoner. Contact a member of staff relating to your prisoner role.");
                    							message.delete();
                    						}
                    						else if(checkUserIsBlacked(messageUser)){
                        						addUserToQueue(message, messageUser);
                        						BotUtils.sleep(10);
                        						removeUserFromQueue(message, messageUser);
                        						
                        						message.delete();
                        					} else {
                        						addUserToQueue(message, messageUser);
                        					}
                        				} else {
                        					messageUser.sendMessage("You need to register in #bot-commands with `!registerdrop yoursc`, before joining a free drop lobby.");
                        					message.delete();
                        				}
                    				} else {
                    					messageUser.sendMessage(string_queue_join);
                    					message.delete();
                    				}
                    			} else {
                    				messageUser.sendMessage(string_drop_closed);
                    				message.delete();
                    			}
                    		}
                    		
                    		else if(message.getContent().equals("!pos")){
                    			if(is_drop_open){
                    				if(!checkQPosAndSend(message, messageUser)){
                    					messageUser.sendMessage(string_queue_not);
                    				}
                    			} else {
                    				messageUser.sendMessage(string_drop_closed);
                    				message.delete();
                    			}
                    		}
                    		
                    		else if(message.getContent().equals("!leave")){
                    			if(is_drop_open){
                    				for(int i = 0; i < queue_array.length(); i++){
                    		            JSONObject test = queue_array.getJSONObject(i);
                		                if(test.getString("User_Id").equals(messageUserId)){
                		                	queue_array.remove(i);
                		                	messageUser.sendMessage("You have left the queue.");
                		                	message.delete();
                		                } else {
                		                	messageUser.sendMessage(string_queue_not);
                		                	message.delete();
                		                }
                    				}
                    			} else {
                    				messageUser.sendMessage(string_drop_closed);
                    				message.delete();
                    			}
                    		}
                    		
                    		else if(message.getContent().contains("!register_drop")){
                    			String GTAName;
                    			if(MessageAPI.isMessageUserRole(message, dropper, primedropper, serv)){
	                    			if(message.getContent().contains(" ")){
	                    				GTAName = message.getContent().substring(message.getContent().indexOf(' ')+ 1).trim();
                    					try {
											saveDropper(message, messageUser, GTAName);
										} catch (IOException e) {
											e.printStackTrace();
										}
                    					message.delete();
		                    		}
                    			} else {
                    				MessageAPI.messageUserNoPerm(message, messageUser);
                    			}
                    		}
                    		
                    		else if(message.getContent().equals("!deregister_drop")){
                    			if(MessageAPI.isMessageUserRole(message, dropper, primedropper, serv)){
										deleteDropper(message, messageUser);
	                    				message.delete();
		                    	}else {
                    				MessageAPI.messageUserNoPerm(message, messageUser);
                    			}
                    		}
                    	
                    		else if (message.getContent().contains("!register")){
                    			String GTAName;
                				if(message.getContent().contains(" ")){
                    				GTAName = message.getContent().substring(message.getContent().indexOf(' ')+ 1).trim();
                    				try {
											registerUser(message, messageUser, GTAName);
										} catch (IOException e) {
											e.printStackTrace();
										}
                    				message.delete();
                				}
                    		}
                    		
                    		else if(message.getContent().equals("!deregister")){
                    			deregisterUser(message, messageUser);
                    		}
                    		
                    		else if(message.getContent().equals("!opendrop")){
                    			if(is_drop_open){
                    				if(MessageAPI.isMessageUserRole(message, dropper, primedropper, serv)){
	                    				openDropAlready(message, messageUser);
	                    			} else {
	                    				MessageAPI.messageUserNoPerm(message, messageUser);
	                    			}
                    			} else {
                    				if(MessageAPI.isMessageUserRole(message, dropper, primedropper, serv)){
                    					openDrop(message, messageUser, serv);
                    					is_drop_open = true;
	                    			} else {
	                    				MessageAPI.messageUserNoPerm(message, messageUser);
	                    			}
                    			}
                    		}
                    		
                    		else if(message.getContent().equals("!opendrop name")){
                    			if(is_drop_open){
	                    			if(MessageAPI.isMessageUserRole(message, dropper, primedropper, serv)){
	                    				openDropAlreadyName(message, messageUser);
	                    			} else {
	                    				MessageAPI.messageUserNoPerm(message, messageUser);
	                    			}
                    			} else {
                    				if(MessageAPI.isMessageUserRole(message, dropper, primedropper, serv)){
                    					openDropName(message, messageUser, serv);
	                    				is_drop_open = true;
	                    				
	                    			} else {
	                    				MessageAPI.messageUserNoPerm(message, messageUser);
	                    			}
                    			}
                    		}
                    		
                    		else if(message.getContent().contains("!add")){
                    			String number;
                    			if(MessageAPI.isMessageUserRole(message, dropper, primedropper, serv)){
                    				if(message.getContent().contains(" ")){
	                    				number = message.getContent().substring(message.getContent().indexOf(' ')+ 1).trim();
	                    				if(is_drop_open){
	                    					String sendname = active_droppers.get(messageUserId);
		                    					if(sendname.equals("true")){
		                    						acceptUserSendName(message, messageUser, number, serv);
		                    					} else {
		                    						acceptUser(message, messageUser, number, serv);
		                    					}
	                    				} else {
	                    					messageUser.sendMessage("No open drop to add users to.");
	                    					message.delete();
	                    				}
                    				} else {
                    					messageUser.sendMessage("You have to use `!add x`.");
                    					message.delete();
                    				}
                    			} else {
                    				MessageAPI.messageUserNoPerm(message, messageUser);
                    			}
                    		}
                    		
                    		else if(message.getContent().equals("!closedrop")){
                				if(MessageAPI.isMessageUserRole(message, dropper, primedropper, serv)){
                					if(active_droppers.containsKey(messageUserId)){
                						if(is_drop_open){
		                    				closeDrop(message, messageUser, serv);
                						} else {
                							messageUser.sendMessage(string_drop_already_closed);
		                    				message.delete();
                						}
                					} else {
		                    			messageUser.sendMessage("You aren't an active Dropper. No open drop to close.");
		                    			message.delete();
                					}
                				} else {
                					MessageAPI.messageUserNoPerm(message, messageUser);
                				}
                    		}
                    	
                    		else if(message.getContent().contains("!commands")){
                    			String check;
                    			if(message.getContent().contains(" ")){
                    				check = message.getContent().substring(message.getContent().indexOf(' ')+ 1).trim();
                    				
                    				if(check.equals("dropper")){
	                    				if(MessageAPI.isMessageUserRole(message, dropper, primedropper, admin, primeadmin, serv)){
	                    					messageUser.sendMessage("Here is the commands list for droppers:"
	                    							+ "\n - In #bot-commands - "
	                    							+ "\n \t `!register_drop yoursc` - will register you into the dropper system, with yoursc being your in-game username."
	    											+ "\n"
	                    							+ "\n \t `!chk regdrop` - use this to check what username we have in the system for you."
	    											+ "\n"
	                    							+ "\n \t `!opendrop` - will open a free drop lobby for you"
	    											+ "\n"
	                    							+ "\n \t `!opendrop_send` - will open a free drop lobby, where users get sent your registered username."
	    											+ "\n"
	                    							+ "\n \t `!add x` -  will accept x number of users from the queue."
	    											+ "\n"
	                    							+ "\n \t `!chk q` - will send you how many users are in the current queue."
	    											+ "\n"
	                    							+ "\n \t `!closedrop` - will close your current drop.");
	                    					message.delete();
	                    				} else {
	                    					MessageAPI.messageUserNoPerm(message, messageUser);
	                    				}
	                    			}
                    				else if(check.equals("admin")){
	                    				if(MessageAPI.isMessageUserRole(message, admin, primeadmin, guardian, primeguardian, serv)){
	                    					messageUser.sendMessage("Here is the commands list for admins:"
	                    							+ "\n - In #blacklist - "
	                    							+ "\n \t `!bl @user` - adds that user to the blacklist."
	    											+ "\n"
	                    							+ "\n \t `!unbl @user` - removes that user from the blacklist."
	    											+ "\n"
	                    							+ "\n \t `!str @user` - adds a strike to that user."
	    											+ "\n"
	                    							+ "\n \t `!res_str @user` - resets all strikes for that user."
	    											+ "\n"
	                    							+ "\n \t `!res_drop @user` - resets that users drop count."
	    											+ "\n"
	                    							+ "\n - In #bot-commands - "
	    											+ "\n"
	                    							+ "\n \t `!removeuser username` - removes that user from the current queue.");
	                    					message.delete();
	                    				} else {
	                    					MessageAPI.messageUserNoPerm(message, messageUser);
	                    				}
                    				}
                    				else if(check.equals("help")) {
                    					messageUser.sendMessage("Here is the list of `!commands`:"
                    							+ "\n \t `!commands dropper` - Lists all Dropper specific commands."
    											+ "\n"
                    							+ "\n \t `!commands admin` - Lists all Admin specific commands."
                    							);
                    				}
	                    		} else {
                    				messageUser.sendMessage("Here is the commands list:"
                    						+ "\n \t `!register yoursc` - will register you into our system, with yoursc being your in-game username."
											+ "\n"
                    						+ "\n \t `!chk reg` - use this to check what username we have in the system for you."
											+ "\n"
                    						+ "\n \t `!join` - if a drop is open, this will add you to the current queue."
											+ "\n"
                    						+ "\n \t `!pos` - if you are in the queue, this will send you your position in the queue."
											+ "\n"
                    						+ "\n \t `!leave` - if you are in the queue, and need to go offline, use this to leave the queue.");
                    				message.delete();
                				}
                    		}
                    		
                    		else if(message.getContent().contains("!chk")){
                    			String check;
                    			if(message.getContent().contains(" ")){
                    				check = message.getContent().substring(message.getContent().indexOf(' ')+ 1).trim();
                    				if(check.equals("reg")){
                    					for(int i = 0; i < user_array.length(); i++){
                            				JSONObject username = user_array.getJSONObject(i);
                            				if(username.get("User_Id").equals(messageUserId)){
                            						String gtaName = username.getString("Social_Club");
                            					
                            						message.delete();
                            						messageUser.sendMessage("You are currently registered as: `" + gtaName + "`.");
                            					}
                    					}
                    				}
                    				if(MessageAPI.isMessageUserRole(message, dropper, primedropper, serv)){
                        				if(check.equals("regdrop")){
                        					for(int i = 0; i < dropper_array.length(); i++){
                                				JSONObject username = dropper_array.getJSONObject(i);
                                				if(username.get("User_Id").equals(messageUserId)){
                                					String gtaName = username.getString("Social_Club");
                                					
                                					message.delete();
                                					messageUser.sendMessage("You are currently registered as a dropper: `" + gtaName + "`.");
                                				}
                        					}
                        				}
                        				else if(check.equals("q")){
                        					if(queue_array.length() == 0) {
                        						messageUser.sendMessage("There are currently: " + queue_array.length() + " users in the queue.");
                        					}
                        					else if(queue_array.length() == 1){
                        						messageUser.sendMessage("There is currently: " + queue_array.length() + " user in the queue.");
                        					} else {
                        						messageUser.sendMessage("There are currently: " + queue_array.length() + " users in the queue.");
                        					}
                        				}
                    				}
                    			}
                    		}
                    		
                    		
                    		
                    		message.delete();
                    	}
                    }
                });
            }

            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
	}
	
	public static void removeUserFromQueueAdmin(Message message, User user, String check, Server serv){
		for(int i = 0; i < queue_array.length(); i++){
            JSONObject test = queue_array.getJSONObject(i);
                if(test.getString("Discord_Name").equals(check)){
                	String discordName = test.getString("Discord_Name");
                	String userId = test.getString("User_Id");
                	
                	queue_array.remove(i);
                	user.sendMessage("User: `" + discordName + "` has been removed from the queue.");
                	message.delete();
                	
                	User removed = serv.getMemberById(userId);
                	removed.sendMessage("You have been removed from the current queue.");
                } else {
                	user.sendMessage("User is not in the current queue.");
                	message.delete();
                }
		}
	}
	
	public static void removeUserFromQueue(Message message, User user){
		for(int i = 0; i < queue_array.length(); i++){
            JSONObject test = queue_array.getJSONObject(i);
                if(test.getString("Discord_Name").equals(user.getName())){
                	queue_array.remove(i);
                	user.sendMessage("You have been removed from the current queue.");
                	message.delete();
                }
		}
	}
	
	public static void addUserToQueue(Message message, User user){
		boolean added = false;
		if(user_array.length() > 0){
			for(int i = 0; i < queue_array.length(); i++){
	            JSONObject test = queue_array.getJSONObject(i);
	                if(test.getString("User_Id").equals(user.getId())){
	                	user.sendMessage("You're already in the current queue.");
	                	message.delete();
	           			added = true;
	           			break;
	                }
			}
			
			if (!added) {
				for(int i = 0; i < user_array.length(); i++){
					JSONObject test = user_array.getJSONObject(i);
					if(test.getString("User_Id").equals(user.getId())){
						String discordName = test.getString("Discord_Name");
						String GTAName = test.getString("Social_Club");
						Integer drops = test.getInt("Drops");
						Integer strikes = test.getInt("Strikes");
						if(drops > 6){
							user.sendMessage("You need to cool off! You've been in too many drops lately!");
							message.delete();
						} 
						else if (drops <= 6){
						
			           	JSONObject item = new JSONObject();
			   			item.put("Discord_Name", discordName);
			   			item.put("Social_Club", GTAName);
			   			item.put("User_Id", user.getId());
			   			item.put("Drops", test.getInt("Drops"));
			   			item.put("Strikes", strikes);
			        	
						queue_array.put(item);
						user.sendMessage("You've been added to the current queue.");
						message.delete();
			            System.out.println("Added user to queue" + " | " + discordName + " | " + GTAName + " | " + user.getId() + " | Drops: " + drops + " |");
			            
						}
					}
				}
			}
		} 
		else if(user_array.length() <= 0){
			user.sendMessage("You aren't currently registered! Use !register your sc in #bot-commands to register!");
			message.delete();
		}
	}
	
	public static void acceptUser(Message message, User user, String number, Server serv){
		int numberU = 0;
		try{
		    numberU = Integer.valueOf(number);
		} catch(Exception ignored) {
		    user.sendMessage("When using `!add x`, x has to be a number.");
		    message.delete();
		}
		if(numberU < 10){
			if(queue_array.length() >= numberU){
				if(queue_array.length() > 0){
					for(int i = 0; i < numberU; i++){
						JSONObject test = queue_array.getJSONObject(0);
						BotUtils.sleep(5);
						accepted_array.put(test);
						BotUtils.sleep(5);
						queue_array.remove(0);
						
						String discordName = test.getString("Discord_Name");
						String GTAName = test.getString("Social_Club");
						String userId = test.getString("User_Id");
						Integer drops = test.getInt("Drops");
						Integer strikes = test.getInt("Strikes");
						
						user.sendMessage("Accepted: `" + discordName + "` | SC: `" + GTAName + "` | Drops: `" + drops + "` | Strikes: `" + strikes + "` |");
						message.delete();
			            System.out.println("Accepted: " + " | " + discordName + " | " + GTAName + " | " + userId + " | Drops: " + drops + " | Strikes: " + strikes + " |");
			            
			            User accept = serv.getMemberById(userId);
			            accept.sendMessage("You have been accepted to " + user.getMentionTag() + "'s free drop lobby. They will add you in-game.");
					
			            for(int t = 0; t < user_array.length(); t++){
							JSONObject useracc = user_array.getJSONObject(t);
							if(useracc.getString("User_Id").equals(userId)){
								String discordName2 = useracc.getString("Discord_Name");
								String gtaName2 = useracc.getString("Social_Club");
								String userId2 = useracc.getString("User_Id");
								Integer drops2 = useracc.getInt("Drops");
								Integer strikes2 = useracc.getInt("Strikes");
								
								JSONObject item = new JSONObject();
								item.put("Discord_Name", discordName2);
								item.put("Social_Club", gtaName2);
								item.put("User_Id", userId2);
								item.put("Drops", drops2 + 1);
								item.put("Strikes", strikes2);
								
								user_array.put(i, item);
							}
						}
					}
					
					Channel announce = serv.getChannelById("269111444526727170");
					
					if(Integer.parseInt(number) > 1){
						announce.sendMessage("`" + number + " users have been accepted into a free drop lobby.`");
					} else {
						announce.sendMessage("`" + number + " user has been accepted into a free drop lobby.`");
					}
					
					/*DropAPI.sleep(5);
					user.sendMessage("You accepted: " + numberU + " users from the queue. There are: " + queue_array.length() + " users left in the current queue.");
					*/
				}else {
					user.sendMessage("No users in queue.");
					message.delete();
				}
			} else {
				user.sendMessage("Not enough users in queue.");
				message.delete();
			}
		} else {
			user.sendMessage("You tried to accept too many users!");
			message.delete();
		}

	}
	
	public static void acceptUserSendName(Message message, User user, String number, Server serv){
		int numberU = 0;
		try{
		    numberU = Integer.valueOf(number);
		} catch(Exception ignored) {
		    user.sendMessage("When using `!add x`, x has to be a number.");
		    message.delete();
		}
		if(!(numberU == 0)){
			if(numberU < 10){
				if(queue_array.length() >= numberU){
					if(user_array.length() > 0){
						String dropperSocial = "";
						for(int i = 0; i < dropper_array.length(); i++){
							JSONObject dropper = dropper_array.getJSONObject(i);
							if(dropper.getString("User_Id").equals(user.getId())){
								dropperSocial = dropper.getString("Social_Club");
							}
						}
						for(int i = 0; i < numberU; i++){
							JSONObject test = queue_array.getJSONObject(0);
							BotUtils.sleep(5);
							accepted_array.put(test);
							BotUtils.sleep(5);
							queue_array.remove(0);
							
							String discordName = test.getString("Discord_Name");
							String GTAName = test.getString("Social_Club");
							String userId = test.getString("User_Id");
							Integer drops = test.getInt("Drops");
							Integer strikes = test.getInt("Strikes");
							
							user.sendMessage("Accepted: `" + discordName + "` | SC: `" + GTAName + "` | Drops: `" + drops + "` | Strikes: `" + strikes + "` |");
							message.delete();
				            System.out.println("Accepted: " + " | " + discordName + " | " + GTAName + " | " + userId + " | Drops: " + drops + " | Strikes: " + strikes + " |");
				            
				            User accept = serv.getMemberById(userId);
				            accept.sendMessage("You have been accepted to " + user.getMentionTag() + "'s free drop lobby. Their social club is: `" + dropperSocial +  "` add them in-game.");
						
							for (int t = 0; t < user_array.length(); t++) {
								JSONObject useracc = user_array.getJSONObject(t);
								if (useracc.getString("User_Id").equals(userId)) {
									String discordName2 = useracc.getString("Discord_Name");
									String gtaName2 = useracc.getString("Social_Club");
									String userId2 = useracc.getString("User_Id");
									Integer drops2 = useracc.getInt("Drops");
									Integer strikes2 = useracc.getInt("Strikes");

									JSONObject item = new JSONObject();
									item.put("Discord_Name", discordName2);
									item.put("Social_Club", gtaName2);
									item.put("User_Id", userId2);
									item.put("Drops", drops2 + 1);
									item.put("Strikes", strikes2);

									user_array.put(i, item);
								}
							}
						}
						
						Channel announce = serv.getChannelById("269111444526727170");
						
						if(Integer.parseInt(number) > 1){
							announce.sendMessage("`" + number + " users have been accepted into a free drop lobby.`");
						} else {
							announce.sendMessage("`" + number + " user has been accepted into a free drop lobby.`");
						}
						
						/*DropAPI.sleep(5);
						user.sendMessage("You accepted: " + numberU + " users from the queue. There are: " + queue_array.length() + " users left in the current queue.");
						*/
					}else {
						user.sendMessage("No users in queue.");
						message.delete();
					}
				} else {
					user.sendMessage("Not enough users in queue.");
					message.delete();
				}
			} else {
				user.sendMessage("You tried to accept too many users!");
				message.delete();
			}
		} else {
			user.sendMessage("You cannot add 0 users!");
			message.delete();
		}
	}
	
	public static void registerUser(Message message, User user, String gtaName) throws IOException{
		boolean replaced = false;
		
		String discordName = user.getName();
		JSONObject item = new JSONObject();
		item.put("Discord_Name", discordName);
		item.put("Social_Club", gtaName);
		item.put("User_Id", user.getId());
		item.put("Drops", 0);
		item.put("Strikes", 0);
		
		//if(checkUserIsReged(user)){
			for(int i = 0; i < user_array.length(); i++){
	            JSONObject test = user_array.getJSONObject(i);
	                if(test.getString("User_Id").equals(user.getId())){
	                   int drops = test.getInt("Drops");
	                   int strikes = test.getInt("Strikes");
	                	
	                   JSONObject item2 = new JSONObject();
	                   item2.put("Discord_Name", discordName);
	                   item2.put("Social_Club", gtaName);
	                   item2.put("User_Id", user.getId());
	                   item2.put("Drops", test.getInt("Drops"));
	                   item2.put("Strikes", test.getInt("Strikes"));
	                   
	                   System.out.println("Re-added User" + " | " + discordName + " | " + gtaName + " | " + user.getId() + " | Drops: " + drops + " | Strikes: " + strikes);
	                   message.getAuthor().sendMessage("Successfully re-registered as: `" + gtaName + "`.");
	                   user_array.put(i, item2);
	                   saveSystem();
	                   replaced = true;
	                   break;
	                }
			//}
		}
		if (!replaced) {
			user_array.put(item);
            System.out.println("Added User" + " | " + discordName + " | " + gtaName + " | " + user.getId() + " |");
            message.getAuthor().sendMessage("Successfully registered as: `" + gtaName + "`.");
            saveSystem();
		}
	}
	
	public static void deregisterUser(Message message, User messageUser){
		if(checkUserIsReged(messageUser)){
			for(int i = 0; i < user_array.length(); i++){
	            JSONObject test = user_array.getJSONObject(i);
	                if(test.getString("User_Id").equals(messageUser.getId())){
	                   String discordName = test.getString("Discord_Name");
	                   String gtaName = test.getString("Social_Club");
	                   
	                   System.out.println("Removed User: " + " | " + discordName + " | " + gtaName + " | " + messageUser.getId() + " |");
	                   message.getAuthor().sendMessage("Successfully de-registered.");
	                   user_array.remove(i);
	                   saveSystem();
	                }
			} 
		} else {
			messageUser.sendMessage("You haven't registered yet!");
			message.delete();
		}
		message.delete();
	}
	
	public static void saveUsers() throws IOException{
		 File users = new File("users.txt");
		 JSONArray a = user_array;
		 String jsonstr = a.toString(2);
		 BotUtils.writeFile(jsonstr, users, Charsets.UTF_8);
	}
	
	public static void loadUsers() throws IOException{
		File users = new File("users.txt");
		String usersInfo = BotUtils.readFile(users, Charsets.UTF_8);
		user_array = new JSONArray();
		
		JSONArray a = new JSONArray(usersInfo);
		for (int i=0; i<a.length(); i++) {
		    JSONObject item = a.getJSONObject(i);
		    
		    user_array.put(item);
		}
	}
	
	public static void saveDropper(Message message, User user, String gtaName) throws IOException{
		boolean replaced = false;
		
		JSONObject item = new JSONObject();
		String discordName = user.getName();
		item.put("Discord_Name", user.getName());
		item.put("Social_Club", gtaName);
		item.put("User_Id", user.getId());
		
		for(int i = 0; i < dropper_array.length(); i++){
            JSONObject test = dropper_array.getJSONObject(i);
                if(test.getString("User_Id").equals(user.getId())){
                   System.out.println("Re-added Dropper" + " | " + discordName + " | " + gtaName + " | " + user.getId() + " |");
                   message.getAuthor().sendMessage("Successfully re-registered as dropper: `" + gtaName + "`.");
                   dropper_array.put(i, item);
                   replaced = true;
                   saveSystem();
                   break;
                }
		} 
		if (!replaced) {
			dropper_array.put(item);
            System.out.println("Added Dropper" + " | " + discordName + " | " + gtaName + " | " + user.getId() + " |");
            message.getAuthor().sendMessage("Successfully registered as dropper: `" + gtaName + "`.");
            saveSystem();
		}
	}
	
	public static void deleteDropper(Message message, User messageUser){
		if(checkDropperIsReged(messageUser)){
			for(int i = 0; i < dropper_array.length(); i++){
	            JSONObject test = dropper_array.getJSONObject(i);
	                if(test.getString("User_Id").equals(messageUser.getId())){
	                   String discordName = test.getString("Discord_Name");
	                   String gtaName = test.getString("Social_Club");
	                   
	                   System.out.println("Removed Dropper: " + " | " + discordName + " | " + gtaName + " | " + messageUser.getId() + " |");
	                   message.getAuthor().sendMessage("Successfully de-registered.");
	                   dropper_array.remove(i);
	                   saveSystem();
	                }
			} 
		} else {
			messageUser.sendMessage("You haven't registered yet!");
			message.delete();
		}
		message.delete();
	}
	
	public static void saveDroppers() throws IOException{
		File droppers = new File("droppers.txt");
		 JSONArray a = dropper_array;
		 String jsonstr = a.toString(2);
		 BotUtils.writeFile(jsonstr, droppers, Charsets.UTF_8);
	}
	
	public static void loadDroppers() throws IOException{
		File droppers = new File("droppers.txt");
		String droppersInfo = BotUtils.readFile(droppers, Charsets.UTF_8);
		dropper_array = new JSONArray();
		
		JSONArray a = new JSONArray(droppersInfo);
		for (int i=0; i<a.length(); i++) {
		    JSONObject item = a.getJSONObject(i);
		    dropper_array.put(item);
		}
	}
	
	public static void saveSettings() throws IOException{
		File settings_file = new File("settings.txt");
		
		JSONArray settings = settings_array;
		String jsonstr = settings.toString(2);
		BotUtils.writeFile(jsonstr, settings_file, Charsets.UTF_8);
	}
	
	public static void loadSettings() throws IOException{
		File settings = new File("settings.txt");
		String settingsInfo = BotUtils.readFile(settings, Charsets.UTF_8);
		
		JSONArray a = new JSONArray(settingsInfo);
		for (int i=0; i<a.length(); i++) {
		    JSONObject item = a.getJSONObject(i);
		    settings_array.put(item);
		}
	}
	
	public static void saveBlacklist() throws IOException {
		File blacklist = new File("blacklist.txt");
		
		String jsonstr = blacklist_array.toString(2);
		BotUtils.writeFile(jsonstr, blacklist, Charsets.UTF_8);
	}
	
	public static void loadBlacklist() throws IOException {
		File blacklist = new File("blacklist.txt");
		String blacklistInfo = BotUtils.readFile(blacklist, Charsets.UTF_8);
		blacklist_array = new JSONArray();
		
		JSONArray a = new JSONArray(blacklistInfo);
		for(int i = 0; i < a.length(); i++){
			JSONObject item = a.getJSONObject(i);
			blacklist_array.put(item);
		}
	}
	
    public static void checkUserInfo(String userName, User messageUser){
    	for(int i = 0; i < user_array.length(); i++){
    		JSONObject user = user_array.getJSONObject(i);
    		if(user.get("Discord_Name").equals(userName)){
    			String gtaName = user.getString("Social_Club");
    			//String userId = user.getString("User_Id");
    			int drops = user.getInt("Drops");
    			int strikes = user.getInt("Strikes");
    			
    			messageUser.sendMessage("User Check complete:       | `" + userName + "` | SC: `" + gtaName + "` | Drops: `" + drops + "` | Strikes: `" + strikes + "` |");
    		}
    	}
    	if(checkDropperIsRegedName(userName)) {
	    	for(int i = 0; i < dropper_array.length(); i++) {
	    		JSONObject user = dropper_array.getJSONObject(i);
	    		if(user.get("Discord_Name").equals(userName)) {
	    			String gtaName = user.getString("Social_Club");
	    			
	    			messageUser.sendMessage("Dropper check complete: | `" + userName + "` | SC: `" + gtaName + "` | Status: `Active Dropper` |");
	    		}
	    	}
    	} else {
    		messageUser.sendMessage("Dropper check complete: | `" + userName + "` | Status: " + "`Standard user.`|");
    	}
    }
    
    public static void clearUserDrops(String userName){
    	for(int i = 0; i < user_array.length(); i++){
    		JSONObject user = user_array.getJSONObject(i);
    		if(user.getString("Discord_Name").equals(userName)){
    			String discordName = user.getString("Discord_Name");
	    		String gtaName = user.getString("Social_Club");
	    		String userId = user.getString("User_Id");
	    		Integer drops = 0;
	    		Integer strikes = user.getInt("Strikes");
	    		
	    		JSONObject newUser = new JSONObject();
	    		newUser.put("Discord_Name", discordName);
	    		newUser.put("Social_Club", gtaName);
	    		newUser.put("User_Id", userId);
	    		newUser.put("Drops", drops);
	    		newUser.put("Strikes", strikes);
	    		
	    		user_array.put(i, newUser);
    		}
    	}
    }
    
    public static void clearUserDrops(User user){
    	for(int i = 0; i < user_array.length(); i++){
    		JSONObject userObj = user_array.getJSONObject(i);
    		if(userObj.getString("Discord_Name").equals(user.getName())){
    			String discordName = userObj.getString("Discord_Name");
	    		String gtaName = userObj.getString("Social_Club");
	    		String userId = userObj.getString("User_Id");
	    		Integer drops = 0;
	    		Integer strikes = userObj.getInt("Strikes");
	    		
	    		JSONObject newUser = new JSONObject();
	    		newUser.put("Discord_Name", discordName);
	    		newUser.put("Social_Club", gtaName);
	    		newUser.put("User_Id", userId);
	    		newUser.put("Drops", drops);
	    		newUser.put("Strikes", strikes);
	    		
	    		user_array.put(i, newUser);
    		}
    	}
    }
    
    public static void clearUserStrikes(String userName){
    	for(int i = 0; i < user_array.length(); i++){
    		JSONObject user = user_array.getJSONObject(i);
    		if(user.getString("Discord_Name").equals(userName)){
    			String discordName = user.getString("Discord_Name");
	    		String gtaName = user.getString("Social_Club");
	    		String userId = user.getString("User_Id");
	    		Integer drops = user.getInt("Drops");
	    		Integer strikes = 0;
	    		
	    		JSONObject newUser = new JSONObject();
	    		newUser.put("Discord_Name", discordName);
	    		newUser.put("Social_Club", gtaName);
	    		newUser.put("User_Id", userId);
	    		newUser.put("Drops", drops);
	    		newUser.put("Strikes", strikes);
	    		
	    		user_array.put(i, newUser);
    		}
    	}
    }
    
    public static void clearUserStrikes(User user){
    	for(int i = 0; i < user_array.length(); i++){
    		JSONObject userObj = user_array.getJSONObject(i);
    		if(userObj.getString("Discord_Name").equals(user.getName())){
    			String discordName = userObj.getString("Discord_Name");
	    		String gtaName = userObj.getString("Social_Club");
	    		String userId = userObj.getString("User_Id");
	    		Integer drops = userObj.getInt("Drops");
	    		Integer strikes = 0;
	    		
	    		JSONObject newUser = new JSONObject();
	    		newUser.put("Discord_Name", discordName);
	    		newUser.put("Social_Club", gtaName);
	    		newUser.put("User_Id", userId);
	    		newUser.put("Drops", drops);
	    		newUser.put("Strikes", strikes);
	    		
	    		user_array.put(i, newUser);
    		}
    	}
    }
    
    public static void clearAllStrikes(){
    	for(int i = 0; i < user_array.length(); i++){
    		JSONObject user = user_array.getJSONObject(i);
    		if(!(user.getString("Discord_Name").equals(null))){
	    		String discordName = user.getString("Discord_Name");
	    		String gtaName = user.getString("Social_Club");
	    		String userId = user.getString("User_Id");
	    		Integer drops = user.getInt("Drops");
	    		Integer strikes = 0;
	    		
	    		JSONObject newUser = new JSONObject();
	    		newUser.put("Discord_Name", discordName);
	    		newUser.put("Social_Club", gtaName);
	    		newUser.put("User_Id", userId);
	    		newUser.put("Drops", drops);
	    		newUser.put("Strikes", strikes);
	    		
	    		user_array.put(i, newUser);
    		}
    	}
    }
    
    public static void clearAllDropCounts(){
    	for(int i = 0; i < user_array.length(); i++){
    		JSONObject user = user_array.getJSONObject(i);
    		if(!(user.getString("Discord_Name").equals(null))){
	    		String discordName = user.getString("Discord_Name");
	    		String gtaName = user.getString("Social_Club");
	    		String userId = user.getString("User_Id");
	    		Integer drops = 0;
	    		Integer strikes = user.getInt("Strikes");
	    		
	    		JSONObject newUser = new JSONObject();
	    		newUser.put("Discord_Name", discordName);
	    		newUser.put("Social_Club", gtaName);
	    		newUser.put("User_Id", userId);
	    		newUser.put("Drops", drops);
	    		newUser.put("Strikes", strikes);
	    		
	    		user_array.put(i, newUser);
    		}
    	}
    }
    
    public static void checkUsersStrikes(Message message, User user, User messageUser){
    	for(int i = 0; i < user_array.length(); i++){
    		JSONObject userObj = user_array.getJSONObject(i);
    		if(userObj.get("Discord_Name").equals(user.getName())){
    			Integer strikes = userObj.getInt("Strikes");
    			
    			messageUser.sendMessage("User: `" + user.getName() + "` has `" + strikes + "` strikes.");
    			message.delete();
    		}
    	}
    }
	
    public static void clearuser_array(){
    	user_array = new JSONArray();
    	System.out.println(" - Userbase purged - ");
    }
    
    public static void cleardropper_array(){
    	dropper_array = new JSONArray();
    	System.out.println(" - Dropperbase Purged - ");
    }
    
    public static void clearAcceptArray(){
    	accepted_array = new JSONArray();
    	System.out.println(" - Accepted Users Purged - ");
    }
    
    public static void clearBlacklist(){
    	blacklist_array = new JSONArray();
    	System.out.println(" - Blacklist Purged - ");	
    }
    
   public static void closeQueue(){
	   queue_array = new JSONArray();
	   System.out.println(" - Queue Closed - ");
   }
   
   public static void dropperUpdateQueue(Message message){
	   message.getAuthor().sendMessage("There are currently: " + queue_array.length() + " users in the queue");
   }
   
   public static boolean checkQPosAndSend(Message message, User messageUser){
	   boolean queue = false;
	   for(int i = 0; i < queue_array.length(); i++){
           JSONObject test = queue_array.getJSONObject(i);
               if(test.getString("User_Id").equals(messageUser.getId())){
               	messageUser.sendMessage("You are position: " + (i + 1) + " in the current queue.");
               	message.delete();
               	queue = true;
               }
		}
	   return queue;
   }
   
   public static boolean checkUserIsInQueue(Message message, User user){
	   boolean is = false;
	   for(int i = 0; i <queue_array.length(); i++){
		   JSONObject userObj = queue_array.getJSONObject(i);
		   if(userObj.getString("User_Id").equals(user.getId())){
			   is = true;
		   }
	   }
	   return is;
   }
   
   public static boolean checkDropperIsReged(User messageUser){
	 boolean registered= false;
 		for(int i = 0; i < dropper_array.length(); i++){
             JSONObject test = dropper_array.getJSONObject(i);
                 if(test.getString("User_Id").equals(messageUser.getId())){
                	 registered = true;
                 }
 		}
 		return registered;
   }
   
   public static boolean checkDropperIsRegedName(String username){
		 boolean registered= false;
	 		for(int i = 0; i < dropper_array.length(); i++){
	             JSONObject test = dropper_array.getJSONObject(i);
	                 if(test.getString("Discord_Name").equals(username)){
	                	 registered = true;
	                 }
	 		}
	 		return registered;
	   }
   
   public static boolean checkUserIsRegedUserName(String username){
	   boolean registered = false;
	   	for(int i = 0; i < user_array.length(); i++){
	   		JSONObject test = user_array.getJSONObject(i);
	   		if(test.getString("Discord_Name").equals(username)){
	   			registered = true;
	   		}
	   	}
	   	return registered;
   }
   
   public static boolean checkUserIsReged(User messageUser){
	   boolean registered= false;
		for(int i = 0; i < user_array.length(); i++){
            JSONObject test = user_array.getJSONObject(i);
                if(test.getString("User_Id").equals(messageUser.getId())){
               	 registered = true;
                }
		}
		return registered;
   }
   
   public static boolean checkUserIsBlacked(User user){
	   boolean blacked = false;
	   if(blacklist_array.length() > 0){
		   for(int i = 0; i < blacklist_array.length(); i++){
			   JSONObject test = blacklist_array.getJSONObject(i);
			   if(test.getString("User_Id").equals(user.getId())){
				   blacked = true;
			   } else {
				   blacked = false;
			   }
		   }
	   } else {
		   blacked = false;
	   }
	   return blacked;
   }
   
	public static void openDropAlready(Message message, User messageUser){
		if(active_droppers.get(messageUser.getId()) != null){
			messageUser.sendMessage("You are already dropping!");
			message.delete();
		} else {
			active_droppers.put(messageUser.getId(), messageUser.getName());
			
			messageUser.sendMessage("Nice! Opening a free drop lobby for " + messageUser.getMentionTag() + ".\n \t You will have to add users, with the SC names I give you. \n \t Use `!add X` to add X users to the drop lobby. \n \t Use `!chk q` to see the current queue length.");
			BotUtils.sleep(2);
			messageUser.sendMessage("There is already a free drop lobby open. No messages sent.");
			
			BotUtils.sleep(100);
			//messageUser.sendMessage("There are currently " + queue_array.length() + " users in the queue.");
			dropperUpdateQueue(message);
			message.delete();
		}
	}
	
	public static void openDropAlreadyName(Message message, User messageUser){

		if(active_droppers.get(messageUser.getId()) != null){
			messageUser.sendMessage("You are already dropping!");
			message.delete();
		}
		else if(checkDropperIsReged(messageUser)){
		active_droppers.put(messageUser.getId(), "true");
		
		messageUser.sendMessage("Nice! Opening a free drop lobby for " + messageUser.getMentionTag() + ".\n \t Your SC name will be given to users and they will add you in-game. \n \t Use `!add X` to add X users to the drop lobby. \n \t Use `!chk q` to see the current queue length.");
		BotUtils.sleep(2);
		messageUser.sendMessage("There is already a free drop lobby open. No messages sent.");
		
		BotUtils.sleep(40);
		//messageUser.sendMessage("There are currently " + queue_array.length() + " users in the queue.");
		dropperUpdateQueue(message);
		message.delete();
		} else {
			messageUser.sendMessage("You need to register in #bot-commands with !registerdrop yoursc, before opening a drop lobby.");
			message.delete();
		}
	}
	
	public static void openDrop(Message message, User messageUser, Server serv) {
		Channel announce = serv.getChannelById(announcement_id);
		if (active_droppers.get(messageUser.getId()) != null) {
			messageUser.sendMessage("You are already dropping!");
			message.delete();
		} else if (checkDropperIsReged(messageUser)) {
			active_droppers.put(messageUser.getId(), "false");

			messageUser.sendMessage("Nice! Opening a free drop lobby for " + messageUser.getMentionTag() + ".\n \t You will have to add users, with the SC names I give you. \n \t Use `!add X` to add X users to the drop lobby. \n \t Use `!chk q` to see the current queue length.");

			announce.sendMessage("@here" + " - A free drop lobby has been opened! Join the queue by using: `!join " + drop_entry_name + "` in `#bot-commands`.");
			
			BotUtils.sleep(40);
			//messageUser.sendMessage("There are currently " + queue_array.length() + " users in the queue.");
			dropperUpdateQueue(message);
			message.delete();
		} else {
			messageUser.sendMessage("You need to register in #bot-commands with !register_drop yoursc, before opening a drop lobby.");
			message.delete();
		}
	}

	public static void openDropName(Message message, User messageUser, Server serv) {
		Channel announce = serv.getChannelById(announcement_id);

		if (active_droppers.get(messageUser.getId()) != null) {
			messageUser.sendMessage("You are already dropping!");
			message.delete();
		} else if (checkDropperIsReged(messageUser)) {
			active_droppers.put(messageUser.getId(), "true");

			messageUser.sendMessage("Nice! Opening a free drop lobby for " + messageUser.getMentionTag() + ".\n \t Your SC name will be given to users and they will add you in-game. \n \t Use `!add X` to add X users to the drop lobby. \n \t Use `!chk q` to see the current queue length.");

			announce.sendMessage("@here" + " - A free drop lobby has been opened! Join the queue by using: `!join " + drop_entry_name + "` in `#bot-commands`.");

			BotUtils.sleep(40);
			//messageUser.sendMessage("There are currently " + queue_array.length() + " users in the queue.");
			dropperUpdateQueue(message);
			message.delete();
		} else {
			messageUser.sendMessage("You need to register in #bot-commands with !register_drop yoursc, before opening a drop lobby.");
			message.delete();
		}
	}
	
	public static void closeDrop(Message message, User messageUser, Server serv){
		Channel announce = serv.getChannelById(announcement_id);
		
		messageUser.sendMessage("Closing your free drop lobby, " + messageUser.getMentionTag() + ".");
		
		if(active_droppers.size() <= 1){
		is_drop_open = false;
		announce.sendMessage("@here - This free drop lobby is now CLOSED. Try again next time! Monitor `#bot-info` for the next drop!");
		BotUtils.sleep(100);
		//announce.sendMessage("@here - The Queue has now closed. Try again next time!");
		
		active_droppers.remove(messageUser.getId());
		
		message.delete();
		}
		if(active_droppers.size() > 1){
			messageUser.sendMessage("Another dropper is dropping. You're now offline, but the queue will continue.");
			active_droppers.remove(messageUser.getId());
			message.delete();
		} else {
			closeQueue();
			clearAcceptArray();
			message.delete();
		}
	}
	
	public static void savePrefix() {
		try {
			DropNameAPI.savePrefix();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveSuffix() {
		try {
			DropNameAPI.saveSuffix();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveSystem(){
		try {
			saveDroppers();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			saveUsers();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			saveBlacklist();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			saveSettings();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadSystem(){
		try {
			loadDroppers();
		} catch (IOException e) {
			e.printStackTrace();
		} try {
			loadUsers();
		} catch (IOException e) {
			e.printStackTrace();
		} try {
			loadBlacklist();
		} catch (IOException e) {
			e.printStackTrace();
		} try {
			loadSettings();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void shutdown(DiscordAPI api) throws IOException{
		System.out.println(" - Shutting down - ");
		saveSystem();
		closeQueue();
		BotUtils.sleep(5);
		api.disconnect();
		BotUtils.sleep(10);
		System.exit(0);
	}
	
}
