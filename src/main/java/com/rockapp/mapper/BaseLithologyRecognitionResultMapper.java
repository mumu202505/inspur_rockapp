package com.rockapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rockapp.dto.BaseLithologyRecognitionResultDto;
import com.rockapp.dto.RecognitonResultDto;
import com.rockapp.entity.BaseLithologyRecognitionResultEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 岩性识别结果表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
@Mapper
public interface BaseLithologyRecognitionResultMapper extends BaseMapper<BaseLithologyRecognitionResultEntity> {
    /**
     * 根据筛选条件查询岩性识别结果
     * @param recognitionresult
     * @return
     */
    public List<BaseLithologyRecognitionResultDto> getResult(@Param("recognitionresult") RecognitonResultDto recognitionresult);

    public List<BaseLithologyRecognitionResultDto> getResultNew(@Param("recognitionresult") RecognitonResultDto recognitionresult,@Param("fId")String fId,@Param("record") String record);

    /**
     * 查询某项目下所有的识别日期
     * @return
     */

    public Page<String> getFIdentifyDateList(@Param("page") Page page, @Param("fId") String fId);


    public Page<String> getFIdentifyDateListNew(@Param("page") Page page, @Param("fId") String fId,@Param("recognitionresult") RecognitonResultDto recognitionresult);
	
}
