package com.rockapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2025-04-23 10:15:50
 */
@Data
@TableName("base_file_upload_detail")
public class BaseFileUploadDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(value = "f_id", type = IdType.ASSIGN_ID)
	private Long fId;
	/**
	 * 上传文件的用户账号
	 */
	private String fUsername;
	/**
	 * 上传文件名
	 */
	private String fileName;
	/**
	 * 上传文件的MD5值
	 */
	private String fMd5;
	/**
	 * 是否完整上传过 0：否 1：是
	 */
	private Integer fIsUploaded;
	/**
	 * 曾经上传过的分片号
	 */
	private String fHasBeenUploaded;
	/**
	 * 存储的url，或者是本机的url地址
	 */
	private String fUrl;
	/**
	 * 本条记录创建时间
	 */
	private Date fCreateTime;
	/**
	 * 本条记录更新时间
	 */
	private Date fUpdateTime;
	/**
	 * 文件的总分片数
	 */
	private Integer fTotalChunks;

}
