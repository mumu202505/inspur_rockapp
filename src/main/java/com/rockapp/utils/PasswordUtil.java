package com.rockapp.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int SALT_LENGTH = 16; // 盐值长度16字节(128位)
    private static final int ITERATIONS = 10000; // PBKDF2迭代次数

    /**
     * 生成随机盐值
     *
     * @return Base64编码的盐值字符串
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 使用PBKDF2WithHmacSHA256加密密码(比MD5更安全)
     *
     * @param password 原始密码
     * @param salt     盐值
     * @return 加密后的密码(Hex格式)
     */
    public static Map<String, String> encryptPassword(String password, String salt) {
        try {
            HashMap<String, String> map = new HashMap<>();
            // 将盐值从Base64解码回字节数组
            byte[] saltBytes = Base64.getDecoder().decode(salt);

            // 使用PBKDF2算法
            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    saltBytes,
                    ITERATIONS,
                    256
            );
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();

            // 转换为Hex字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            map.put("salt", salt);
            map.put("password", hexString.toString());
            return map;
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 验证密码
     *
     * @param inputPassword  用户输入的密码
     * @param salt           数据库中存储的盐值
     * @param storedPassword 数据库中存储的加密密码
     * @return 是否匹配
     */
    public static boolean verifyPassword(String inputPassword, String salt, String storedPassword) {
        Map<String, String> stringStringMap = encryptPassword(inputPassword, salt);
        String s = stringStringMap.get("password");
        return storedPassword.equals(s);
    }
}