package com.ko.lct.job.geocoding.utilities;

public abstract class EncryptionUtilities {
    private static final String fromStr = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()";
    private static final String toStr = "0ABCDEFGHIJK!@#$%^&*()LMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789";

    public static String encrypt(String strToEncrypt) {
	if (strToEncrypt == null || strToEncrypt.isEmpty()) {
	    return strToEncrypt;
	}

	StringBuffer inpStrBuf = new StringBuffer(strToEncrypt);
	final int l = inpStrBuf.length();
	for (int i = 0; i < l; i++) {
	    int index = fromStr.indexOf(inpStrBuf.charAt(i));
	    if (index > -1) {
		inpStrBuf.setCharAt(i, toStr.charAt(index));
	    }
	}
	return inpStrBuf.toString();
    }

    public static String decrypt(String encryptedStr) {
	if (encryptedStr == null || encryptedStr.isEmpty()) {
	    return encryptedStr;
	}

	StringBuffer inpStrBuf = new StringBuffer(encryptedStr);
	final int l = inpStrBuf.length();
	for (int i = 0; i < l; i++) {
	    int index = toStr.indexOf(inpStrBuf.charAt(i));
	    if (index >= 0) {
		inpStrBuf.setCharAt(i, fromStr.charAt(index));

	    }
	}
	return inpStrBuf.toString();
    }

}
