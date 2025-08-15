package com.rockapp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rockapp.dto.*;
import com.rockapp.entity.BaseLithologyRecognitionReportEntity;

import java.util.List;

/**
 * 岩性识别报告表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
public interface BaseLithologyRecognitionReportService extends IService<BaseLithologyRecognitionReportEntity> {


    /**
     * 保存报告
     * @param recognitionReportDto
     * @return
     */
    String saveRecognitionResult(BaseLithologyRecognitionReportDto recognitionReportDto);

    /**
     * 添加备注/反馈
     * @param remarkDto
     */
    void saveRemark(RemarkDto remarkDto);

    /**
     *查询项目/标段下所有报告
     * @param fId
     * @param current
     * @param size
     */
    Page getProjectReport(String fId, Integer current, Integer size);

    /**
     *
     */
    Page getProjectReportTest(RecognitonReportDto recognitonReportDto,String fId,Integer current, Integer size);


    /**
     * 移动识别结果
     *
     * @param moveProjectResultDto
     */
    void moveProjectReport(MoveProjectResultDto moveProjectResultDto);

    /**
     * 删除识别报告
     * @param fId
     */
    void deleteProjectReport(String fId);

    /**
     * 根据id获取详情
     * @param recognitionReportid
     */
    ReportAllDto getReportById(String recognitionReportid);

    /**
     * 筛选查询报告
     * @param recognitonReportDto
     * @return
     */
    List<BaseLithologyRecognitionReportDto> getReport(RecognitonReportDto recognitonReportDto);
}

