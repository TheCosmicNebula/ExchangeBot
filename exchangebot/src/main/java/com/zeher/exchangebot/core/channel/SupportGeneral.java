package com.zeher.exchangebot.core.channel;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zeher.exchangebot.core.ExchangeBotCore;

import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;

public class SupportGeneral {
	
	public static void main(Message message, User messageUser, JSONArray dropper_array, JSONArray user_array) {
		System.out.println("WORKING");
		if (message.getContent().contains(" ") || message.getContent().contains("")) {
			String check;
			check = message.getContent().substring(message.getContent().indexOf(' ') + 1).trim();
			for (int i = 0; i < dropper_array.length(); i++) {
				JSONObject dropperObj = dropper_array.getJSONObject(i);
				if (dropperObj.get("Social_Club").equals(check)) {
					System.out.println(
							"User: " + messageUser.getName() + " tried to type another users Social_Club username.");
					message.delete();
					message.reply(ExchangeBotCore.string_no_social);
				}
			}
			for (int i = 0; i < user_array.length(); i++) {
				JSONObject userObj = user_array.getJSONObject(i);
				if (userObj.getString("Social_Club").equals(check)) {
					System.out.println(
							"User " + messageUser.getName() + " tried to type another users Social_Club username.");
					message.delete();
					message.reply(ExchangeBotCore.string_no_social);
				}
			}
		}
	}
}
