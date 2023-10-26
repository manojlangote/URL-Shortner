package com.url.shortner.app.service;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.url.shortner.app.db.RedisDBServiceImpl;

import java.nio.charset.StandardCharsets;

public class URLShortnerServiceImpl {
	@Autowired
	static RedisDBServiceImpl redisDBServiceImpl = new RedisDBServiceImpl();

//	public static void main(String[] args) {
//		System.out.println(shortenURL(
//				"https://www.youtube.com/watch?v=aBxjDBC4M1U&list=PLgUwDviBIf0oE3gA41TKO2H5bHpPd7fzn&index=47", 8));
//	}

	@SuppressWarnings("unchecked")
	public static JSONObject shortenURL(String url, int length) {
		try {
			// Calculate SHA-256 hash
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(url.getBytes(StandardCharsets.UTF_8));

			// Truncate the hash
			byte[] truncatedHash = new byte[length];
			System.arraycopy(hash, 0, truncatedHash, 0, length);

			// Convert to a string of only lowercase and uppercase letters and numbers
			String base62Chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
			StringBuilder shortenedURL = new StringBuilder();

			for (byte b : truncatedHash) {
				// Ensure the byte value is non-negative
				int value = (b & 0xFF);
				// Map the byte value to the range [0, 61] (total of 62 characters)
				int index = value % 62;
				// Append the corresponding character to the shortened URL
				shortenedURL.append(base62Chars.charAt(index));
			}
			String shortUrlKey = shortenedURL.toString();

			JSONObject response = new JSONObject();
			response.put("shortUrl", "http://linkify.url/"+shortUrlKey);

			// Put inside database and then return.
			if (putURLIntoDB(url, shortUrlKey).equalsIgnoreCase("OK"))
				return response;
			else {
				response.put("shortUrl", null);
				return response;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String putURLIntoDB(String url, String shortUrlKey) {
		HashMap<String, String> map = new HashMap<>();
		map.put("DestinationURL", url);
		map.put("HitCount", "0");
		return redisDBServiceImpl.setData(shortUrlKey, map);
	}
	public static String getURLFromDB(String shortUrl) {
		Map<String, String> urlData = redisDBServiceImpl.getData(shortUrl);
		urlData.put("HitCount", urlData.get("HitCount")+1);
		redisDBServiceImpl.setData(shortUrl, urlData);
		return urlData.get("DestinationURL");
	}

}
