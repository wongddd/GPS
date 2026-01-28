package com.yyt.trackcar.netty;

import com.socks.library.KLog;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public class ByteStringUtils {


    public static final int IMEI_START_NUMBER = 5;
    public static final int IMEI_LENGTH = 8;

    public static final int INFO_MSG_START_NUMBER = 21;

    public static final int PACKAGE_START_NUMBER = 3;
    public static final int PACKAGE_LENGTH = 2;

    public static final byte APP_DOWN_MIYAO = 2;

    public static final byte APP_DOWN_MIYAOO = 1;

    // byte[] phoneLengthByte
    // ={(byte)(String.valueOf(tongXunLu).getBytes().length)};



/*	public static byte[] serviceToDeviceFinallyByte(byte[] msg){
		byte[] finallyPackage = mergeBytes(
				LanEncodeUtils.finalCipherPackage(APP_DOWN_MIYAO, msg),
				END_PACKAGE);
		return finallyPackage;
	}*/


	/*public static byte[] serviceToDeviceFinallyByte(String msg){
		byte[] finallyPackage = mergeBytes(
				LanEncodeUtils.finalCipherPackage(APP_DOWN_MIYAO, hexStringToByte(msg)),
				END_PACKAGE);
		return finallyPackage;
	}*/


    //计步
    public static byte[] getStepByte(Long step) {
        byte b_step[] = {(byte) ((step & 0xff000000) >> 24), (byte) ((step & 0xff0000) >> 16),
                (byte) ((step & 0xff00) >> 8), (byte) (step & 0xff)};
        return b_step;
    }

    /**
     * Byte转Bit
     */
    public static String byteToBit(byte b) {
        return "" + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1) + (byte) ((b >> 5) & 0x1)
                + (byte) ((b >> 4) & 0x1) + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1) + (byte) ((b >> 1) & 0x1)
                + (byte) ((b >> 0) & 0x1);
    }

    /**
     * Bit转Byte
     */
    public static byte BitToByte(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {// 4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }

    public static byte[] getStringOneByte(String msg) {


        msg = msg + "";


        byte[] byteL = {(byte) (getUtf8Byte(msg).length)};
        return byteL;
    }

    public static byte[] getIntOneByte(int num) {
        byte[] byteL = new byte[1];
        byteL[0] = (byte) num;
        return byteL;
    }


    public static byte[] byteToByte(byte b) {
        byte[] byteL = new byte[1];
        byteL[0] = b;
        return byteL;
    }

    public static byte[] getUtf8Byte(String msg) {

        msg = msg + "";


        try {

            return msg.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
            //e.printStackTrace();
        }
    }

    public static String getUtf8String(byte[] msg) {

        try {
            return new String(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 时间戳
     *
     * @return
     */
    public static byte[] getTimestamp() {
        long time = System.currentTimeMillis();
        byte b_time[] = {(byte) ((time & 0xff0000000000l) >> 40),
                (byte) ((time & 0xff00000000l) >> 32),
                (byte) ((time & 0xff000000) >> 24), (byte) ((time & 0xff0000) >> 16),
                (byte) ((time & 0xff00) >> 8),
                (byte) (time & 0xff)};

        return b_time;
    }

    public static String bytes2HexString(byte[] b) {
        KLog.d("bytes2HexString:" + b);
        String r = "";

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            // r += hex.toUpperCase();
            r += hex;
        }

        return r;
    }

    public static byte[] stringToByteUtf8(String msg) {
        try {
            return msg.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String bytes2HexString(byte b) {

        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex;
    }

    public static byte[] get15Imeibyte(String imei) {
        return hexStringToByte("0" + imei);
    }

    public static byte[] hexStringToByte(String hex) {

        if (hex == null || hex.equals("")) {
            return null;
        }
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789abcdef".indexOf(c);
        return b;
    }

    /**
     * 参数解析：
     * <p>
     * src：byte源数组 srcPos：截取源byte数组起始位置（0位置有效） length：截取的数据长度
     */
    public static byte[] byteIntercept(byte[] src, int srcPos, int length) {
        byte[] desc = new byte[length];
        System.arraycopy(src, srcPos - 1, desc, 0, length);
        return desc;
    }

    /**
     * 通过字节获取最终长度
     *
     * @param args
     */

    public static int getMsglength(byte[] b_len) {
        int Length = ((b_len[0] << 8) & 0xff00) | (b_len[1] & 0xff);
        if (Length < 0) {
            Length = Length & 0xff;
        }
        return Length;
    }

    public static int get3ByteMsglength(byte[] b_len) {
        int Length = ((b_len[0] << 16) & 0xff0000) | ((b_len[1] << 8) & 0xff00) | (b_len[2] & 0xff);
        if (Length < 0) {
            Length = Length & 0xff;
        }
        return Length;
    }


    public static byte[] getUtf2ByteLength(String msg) {

        msg = msg + "";

        int length = getUtf8Byte(msg).length;
        byte b_len[] = {(byte) ((length & 0xff00) >> 8), (byte) (length & 0x00ff)};
        return b_len;
    }


    public static byte[] getMsgByte(int length) {
        byte b_len[] = {(byte) ((length & 0xff00) >> 8), (byte) (length & 0x00ff)};
        return b_len;
    }

    public static String getImeiJustUseGetIpAndLogin(byte[] receivebyte) {
        String imei = ByteStringUtils.bytes2HexString(ByteStringUtils.byteIntercept(receivebyte,
                5, 8));
        return imei.substring(1, imei.length());
    }

    public static String byte8ToImei(byte[] receivebyte) {
        String imei = ByteStringUtils.bytes2HexString(receivebyte);
        return imei.substring(1, imei.length());
    }

    /**
     * 合并多个字节数组到一个字节数组
     *
     * @param values 动态字节数字参数
     * @return byte[] 合并后的字节数字
     */
    public static byte[] mergeBytes(byte[]... values) {
        int lengthByte = 0;
        for (byte[] value : values) {
            lengthByte += value.length;
        }
        byte[] allBytes = new byte[lengthByte];
        int countLength = 0;
        for (byte[] b : values) {
            System.arraycopy(b, 0, allBytes, countLength, b.length);
            countLength += b.length;
        }
        return allBytes;
    }

    /**
     * 组包
     */

    private static final byte[] START_PACKAGE = {0x5B, 0x5B};

    private static final byte[] DOWN_PACKAGE = {0x7D};

    public static final byte[] END_PACKAGE = {0x0D, 0x0A};

    public static final byte[] KONG_LENGTH = {0x00, 0x00};


    public static int decodeHEX(String hexs) {
        BigInteger bigint = new BigInteger(hexs, 16);
        int numb = bigint.intValue();
        return numb;
    }


    // 使用1个字节表示
    public static String numToHex8(int b) {
        return String.format("%02x", b);// 2表示需要两个16进制数
    }

    // 使用2个字节表示
    public static String numToHex16(int b) {
        return String.format("%04x", b);
    }

    /**
     * 将10进制整型转为16进制字符串 （使用4个字节表示）
     *
     * @param b 10进制整型
     * @return 16进制字符串
     */
    public static String numToHex32(long b) {
        return String.format("%08x", b);
    }

    /**
     * 使用8个字节表示
     *
     * @param b 10进制整型
     * @return 16进制字符串
     */
    public static String numToHex64(long b) {
        return String.format("%016x", b);
    }


    /**
     * 获取16进制的底图标签值
     *
     * @param mapid 10进制底图标签值
     * @return 16进制底图标签值
     */
    public static String hexString(long b) {
        return "0x".concat(numToHex32(b));
    }

}
