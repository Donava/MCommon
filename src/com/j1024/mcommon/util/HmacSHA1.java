package com.j1024.mcommon.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by don on 1/11/15.
 */
public class HmacSHA1 {
    private final static char[] hexArray = "0123456789abcdef".toCharArray();
    private static final String ALGORITHM = "HmacSHA1";

    private static SecretKeySpec secretKeySpec;
    private static Mac mac;

    public static String hash(String value, String key) {
        try {
            if (mac == null) {
                mac = Mac.getInstance(ALGORITHM);
            }
            if (secretKeySpec == null) {
                secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), ALGORITHM);
            }
            mac.init(secretKeySpec);
            byte[] bytes = mac.doFinal(value.getBytes());
            return bytesToHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
