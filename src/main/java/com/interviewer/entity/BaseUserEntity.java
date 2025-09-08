package com.interviewer.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author liyy
 * @email 32226691@qq.com
 * @date 2025-09-05 16:30:12
 */
@Data
@TableName("base_user")
public class BaseUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID
	 */
	@TableId(value = "f_id", type = IdType.ASSIGN_UUID)
	private String id;
	/**
	 * 账号类型
	 */
	@TableField("f_role")
	private String role;
	/**
	 * 姓名
	 */
	@TableField("f_user_name")
	private String userName;
	/**
	 * 登录密码
	 */
	@TableField("f_password")
	private String password;
	/**
	 * 学号
	 */
	@TableField("f_student_number")
	private String studentNumber;
	/**
	 * 性别
	 */
	@TableField("f_gender")
	private String gender;
	/**
	 * 邮箱
	 */
	@TableField("f_mail")
	private String mail;
	/**
	 * 手机号
	 */
	@TableField("f_phone")
	private String phone;
	/**
	 * 出生年月
	 */
	@TableField("f_birth_date")
	private Date birthDate;
	/**
	 * 教育类型0全日制1自考
	 */
	@TableField("f_education_type")
	private Integer educationType;
	/**
	 * 学历
	 */
	@TableField("f_education_level")
	private String educationLevel;
	/**
	 * 学校
	 */
	@TableField("f_school")
	private String school;
	/**
	 * 学院
	 */
	@TableField("f_college")
	private String college;
	/**
	 * 专业
	 */
	@TableField("f_major")
	private String major;
	/**
	 * 班级
	 */
	@TableField("f_school_class")
	private String schoolClass;
	/**
	 * 起始就读时间
	 */
	@TableField("f_start_date")
	private Date startDate;
	/**
	 * 结束就读时间
	 */
	@TableField("f_end_date")
	private Date endDate;
	/**
	 * 国家/城市
	 */
	@TableField("f_expected_country_city")
	private String expectedCountryCity;
	/**
	 * 期望职位
	 */
	@TableField("f_expected_position")
	private String expectedPosition;
	/**
	 * 期望薪资
	 */
	@TableField("f_expected_salary")
	private String expectedSalary;
	/**
	 * 期望行业
	 */
	@TableField("f_expected_industry")
	private String expectedIndustry;
	/**
	 * 兴趣
	 */
	@TableField("f_interests")
	private String interests;
	/**
	 * 头像MinioUrl
	 */
	@TableField("f_head_portrait")
	private String headPortrait;
	/**
	 * 密码盐
	 */
	@TableField("f_salt")
	private String salt;
	/**
	 * 创建人
	 */
	@TableField(value = "f_create_user_name",fill = FieldFill.INSERT)
	private String createUserName;
	/**
	 * 创建时间
	 */
	@TableField(value = "f_create_time",fill = FieldFill.INSERT)
	private Date createTime;
	/**
	 * 修改人
	 */
	@TableField(value = "f_last_modify_user_name",fill = FieldFill.INSERT_UPDATE)
	private String lastModifyUserName;
	/**
	 * 修改时间
	 */
	@TableField(value = "f_last_modify_time",fill = FieldFill.INSERT_UPDATE)
	private Date lastModifyTime;
	/**
	 * 是否删除
	 */
	@TableField("f_is_delete")
	@TableLogic
	private Integer isDelete;

	/**
	 * 时间
	 */
	@TableField(exist = false)
	@Schema(description = "时间")
	private Date loginTime;
	/**
	 * ip
	 */
	@TableField(exist = false)
	@Schema(description = "ip地址")
	private String loginIp;

	@TableField(exist = false)
	private Long exp;

}
