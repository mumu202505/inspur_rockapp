package com.interviewer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "文件上传初始化入参")
public class FileInitDto implements Serializable {
    @Schema(description = "文件MD5")
    String fileMd5;
    @Schema(description = "文件名称")
    String fileName;
    @Schema(description = "文件大小")
    String fileSize;
    @Schema(description = "文件总片数")
    String totalChunks;
}
