package com.zeher.exchangebot.core.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class BotUtils {
	
	public static void sleep(int time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeFile(String content, File path, Charset encoding) throws IOException {
		Files.write(path.toPath(), content.getBytes(encoding));
	}

	public static String readFile(File path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(path.toPath());
		return new String(encoded, encoding);
	}

}


