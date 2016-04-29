package org.dync.teameeting.utils;

public class RandData {
	
	public static String randomString(int len) {
		String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		String randomString = "";
		for (int i = 0; i < len; i++) {
			int randomPoz = (int) Math.floor(Math.random() * charSet.length());
			randomString += charSet.substring(randomPoz, randomPoz + 1);
		}
		return randomString;
	}

}
