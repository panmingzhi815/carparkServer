package com.dongluhitec.card.connect.util;

/**
 * User: wudong
 * Date: 24/04/2013
 * Time: 08:29
 */
public class ByteUtils {

    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder format = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            format.append(byteToHexString(bytes[i]));
            if (i != bytes.length - 1) {
                format.append(" ");
            }
        }
        return '[' + format.toString() + ']';
    }

    public static String byteArrayToHexStringNoFormat(byte[] bytes) {
        StringBuilder format = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            format.append(byteToHexString(bytes[i]));
        }
        return format.toString();
    }

    public static String byteToHexString(byte bytes) {
        char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'
        };
        char hexChars1 = 0;
        char hexChars2 = 0;
        int v;

        v = bytes & 0xFF;
        hexChars1 = hexArray[v / 16];
        hexChars2 = hexArray[v % 16];

        return hexChars1 + "" + hexChars2;
    }

    public static byte hexStringToByte(String s) {
        String hexStr1 = s.substring(0, 1);
        String hexStr2 = s.substring(1, 2);
        byte b0 = Byte.decode("0x" + new String(hexStr1.getBytes()))
                .byteValue();
        byte b1 = Byte.decode("0x" + new String(hexStr2.getBytes()))
                .byteValue();
        byte ret = (byte) (b0 << 4 ^ b1);
        return ret;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String encodeChinese(String str) {
        try {
            String ret = "";
            byte[] b = str.getBytes("gb2312");
            for (int i = 0; i < b.length; i++) {
                String hex = Integer.toHexString(b[i] & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                ret += hex.toUpperCase();
            }
            return ret;
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] reverse(byte[] bytes) {
        int length = bytes.length;
        byte[] result = new byte[length];
        for (int i = 1; i <= length; i++) {
            result[length - i] = bytes[i - 1];
        }
        return result;
    }
}
