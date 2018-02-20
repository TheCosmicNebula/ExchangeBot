package com.zeher.exchangebot.core.api;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.base.Charsets;
import com.zeher.exchangebot.core.util.BotUtils;

public class DropNameAPI {

	static JSONArray prefix_array = new JSONArray();
	static JSONArray suffix_array = new JSONArray();

	public static String chooseName() {
		Random rand_pre = new Random();
		int index_pre = rand_pre.nextInt(prefix_array.length());
		
		Random rand_suf = new Random();
		int index_suf = rand_suf.nextInt(suffix_array.length());
		
		JSONObject prefix_obj = prefix_array.getJSONObject(index_pre);
		JSONObject suffix_obj = suffix_array.getJSONObject(index_suf);
		
		String prefix = prefix_obj.getString("prefix");
		String suffix = suffix_obj.getString("suffix");
		
		System.out.println(" - Drop Entry Name Update: " + prefix + suffix + " - ");
		
		return prefix + suffix;
	}

	public static void savePrefix() throws IOException {
		File prefix = new File("prefix.txt");
		JSONArray temp = prefix_array;
		String jsonstr = temp.toString();
		BotUtils.writeFile(jsonstr, prefix, Charsets.UTF_8);
	}

	public static void loadPrefix() throws IOException {
		File prefix = new File("prefix.txt");
		String jsonstr = BotUtils.readFile(prefix, Charsets.UTF_8);
		prefix_array = new JSONArray();

		JSONArray temp = new JSONArray(jsonstr);
		for (int i = 0; i < temp.length(); i++) {
			JSONObject item = temp.getJSONObject(i);
			prefix_array.put(item);
		}
	}
	
	public static void saveSuffix() throws IOException {
		File suffix = new File("suffix.txt");
		JSONArray temp = suffix_array;
		String jsonstr = temp.toString();
		BotUtils.writeFile(jsonstr, suffix, Charsets.UTF_8);
	}

	public static void loadSuffix() throws IOException {
		File suffix = new File("suffix.txt");
		String jsonstr = BotUtils.readFile(suffix, Charsets.UTF_8);
		suffix_array = new JSONArray();

		JSONArray temp = new JSONArray(jsonstr);
		for (int i = 0; i < temp.length(); i++) {
			JSONObject item = temp.getJSONObject(i);
			suffix_array.put(item);
		}
	}
}
