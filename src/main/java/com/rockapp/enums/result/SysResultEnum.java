package com.rockapp.enums.result;


import com.rockapp.core.IResultStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 后台管理用户结果枚举
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
@Getter
@AllArgsConstructor
public enum SysResultEnum implements IResultStatus {
    USER_NAME_NO_EXISTED(40001, "用户名不存在"),
    USER_PASSWORD_ERROR(40002, "用户密码错误"),
    USER_NAME_PASSWORD_ERROR(40003, "用户名或密码错误"),
    USER_DISABLED(40004, "您的账号已被禁用，请联系管理员"),
    TOKEN_CREATE_FAILED(40005, "token生成失败"),
    INVALID_CAPTCHA(40006, "无效验证码"),
    ERROR_CAPTCHA(40007, "验证码错误"),
    LOGIN_EXCEPTION(40008, "登录异常"),
    GET_CURRENT_USER_FAILED(40009, "获取当前用户信息异常"),
    WECHAT_ACCESS_TOKEN_FAIL(40010, "获取access_token异常"),
    WECHAT_APP_VALID_SIGNATURE_FAIL(40010, "签名验证失败"),
    WECHAT_APP_GET_USER_FAIL(40012, "获取用户信息失败"),
    GET_CURRENT_APP_USER_FAILED(40013, "获取当前小程序用户信息异常"),
    APP_USER_DISABLED(40014, "账号已禁用"),
    APP_USER_TIMEOUT(40015, "账号已过期,请重新登录"),
    APP_USER_UPDATE(40016, "请检查手机号或者账号是否正确"),
    APP_USER_IP(40017, "已更换IP，请重新登录"),

    // 用户信息业务异常
    ADD_USER_FAIL(40100, "新增用户信息失败"),
    UPDATE_USER_FAIL(40101, "更新用户信息失败"),
    USER_CODE_EXISTED(40102, "用户已存在"),
    USER_PHONE_EXISTED(40103, "用户手机号已存在"),
    UK_INDEX_EXCEPTION(40104, "唯一索引异常"),
    DEL_USER_FAIL(40105, "用户信息删除失败"),
    ENABLED_USER_FAIL(40106, "用户信息启用失败"),
    DISABLED_USER_FAIL(40107, "用户信息禁用失败"),
    RESET_PWD_FAIL(40108, "用户密码重置失败"),
    CLOCK_USER_NO_EXISTED(40110, "当前打卡用户信息不存在"),
    OLD_PASSWORD_ERROR(40111, "原密码不正确"),
    GET_USER_ERROR(40112, "获取用户失败"),
    GET_TEXT_ERROR(40113, "短信发送失败"),
    ENCRYPT_PASSWORD_ERROR(40114, "密码加密失败"),
    BINDING_PHONE_ERROR(40115, "手机号以绑定其他账号，请更换手机号"),


    // 角色信息业务异常
    ROLE_KEY_EXISTED(40300, "角色编码已存在"),
    ROLE_NAME_EXISTED(40301, "角色名称已存在"),
    ROLE_UK_INDEX_EXCEPTION(40302, "唯一索引异常"),
    ADD_ROLE_FAIL(40303, "新增角色信息失败"),
    UPDATE_ROLE_FAIL(40304, "更新角色信息失败"),
    DEL_ROLE_FAIL(40305, "删除角色信息失败"),
    ROLE_ENABLED_USER_FAIL(40306, "角色信息启用失败"),
    ROLE_DISABLED_USER_FAIL(40307, "角色信息禁用失败"),

    //项目异常
    USER_PROJECT_EXISTED(40400, "该项目下有关联标段，无法删除"),
    USER_PROJECT_RESULT_EXISTED(40401, "该项目下有关联结果，无法删除"),
    USER_PROJECT_REPORT_EXISTED(40402, "该项目下有关联报告，无法删除"),
    DELETE_USER_PROJECT_FAIL(40403, "用户删除失败"),
    USER_PROJECT_NOT_EXIST(40404, "项目不存在,添加失败"),
    USER_PROJECT_NOT_INSERT(40405, "此项目无操作权限，无法在此项目下进行操作"),
    USER_PROJECT_NOT_JURISDICTION(40406, "无操作权限"),
    USER_PROJECT_NOT_RESULT(40407, "识别结果不存在，无法删除"),
    USER_PROJECT_NOT_REPORT(40407, "识别报告不存在，无法删除"),
    USER_PROJECT_NOT_ADD(40408, "用户已存在，无法重复添加"),


    //智能问答
    PROMPT_QUESTION_NOT_SAVE(40500, "问题已存在"),
    //文件上传
    PROMPT_FILE_EXISTED(40600, "不支持的文件类型"),
    PROMPT_FILE_CONTENT_EXISTED(40601, "检测到潜在的可执行脚本内容"),
    PROMPT_FILE_RED_EXISTED(40602, "文件读取失败"),
    PROMPT_FILE_PUT_EXISTED(40602, "文件上传失败"),
    ;

    private Integer code;
    private String msg;
}
