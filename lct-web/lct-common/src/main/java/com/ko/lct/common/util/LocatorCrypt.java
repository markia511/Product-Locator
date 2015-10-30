package com.ko.lct.common.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class LocatorCrypt {
    
    public static String keyToBase64(byte[] key) {
	 return Base64.encodeBase64URLSafeString(key);
    }
    
    public static byte[] Base64ToKey(String keyString) {
	 return Base64.decodeBase64(keyString);
    }
    
    public static byte[] GuidToBytes(final String guid) {
	UUID uuid = UUID.fromString(guid);
	return ByteBuffer.allocate(16).putLong(0, uuid.getLeastSignificantBits()).putLong(8, uuid.getMostSignificantBits()).array();
    }

    public static String getSignature(final String clientId, final String keyGuid, final double latitude, final double longitude, final int distance)
	    throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException, UnsupportedEncodingException {
	StringBuffer sb = new StringBuffer();
	sb.append(clientId)
		.append(latitude)
		.append(longitude)
		.append(distance);
	SecretKeySpec secretKeySpec = new SecretKeySpec(GuidToBytes(keyGuid), "HmacSHA1");
	Mac mac = Mac.getInstance("HmacSHA1");
	mac.init(secretKeySpec);
	final byte[] signedBytes = mac.doFinal(sb.toString().getBytes("UTF-8"));
	String signatureBase64 = Base64.encodeBase64URLSafeString(signedBytes);
	return signatureBase64;
    }
    
    /*
    public static void main(String args[]) {
	System.out.println(keyToBase64(new byte[] {1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0}));
    }
    */

}
