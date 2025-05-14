package com.rockapp.enums.result;


import com.rockapp.core.IResultStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisResultEnum implements IResultStatus {
    REDIS_SERVER_ERROR(10001, "redis服务器连接失败"),
    REDIS_SET_VALUE_ERROR(10002, "redis存入数据失败"),
    REDIS_READ_ERROR(10003, "redis读取数据失败"),
    REDIS_EXPRIE_ERROR(10004, "redis设置过期时间失败"),
    REDIS_DELETE_ERROR(10005, "redis删除失败，key值不存在"),
    REDIS_APPEND_ERROR(10006, "redis追加值异常，无法追加"),
    REDIS_INCREMENT_ERROR(10007, "redis递增失败，递增因子必须大于0"),
    REDIS_DECR_ERROR(10008, "redis递减失败，递减因子必须大于0"),
    REDIS_NOT_ERROR(10009, "分片信息已过期，请重新上传");

    private Integer code;
    private String msg;
}
