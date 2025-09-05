/*
package com.rockapp.utils;

import com.rockapp.core.constant.CommonConstant;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

*/
/**
 * RSAUtil class
 *
 * @date 2019/1/8
 *//*


public class RSAUtil {

//    private static final String CommonConstant.PRIVATE_KEY = "234242424243/4IROPKcPxgiSVot7km6bok1w7R11pr6t60fin1mattZcHRvdbcEw86YDCmvKeBvtLSRDIQ9GvS9kFIv7yCYPnav8KPW3DbtuOXkdqkZ/I8L6fSiO6kRHeX3lm5zQ/D2s3t21XzABd2kvZ12dmANzdslAgMBAAECgYBAYhJDdymKV93y4kHlslXblZ/XjtdDCkh4b2U5Y2oMp8vAMl+5/FbYjy2g+dWM5DSUOACu2eUYkM1srZaK7oAE24cfy8uYl8eB9FcQC2EUZw8HhqUA29kcJiHPvpW0FngO/I+YDAYmY/v+AE4iZiHOlGKNCY/S+47CA5TFB49IgQJBAMMXOCnpKqF3oYeNvD+ziTohZA3t5c//hWLSsvF2Ckcf5mn8oTSAr/An2zV96XpTzcltNmBz3GfEyFEzMc0uLNECQQCxFo/DpY/ZtXjuyYmpiyzMyP0nuZuaEH7Kh1aGitpH3/h3wzF94SqdN3BG5MbhwOUGf29LiTC3VS2ex4outM4VAkByvCIky+NtiNzvytSuphkLnf2pD4N7u2wn/YCN730F7WXmaVQpe5F9bQNHx2BbuBOr5dX4DcvPH3UsBC7C2+gxAkA5eJ39m7ycphVyQXvoxx8p8syd2NHT5SiQ5+0/ys5eZySHEZT9Slb/IN/SUirtVyG8wxzGgeaKiR0+G3oQAGOlAkEAqXsSqZtKCmWxT1e5jGxePacEhgake2RH2sqn/act4UxmBkK+Itm0yBGfvF4SW+EU5Whv3EKrwF5UIHZpAYijRA==";
//    private static final String CommonConstant.PUBLIC_KEY = "234242424242424242423424+CPKljlP+CETjynD8YIklaLe5Jum6JNcO0ddaa+retH4p9ZmrbWXB0b3W3BMPOmAwpryngb7S0kQyEPRr0vZBSL+8gmD52r/Cj1tw27bjl5HapGfyPC+n0ojupER3l95Zuc0Pw9rN7dtV8wAXdpL2ddnZgDc3bJQIDAQAB";

    public static Map<String, PrivateKey> keysMap = new HashMap<>();
    public static final String VERSION = "001";

    static {
        try {
            keysMap.put(VERSION, getPrivateKey(CommonConstant.PRIVATE_KEY));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    */
/**
     * RSA最大加密明文大小
     *//*

    private static final int MAX_ENCRYPT_BLOCK = 117;

    */
/**
     * RSA最大解密密文大小
     *//*

    private static final int MAX_DECRYPT_BLOCK = 128;


    */
/**
     * 获取密钥对
     *
     * @return 密钥对
     *//*

    static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        return generator.generateKeyPair();
    }

    */
/**
     * 获取私钥
     *
     * @param privateKey 私钥字符串
     * @return
     *//*

    static PrivateKey getPrivateKey(String privateKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedKey = Base64.decodeBase64(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        return keyFactory.generatePrivate(keySpec);
    }

    */
/**
     * 获取公钥
     *
     * @param publicKey 公钥字符串
     * @return
     *//*

    static PublicKey getPublicKey(String publicKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedKey = Base64.decodeBase64(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return keyFactory.generatePublic(keySpec);
    }

    */
/**
     * RSA加密
     *
     * @param data 待加密数据
     * @return
     *//*

    public static String encrypt(String data) throws Exception {
        return encrypt(data, getPublicKey(CommonConstant.PUBLIC_KEY));
    }

    */
/**
     * RSA 解密
     *
     * @param data
     * @return
     * @throws Exception
     *//*

    public static String decrypt(String data) throws Exception {
        return decrypt(data, getPrivateKey(CommonConstant.PRIVATE_KEY));
    }

    */
/**
     * RSA 签名
     *
     * @param data
     * @return
     * @throws Exception
     *//*

    public static String sign(String data) throws Exception {
        return sign(data, getPrivateKey(CommonConstant.PRIVATE_KEY));
    }

    */
/**
     * RSA 验签
     *
     * @param srcData 原始字符串
     * @param sign    签名
     * @return 是否验签通过
     *//*

    public static boolean verify(String srcData, String sign) throws Exception {
        return verify(srcData, getPublicKey(CommonConstant.PUBLIC_KEY), sign);
    }


    */
/**
     * 加密算法
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     *//*

    private static String encrypt(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int inputLen = data.getBytes().length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data.getBytes(), offset, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data.getBytes(), offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        // 获取加密内容使用base64进行编码,并以UTF-8为标准转化成字符串
        // 加密后的字符串
        return java.util.Base64.getEncoder().encodeToString(encryptedData);
//        return Base32Util.encode(encryptedData);
    }

    */
/**
     * 解密算法
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     *//*

    private static String decrypt(String data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
//        byte[] dataBytes = Base32Util.decode(data);
        byte[] dataBytes = java.util.Base64.getDecoder().decode(data);
        int inputLen = dataBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(dataBytes, offset, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(dataBytes, offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        // 解密后的内容
        return new String(decryptedData, "UTF-8");
    }


    */
/**
     * 签名算法
     *
     * @param data       待签名数据
     * @param privateKey 私钥
     * @return 签名
     *//*

    private static String sign(String data, PrivateKey privateKey) throws Exception {
        byte[] keyBytes = privateKey.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(key);
        signature.update(data.getBytes());
        return new String(Base64.encodeBase64(signature.sign()));
    }

    */
/**
     * RSA验签算法
     *
     * @param srcData   原始字符串
     * @param publicKey 公钥
     * @param sign      签名
     * @return 是否验签通过
     *//*

    private static boolean verify(String srcData, PublicKey publicKey, String sign) throws Exception {
        byte[] keyBytes = publicKey.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(key);
        signature.update(srcData.getBytes());
        return signature.verify(Base64.decodeBase64(sign.getBytes()));
    }

//    public static void main(String[] args) {
//        try {
//            // 生成密钥对
////            KeyPair keyPair = getKeyPair();
////            String privateKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
////            String publicKey = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
////            System.out.println("私钥:" + privateKey);
////            System.out.println("公钥:" + publicKey);
//            // RSA加密
//            String data = "待加密的文字内容";
//            String encryptData = encrypt(data, getPublicKey(CommonConstant.PUBLIC_KEY));
//            System.out.println("加密后内容:" + encryptData);
//            // RSA解密
//            String decryptData = decrypt(encryptData, getPrivateKey(CommonConstant.PRIVATE_KEY));
//            System.out.println("解密后内容:" + decryptData);
//
//            // RSA签名
//            String sign = sign(data, getPrivateKey(CommonConstant.PRIVATE_KEY));
//            System.out.println("RSA签名：" + sign);
////             RSA验签
//            boolean result = verify(data, getPublicKey(CommonConstant.PUBLIC_KEY), sign);
//            System.out.print("验签结果:" + result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.print("加解密异常");
//        }
//    }
}
*/
