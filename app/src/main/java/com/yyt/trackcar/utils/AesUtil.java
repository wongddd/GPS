package com.yyt.trackcar.utils;

/**
 * @author hman
 */
public class AesUtil {

    // 加密
    public static String encrypt(String sSrc) {
        String sKey;
//        sKey = CWConstant.AES_KEY;
//        if (sKey == null) {
//            return null;
//        }
//        // 判断Key是否为16位
//        if (sKey.length() != 16) {
//            return null;
//        }

//        try {
//            byte[] raw = sKey.getBytes("utf-8");
//            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
//            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
//            byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
//            return new String(Base64.encodeBase64(encrypted));//此处使用BASE64做转码功能，同时能起到2次加密的作用。
//        } catch (Exception e) {
//            if (BuildConfig.DEBUG)
//                e.printStackTrace();
//        }
        return "";
    }

    // 解密
    public static String decrypt(String sSrc) {
//        try {
//            String sKey;
//            sKey = CWConstant.AES_KEY;
////            // 判断Key是否正确
////            if (sKey == null) {
////                return null;
////            }
////            // 判断Key是否为16位
////            if (sKey.length() != 16) {
////                return null;
////            }
//            byte[] raw = sKey.getBytes("utf-8");
//            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
//            byte[] encrypted1 = Base64.decodeBase64(sSrc.getBytes());//先用base64解密
//
//            byte[] original = cipher.doFinal(encrypted1);
//            return new String(original, "utf-8");
//        } catch (Exception e) {
//            if (BuildConfig.DEBUG)
//                e.printStackTrace();
//        }
        return "";
    }

    // 加密
    public static String customEncrypt(String sSrc) {
//        String sKey = CWConstant.AES_KEY;
////        if (sKey == null) {
////            return null;
////        }
////        // 判断Key是否为16位
////        if (sKey.length() != 16) {
////            return null;
////        }
//        try {
//            byte[] raw = sKey.getBytes("utf-8");
//            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
//            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
//            byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
//            return new String(Base64.encodeBase64(encrypted));//此处使用BASE64做转码功能，同时能起到2次加密的作用。
//        } catch (Exception e) {
//            if (BuildConfig.DEBUG)
//                e.printStackTrace();
//        }
        return "";
    }

    // 解密
    public static String customDecrypt(String sSrc) {
//        try {
//            String sKey = CWConstant.AES_KEY;
////            // 判断Key是否正确
////            if (sKey == null) {
////                return null;
////            }
////            // 判断Key是否为16位
////            if (sKey.length() != 16) {
////                return null;
////            }
//            byte[] raw = sKey.getBytes("utf-8");
//            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
//            byte[] encrypted1 = Base64.decodeBase64(sSrc.getBytes());//先用base64解密
//
//            byte[] original = cipher.doFinal(encrypted1);
//            return new String(original, "utf-8");
//        } catch (Exception e) {
//            if (BuildConfig.DEBUG)
//                e.printStackTrace();
//        }
        return "";
    }
}

