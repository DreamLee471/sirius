package org.sirius.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

public class Utils {
	
	private static MessageDigest md5;
	private static BASE64Encoder base64en = new BASE64Encoder();
	
	static{
		try {
			md5=MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	
	public static String md5(String content) throws UnsupportedEncodingException{
		return base64en.encode(md5.digest(content.getBytes("utf-8")));
        
	}

}
