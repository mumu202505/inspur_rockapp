package com.rockapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rockapp.dto.BaseLithologyRecognitionReportDto;
import com.rockapp.dto.RecognitonReportDto;
import com.rockapp.entity.BaseLithologyRecognitionReportEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 岩性识别报告表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
@Mapper
public interface BaseLithologyRecognitionReportMapper extends BaseMapper<BaseLithologyRecognitionReportEntity> {
    /**
     * 查询某项目下所有的报告日期(分页)
     *
     * @return
     */

    public Page<String> getFProtDateList(@Param("page") Page page, @Param("fId") String fId);


    /**
     * 查询某项目下所有的报告日期
     *
     * @return
     */

    public List<String> listAllById(@Param("fId") String fId);


    /**
     * 查询某项目下所有的报告日期
     *
     * @return
     */

    public Page<String> getFProtDateListTest(@Param("page") Page page, @Param("fId") String fId, @Param("recognitonReportDto") RecognitonReportDto recognitonReportDto);

    /**
     * 筛选查询
     *
     * @param recognitonReportDto
     * @return
     */
    List<BaseLithologyRecognitionReportEntity> getReportTest(@Param("recognitionreport") RecognitonReportDto recognitonReportDto, @Param("record") String record);

    /**
     * 筛选查询
     *
     * @param recognitonReportDto
     * @return
     */
    List<BaseLithologyRecognitionReportDto> getReport(@Param("recognitionreport") RecognitonReportDto recognitonReportDto);

}
