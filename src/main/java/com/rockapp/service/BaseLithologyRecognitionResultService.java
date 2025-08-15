package com.rockapp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rockapp.dto.*;
import com.rockapp.entity.BaseLithologyRecognitionResultEntity;

import java.util.List;

/**
 * 岩性识别结果表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
public interface BaseLithologyRecognitionResultService extends IService<BaseLithologyRecognitionResultEntity> {

    /**
     * 根据筛选条件查询岩性识别结果
     *
     * @param recognitionresult
     * @return
     */
    List<BaseLithologyRecognitionResultDto> getResult(RecognitonResultDto recognitionresult);

    Page<ProjectResultAllDto> getResultNew(RecognitonResultDto recognitionresult , String fId, Integer current, Integer size);

    /**
     * 保存岩性识别结果返回id
     *
     * @param recognitionResultDto
     */
    String saveRecognitionResult(BaseLithologyRecognitionResultDto recognitionResultDto);

    /**
     * 添加反馈/备注
     *
     * @param remarkDto
     */
    void saveRemark(RemarkDto remarkDto);

    /**
     * 查询项目/标段下所有结果
     *
     * @param fId
     * @return
     */
    Page getProjectResult(String fId, Integer current, Integer size);


    /**
     * 移动识别结果
     *
     * @param moveProjectResultDto
     */
    void moveProjectResult(MoveProjectResultDto moveProjectResultDto);

    /**
     * 删除项目识别结果
     *
     * @param fId
     */
    void deleteProjectResult(String fId);

    BaseLithologyRecognitionResultDto getResultById(String recognitionresultid);

}

