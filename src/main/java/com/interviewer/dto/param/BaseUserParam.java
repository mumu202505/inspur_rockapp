package com.interviewer.dto.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Schema(description = "修改用户信息入参")
public class BaseUserParam implements Serializable {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private String id;
    /**
     * 账号类型
     */
    @Schema(description = "账号类型")
    private String role;
    /**
     * 姓名
     */
    @Schema(description = "姓名")
    private String userName;
    /**
     * 学号
     */
    @Schema(description = "学号")
    private String studentNumber;
    /**
     * 性别
     */
    @Schema(description = "性别")
    private String gender;
    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String mail;
    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;
    /**
     * 出生年月
     */
    @Schema(description = "出生年月")
    private Date birthDate;
    /**
     * 教育类型0全日制1自考
     */
    @Schema(description = "教育类型0全日制1自考")
    private Integer educationType;
    /**
     * 学历
     */
    @Schema(description = "学历")
    private String educationLevel;
    /**
     * 学校
     */
    @Schema(description = "学校")
    private String school;
    /**
     * 学院
     */
    @Schema(description = "学院")
    private String college;
    /**
     * 专业
     */
    @Schema(description = "专业")
    private String major;
    /**
     * 班级
     */
    @Schema(description = "班级")
    private String schoolClass;
    /**
     * 起始就读时间
     */
    @Schema(description = "起始就读时间")
    private Date startDate;
    /**
     * 结束就读时间
     */
    @Schema(description = "结束就读时间")
    private Date endDate;
    /**
     * 国家/城市
     */
    @Schema(description = "国家/城市")
    private String expectedCountryCity;
    /**
     * 期望职位
     */
    @Schema(description = "期望职位")
    private String expectedPosition;
    /**
     * 期望薪资
     */
    @Schema(description = "期望薪资")
    private String expectedSalary;
    /**
     * 期望行业
     */
    @Schema(description = "期望行业")
    private String expectedIndustry;
    /**
     * 兴趣
     */
    @Schema(description = "兴趣")
    private String interests;
    /**
     * 头像MinioUrl
     */
    @Schema(description = "头像MinioUrl")
    private String headPortrait;
}
