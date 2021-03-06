package com.lehuipay.leona.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class CommonUtil {

    /**
     * trim string, include convert null to ""; if str1 is null, will return "";
     *
     * @param str1
     * @return
     */
    public static String NVLL(String str1) {
        if (str1 == null)
            return "";
        else
            return str1;
    }

    public static Boolean isEmpty(String str1) {
        return NVLL(str1).isEmpty();
    }

    public static Boolean equals(String str1, String str2) {
        return NVLL(str1).equals(NVLL(str2));
    }

    private static Random random=new Random();

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    public static String readPemFile2String(String fileName) throws IOException, UnsupportedEncodingException {
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(filecontent);
        } finally{
            try{
                if(in != null){
                    in.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return new String(filecontent, "UTF-8").
                replace("-----BEGIN PRIVATE KEY-----", "").
                replace("-----END PRIVATE KEY-----", "").
                replace("-----BEGIN PUBLIC KEY-----", "").
                replace("-----END PUBLIC KEY-----", "").trim();
    }

    /**
     * 生成随即串
     *
     * @param min 最小程度
     * @param max 最大长度
     * @return
     */
    public static String randomStr(int min, int max){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        final int length = random.nextInt(max -min + 1) + min;
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String randomStr(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    private static Base64 base64 = new Base64();

    public static byte[] base64Decode(String encrypt) {
        return base64.decode(encrypt);
    }

    public static String base64Encode(byte[] data) {
        return base64.encodeToString(data);
    }

    public static String inputStream2String(InputStream in) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        return result.toString(StandardCharsets.UTF_8.name());
    }

    public static byte[] inputStream2ByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        output.flush();
        output.close();
        return output.toByteArray();
    }
}
