package com.yyt.trackcar.netty;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class LagenioAppEDT {

    private static final String[] ivA1 ={
            "!mXnPGMeO8Xi@GkT",
            "uJfOmj7O3XHSRj5b",
            "Rt3Bn!BYDSSkyNzV",
            "rVM92NHRu!j*Qb^^",
            "@rtUcxScGPK&LdVg"};

    private static final String[] ivB1 ={
            "su6lmuo9a6NgaFUMANWRz0OvgCl3JL0t",
            "eJRoYBlux@$9ZPbmHU#drC%sLZw^a9Sl",
            "y2mKusCvDTxAe$UJcFJOM7C8el$L6o^R",
            "nGZkr88fdLYSfZYxg4PlkL^*HpGIHKSX",
            "e&tJRUol6py7aIZmiV#Zi$uij2sU9DiL",
            "RuBrakydnjb^DOX@yomQYziZIuHv0r6M",
            "FLaltOoogH#dR#scv%OBS6kxvrC4cEuO"};

    private static final String packageHead ="3c3c";
    private static final String packageTail ="2f2f";


    public static  boolean verfifyMsg(String msg){
        boolean reposne = false;
        try{
            int length = msg.length();
            String head =msg.substring(0,4);
            String tail =msg.substring(length-4,length);

            if(head.equals(packageHead) && tail.equals(packageTail) && length > 85){
                reposne = true;
            }

        }catch(Exception e){
            reposne = false;
        }
        return reposne;
    }

    public static String decryptPackage(String jiamiMsg){
        String jiemiMsg = null;


        try {/*try {*/
            //String packageHead =jiamiMsg.substring(0,4);
            int mode = Integer.valueOf(jiamiMsg.substring(4,5));
            String iv = jiamiMsg.substring(5,21);

            String hashYuan256 = jiamiMsg.substring(21,85);

            int msgLength = jiamiMsg.length();
            String jiamihou = jiamiMsg.substring(85,msgLength-4);
            String	hashHou256 = hmacSHA256(iv +ivA1[mode-1] ,jiamihou);
            if(hashHou256.equals(hashYuan256)){
                jiemiMsg = decrypt(jiamihou,ivB1[mode-1],iv);
            }
            return jiemiMsg;
        } catch (Exception e) {
            jiemiMsg = "未使用新加密的请求数据: "+jiamiMsg;
        }
        return jiemiMsg;
    }

    public static String encryptPackage(String msg){

        String reponse =null;
        try {

            int mode = msg.length()%6;
            if(mode > 5){
                mode =  msg.length()%3;
            }
            if(mode == 0){
                mode =  msg.length()%2;
            }

            if(mode == 0){
                mode =  5;
            }
            String iv = getRandomString(16);
            String jiaMiMsg = encrypt(msg, ivB1[mode-1], iv);
            String sha256 = hmacSHA256(iv +ivA1[mode-1] ,jiaMiMsg);

            reponse = packageHead + mode + iv + sha256 + jiaMiMsg  + packageTail;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            reponse =null;
        }
        return reponse;
    }



    private static String decrypt(String plaintext, String scret, String radm) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        SecretKeySpec key = new SecretKeySpec(scret.getBytes("utf-8"), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(radm.getBytes("utf-8"));
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        return new String(cipher.doFinal(ByteStringUtils.hexStringToByte(plaintext)),"utf-8");
    }


    private static String  encrypt(String plaintext, String scret, String radm) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        SecretKeySpec key = new SecretKeySpec(scret.getBytes("utf-8"), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(radm.getBytes("utf-8"));
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        return ByteStringUtils.bytes2HexString(cipher.doFinal(plaintext.getBytes("utf-8")));
    }


    @SuppressWarnings("unused")
    private static byte[] hmacSHA256(byte[] key,byte[] content) throws Exception {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        hmacSha256.init(new SecretKeySpec(key, 0, key.length, "HmacSHA256"));
        byte[] hmacSha256Bytes = hmacSha256.doFinal(content);
        return hmacSha256Bytes;
    }


    private  static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b!=null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

    private static String hmacSHA256(String secret, String message) throws Exception {
        String hash = "";
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(ByteStringUtils.getUtf8Byte(secret), "HmacSHA256");
        hmacSha256.init(secret_key);
        byte[] bytes = hmacSha256.doFinal(ByteStringUtils.getUtf8Byte(message));
        hash = byteArrayToHexString(bytes);
        return hash;
    }


    private static  String getRandomString(int length) {

        byte[] dstbuf =new byte[length];


        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {

            int flag = random.nextInt() % 3;
            switch (flag)
            {
                case 0:
                    dstbuf[i] = (byte) ('A' + random.nextInt(26));
                    break;
                case 1:
                    dstbuf[i] = (byte) ('0' + random.nextInt(10));
                    break;
                case 2:
                    dstbuf[i] = (byte) ('a' + random.nextInt(26));

                    break;
                default:
                    dstbuf[i] = 'x';
                    break;
            }
        }
        return new String(dstbuf);
    }

}
