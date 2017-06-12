package com.aafes.starsettler.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Converter {

    public static final Charset ASCII = Charset.forName("ISO-8859-1");
    public static final Charset EBCDIC = Charset.forName("CP037");
    public static final String ISO8583_TIMESTAMP_FORMAT = "yyMMddHHmmss";
    public static final String ISO8601_TIMESTAMP_FORMAT =
            "yyyy-MM-dd'T'HH:mm:ss";

    public static String getIsoTimestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat(
                ISO8601_TIMESTAMP_FORMAT);
        Date today = new java.util.Date();
        String t = formatter.format(
                new java.sql.Timestamp(today.getTime()));
        return t;
    }

    static int getByteArrayLength(byte[] ba) {
        int i = ba.length - 1;
        while (i >= 0) {
            if (ba[i] != 0x00) {
                break;
            }
            i --;
        }
        i ++;
        return i;
    }

    private Converter() {
    }

    public static String stringToBcd(String s) {
        int size = s.length();
        byte[] bytes = new byte[(size + 1) / 2];
        int index = 0;
        boolean advance = size % 2 != 0;
        for (char c : s.toCharArray()) {
            byte b = (byte) (c - '0');
            if (advance) {
                bytes[index] |= b;
                index ++;
            }
            else {
                bytes[index] |= (byte) (b << 4);
            }
            advance =  ! advance;
        }
        return new String(bytes, Converter.ASCII);
    }

    public static String bcdToString(String bcd) {
        StringBuilder sb = new StringBuilder(bcd.length() * 2);
        for (byte b : bcd.getBytes(Converter.ASCII)) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String getTimestamp() {
        java.text.SimpleDateFormat formatter =
                new java.text.SimpleDateFormat(ISO8583_TIMESTAMP_FORMAT);
        java.util.Date today = new java.util.Date();
        String t = formatter.format(
                new java.sql.Timestamp(today.getTime()));
        return t;
    }

    public static byte[] logStringToBytes(String logString) {
        ByteBuffer bb = ByteBuffer.allocate(4096);
        byte c;
        int len = logString.length();
        //for (int i = 0; i < len; i++) {
        int i = 0;
        while (i < len) {
            c = (byte) logString.charAt(i);
            if (binaryConvention(logString, i)) {
                String s = logString.substring(i + 2, i + 4);
                c = (byte) Integer.parseInt(s, 16);
                i += 4;
                bb.put(c);
            }
            else {
                bb.put(c);
            }
            i ++;
        }
        bb.flip();
        int size = bb.limit();
        byte[] b = new byte[size];
        bb.get(b);
        return b;
    }

    private static boolean binaryConvention(String s, int i) {
        int edge;
        edge = s.length() - 4;
        if (i >= edge) {
            return false;
        }
        char ch = s.charAt(i);
        if (ch != '<') {
            return false;
        }
        ch = s.charAt(i + 1);
        if (ch != '!') {
            return false;
        }
        ch = s.charAt(i + 2);
        if (Character.digit(ch, 16) == -1) {
            return false;
        }
        ch = s.charAt(i + 3);
        if (Character.digit(ch, 16) == -1) {
            return false;
        }
        ch = s.charAt(i + 4);
        return ch == '>';
    }

    public static void printByteArray(byte[] b) {
        if (b == null) {
            return;
        }
        if (b.length == 0) {
            return;
        }
        long len = b.length;
        //System.out.println(new String(b));
        for (int i = b.length - 1;i >= 0;i --) {
            if (b[i] != 0) {
                len = i + 1;
                break;
            }
        }
        for (int i = 0;i < len;i ++) {
            if (b[i] < 32) {
                System.out.printf("\\x%02x", b[i]);
            }
            else {
                System.out.printf("%c", b[i]);
            }
        }
        //  System.out.println();
        //System.out.println(Converter.stringify(b));
    }

    public static String hexDump(byte[] b) {
        StringBuilder sb;
        sb = new StringBuilder();

        if (b == null) {
            return "";
        }
        if (b.length == 0) {
            return "";
        }
        long len = b.length;
        //System.out.println(new String(b));
        for (int i = b.length - 1;i >= 0;i --) {
            if (b[i] != 0) {
                len = i + 1;
                break;
            }
        }
        for (int i = 0;i < len;i ++) {
            if (b[i] < 32) {
                sb.append(String.format("\\x%02x", b[i]));
            }
            else {
                sb.append((char) b[i]);
            }
        }
        return sb.toString();
    }

    public static String hexDump2(byte[] b) {
        StringBuilder sb;
        sb = new StringBuilder();

        if (b == null) {
            return "";
        }
        if (b.length == 0) {
            return "";
        }
        long len = b.length;
        //System.out.println(new String(b));
        for (int i = b.length - 1;i >= 0;i --) {
            if (b[i] != 0) {
                len = i + 1;
                break;
            }
        }
        for (int i = 0;i < len;i ++) {
            if (b[i] < 32) {
                sb.append(String.format("<!%02X>", b[i]));
            }
            else {
                sb.append((char) b[i]);
            }
        }
        return sb.toString();
    }

    public static String toJson(Object o) {
        return toCompactJson(o);
    }

    public static String toPrettyJson(Object o) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(o);
        return json;
    }

    public static String toCompactJson(Object o) {
        Gson gson = new Gson();
        String json = gson.toJson(o);
        return json;
    }

    public static String padLeftWithZeros(String data, int width) {
        String s;
        s = data;
        if (s == null) {
            s = "0";
        }
        if (s.equals("")) {
            s = "0";
        }

        return String.format("%0" + width + "d", Integer.parseInt(s));
    }

    public static String padLeftWithSpaces(String data, int width) {
        String s;
        s = data;
        if (data == null) {
            s = " ";
        }
        if (s.length() > width) {
            return s.substring(0, width);
        }
        return String.format("%" + width + "s", s);
    }

    public static byte unsignedChar(int i) {
        int j;

        if (i > 255) {
            return 0;
        }
        if (i > 127) {
            j = -129 + (i - 127);
        }
        else {
            j = i;
        }
        return (byte) j;
    }

    public static int unsignedChar(byte b) {
        return (0x000000FF & ((int) b));
    }

