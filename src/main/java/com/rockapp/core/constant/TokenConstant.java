package com.rockapp.core.constant;

/**
 * token 系统默认过期时间及刷新token的过期时间
 */
public class TokenConstant {

    /**
     * access_token 前缀
     */
    public static final String ACCESS_TOKEN = "access_token";

    /**
     * redis 过期时间 以秒为单位  6小时;
     */
    public static final Integer EXPIRES_IN = 60 * 60 * 24 * 7; //

    /**
     * token 过期时间 毫秒   6小时
     */
    public static final Integer TOKEN_IN = 7 * 24 * 60 * 60 * 1000;

    /**
     * 验证码过期时间
     */
    public static final Integer CODE_EXPIRES_IN = 60 * 5; // 单位秒

    /**
     * redis 缓存登录用户信息的key
     */
    public static final String LOGIN_USER_REDIS_KEY = "LOGIN:USER:";
    /**
     * 手机验证码
     */
    public static final String PHONE_CODE = "phoenCode";

    /**
     * 令牌环密钥
     */
    public static final String SECRET = "abc123456def";

    /**
     * 令牌环头标识
     */
    public static final String TOKEN_PREFIX = "Bearer";

    /**
     * //配置令牌环在http heads中的键值
     */
    public static final String HEADER_STRING = "Passport";

    /**
     * //自定义字段-角色字段
     */
    public static final String ROLE = "ROLE";

}
