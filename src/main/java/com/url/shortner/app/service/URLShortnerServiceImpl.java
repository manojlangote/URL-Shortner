package com.url.shortner.app.service;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.url.shortner.app.db.RedisDBServiceImpl;

import java.nio.charset.StandardCharsets;

@Component
public class URLShortnerServiceImpl {
	private static final String HASH_KEY_ALLOWED_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private static final String PROP_DESTINATION_URL = "DestinationURL";

	private static final String PROP_INFO = "INFO";

	private static final String PROP_HIT_COUNT = "HitCount";

	private static final String PROP_SHORT_URL = "shortUrl";

	private static final String PROP_MESSAGE = "Message";

	private static final String PROP_SEVERITY = "severity";

	private static final String PROP_ERROR = "ERROR";

	@Autowired
	static RedisDBServiceImpl redisDBServiceImpl = new RedisDBServiceImpl();

	private static String appDomain = System.getenv("");

	@Value("${my-app-domain}")
	public void setAppDomain(String domain) {
		appDomain = domain;
	}

	public static String getAppDomain() {
		return appDomain;
	}

//	public static void main(String[] args) {
//		System.out.println(shortenURL(
//				"https://www.youtube.com/watch?v=aBxjDBC4M1U&list=PLgUwDviBIf0oE3gA41TKO2H5bHpPd7fzn&index=47", 8));
//	}

	@SuppressWarnings("unchecked")
	public static JSONObject shortenURL(String url, String customInput, int length) {
		try {
			JSONObject response = new JSONObject();

			if (customInput != null && !customInput.isEmpty() && !customInput.equals("null")) {
				if (!RedisDBServiceImpl.doesKeyExist(customInput)) {
					return putRecord(url, customInput);
				}
				response.put(PROP_SHORT_URL, null);
				response.put(PROP_SEVERITY, PROP_ERROR);
				response.put(PROP_MESSAGE, "Custom URL already exist, Please insert unique custom URL.");
				return response;
			}
			// Calculate SHA-256 hash
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(url.getBytes(StandardCharsets.UTF_8));

			// Truncate the hash
			byte[] truncatedHash = new byte[length];
			System.arraycopy(hash, 0, truncatedHash, 0, length);

			// Convert to a string of only lowercase and uppercase letters and numbers
			String base62Chars = HASH_KEY_ALLOWED_CHAR;
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

			return putRecord(url, shortUrlKey);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String putURLIntoDB(String url, String shortUrlKey) {
		HashMap<String, String> map = new HashMap<>();
		map.put(PROP_DESTINATION_URL, url);
		map.put(PROP_HIT_COUNT, "0");
		return redisDBServiceImpl.setData(shortUrlKey, map);
	}

	public static String getURLFromDB(String shortUrl) {
		Map<String, String> urlData = redisDBServiceImpl.getData(shortUrl);
		urlData.put(PROP_HIT_COUNT, urlData.get(PROP_HIT_COUNT) + 1);
		redisDBServiceImpl.setData(shortUrl, urlData);
		return urlData.get(PROP_DESTINATION_URL);
	}

	@SuppressWarnings("unchecked")
	public static JSONObject putRecord(String url, String shortURL) {
		JSONObject response = new JSONObject();
		if (putURLIntoDB(url, shortURL).equalsIgnoreCase("OK")) {
			response.put(PROP_SHORT_URL, appDomain + shortURL);
			response.put(PROP_SEVERITY, PROP_INFO);
			response.put(PROP_MESSAGE, "Record inserted with short URL" + appDomain + shortURL);
			return response;
		}
		response.put(PROP_SEVERITY, PROP_ERROR);
		response.put(PROP_MESSAGE, "Record is not inserted with short URL" + appDomain + shortURL);
		response.put(PROP_SHORT_URL, null);
		return response;
	}

}