//    public static int unsignedShort(byte[] b) {
//        int firstByte = (0x000000FF & ((int) b[0]));
//        int secondByte = (0x000000FF & ((int) b[1]));
//        char us;
//        us = (char) (firstByte << 8 | secondByte);
//        int result = us;
//        return result;
//    }
//
//    public static byte[] unsignedShort(int i) {
//        char val = (char) i;
//        byte[] b = new byte[2];
//        b[0] = (byte) ((val & 0xFF00) >> 8);
//        b[1] = (byte) (val & 0x00FF);
//        return b;
//    }
    public static int unsignedShort(byte[] b) {
        int firstByte = (0x000000FF & ((int) b[0]));
        int secondByte = (0x000000FF & ((int) b[1]));
        char us;
        us = (char) (secondByte << 8 | firstByte);
        int result = us;
        return result;
    }

    public static byte[] unsignedShort(int i) {
        char val = (char) i;
        byte[] b = new byte[2];
        b[1] = (byte) ((val & 0xFF00) >> 8);
        b[0] = (byte) (val & 0x00FF);
        return b;
    }

    public static byte[] htons(int i) {
        char val = (char) i;
        byte[] b = new byte[2];
        b[0] = (byte) ((val & 0xFF00) >> 8);
        b[1] = (byte) (val & 0x00FF);
        return b;
    }

    public static long unsignedInt(byte[] b) {
        int firstByte = (0x000000FF & ((int) b[0]));
        int secondByte = (0x000000FF & ((int) b[1]));
        int thirdByte = (0x000000FF & ((int) b[2]));
        int fourthByte = (0x000000FF & ((int) b[3]));

        long ui = ((long) (firstByte << 24 |
                secondByte << 16 |
                thirdByte << 8 |
                fourthByte)) &
                0xFFFFFFFFL;
        return ui;
    }

    public static byte[] unsignedInt(long value) {
        byte[] b = new byte[4];
        b[0] = (byte) ((value & 0xFF000000L) >> 24);
        b[1] = (byte) ((value & 0x00FF0000L) >> 16);
        b[2] = (byte) ((value & 0x0000FF00L) >> 8);
        b[3] = (byte) (value & 0x000000FFL);
        return b;
    }

}
