package com.zeher.exchangebot.core.api;

import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.permissions.Role;

public class MessageAPI {
	
	public static boolean userEquals(Message message, User user){
		if(message.getAuthor().equals(user)){
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean userNameEquals(Message message, String userName){
		if(message.getAuthor().getName().equals(userName)){
			return true;
		} else {
			return false;
		}
	}
	
	public static void sendUserPM(User msgU, String msg){
		msgU.sendMessage(msg);
	}
	
	public static boolean messageContentEquals(Message message, String equals){
	    return message.getContent().equals(equals);
	}
	
	public static boolean messageUserEquals(Message message, User user){
		return message.getAuthor().equals(user);
	}
	
	public static boolean messageUserNameEquals(Message message, String name){
		return message.getAuthor().getName().equals(name);
	}
	
	public static boolean isMessageUserRole(Message message, Role role, Server serv){
		User msgAuth = message.getAuthor();
		return msgAuth.getRoles(serv).contains(role);
	}
	 
	public static boolean isMessageUserRole(Message message, Role role1, Role role2, Server serv){
		User msgAuth = message.getAuthor();
		if(msgAuth.getRoles(serv).contains(role1) || msgAuth.getRoles(serv).contains(role2)){
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isMessageUserRole(Message message, Role role1, Role role2, Role role3, Server serv){
		User msgAuth = message.getAuthor();
		if(msgAuth.getRoles(serv).contains(role1) || msgAuth.getRoles(serv).contains(role2) || msgAuth.getRoles(serv).contains(role3)){
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isMessageUserRole(Message message, Role role1, Role role2, Role role3, Role role4, Server serv){
		User msgAuth = message.getAuthor();
		if(msgAuth.getRoles(serv).contains(role1) || msgAuth.getRoles(serv).contains(role2) || msgAuth.getRoles(serv).contains(role3) || msgAuth.getRoles(serv).contains(role4)){
			return true;
		} else {
			return false;
		}
	}
	
	public static void messageUserNoPerm(Message message, User messageUser){
		messageUser.sendMessage("You don't have access to that command.");
		message.delete();
	}
	    
}
