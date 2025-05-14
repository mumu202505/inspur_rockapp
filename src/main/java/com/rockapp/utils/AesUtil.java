package com.rockapp.utils;

import com.rockapp.core.constant.CommonConstant;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * 登陆密码加密工具类
 */
@Slf4j
public class AesUtil {
    /**
     * 加密
     *
     * @param password 密码
     * @param key      key
     * @return java.lang.String
     */
    public static String encrypt(String password, String key) {
        try {
            //获得密码的字节数组
            byte[] raw = key.getBytes();
            //根据密码生成AES密钥
            SecretKeySpec skey = new SecretKeySpec(raw, "AES");
            //根据指定算法ALGORITHM自成密码器
            Cipher cipher = Cipher.getInstance(CommonConstant.ALGORITHMS);
            //初始化密码器，第一个参数为加密(ENCRYPT_MODE)或者解密(DECRYPT_MODE)操作，第二个参数为生成的AES密钥
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            //获取加密内容的字节数组(设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
            byte[] byteContent = password.getBytes(StandardCharsets.UTF_8);
            //密码器加密数据
            byte[] encodeContent = cipher.doFinal(byteContent);
            //将加密后的数据转换为字符串返回
            return Base64.encodeBase64String(encodeContent);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密
     *
     * @param password 加密后的内容
     * @return java.lang.String
     */
    public static String decrypt(String password) {
        try {
            //获得密码的字节数组
            byte[] raw = CommonConstant.KEY.getBytes();
            //根据密码生成AES密钥
            SecretKeySpec skey = new SecretKeySpec(raw, "AES");
            //根据指定算法ALGORITHM自成密码器
            Cipher cipher = Cipher.getInstance(CommonConstant.ALGORITHMS);
            //初始化密码器，第一个参数为加密(ENCRYPT_MODE)或者解密(DECRYPT_MODE)操作，第二个参数为生成的AES密钥
            cipher.init(Cipher.DECRYPT_MODE, skey);
            //把密文字符串转回密文字节数组
            byte[] encodeContent = Base64.decodeBase64(password);
            //密码器解密数据
            byte[] byteContent = cipher.doFinal(encodeContent);
            //将解密后的数据转换为字符串返回
            return new String(byteContent, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("String : {} aes decrypt error", password);
            return null;
        }
    }
}

