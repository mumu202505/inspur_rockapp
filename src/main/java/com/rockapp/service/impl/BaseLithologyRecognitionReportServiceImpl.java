package com.rockapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rockapp.core.constant.CommonConstant;
import com.rockapp.core.exception.ServiceException;
import com.rockapp.dto.*;
import com.rockapp.entity.BaseLithologyRecognitionReportEntity;
import com.rockapp.entity.BaseLithologyRecognitionResultEntity;
import com.rockapp.entity.BaseUserEntity;
import com.rockapp.entity.BaseUserProjectEntity;
import com.rockapp.enums.result.SysResultEnum;
import com.rockapp.mapper.BaseLithologyRecognitionReportMapper;
import com.rockapp.mapper.BaseLithologyRecognitionResultMapper;
import com.rockapp.mapper.BaseUserProjectMapper;
import com.rockapp.service.BaseLithologyRecognitionReportService;
import com.rockapp.utils.CommonBeanUtils;
import com.rockapp.utils.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("baseLithologyRecognitionReportService")
@Transactional
public class BaseLithologyRecognitionReportServiceImpl extends ServiceImpl<BaseLithologyRecognitionReportMapper, BaseLithologyRecognitionReportEntity> implements BaseLithologyRecognitionReportService {
    @Autowired
    BaseLithologyRecognitionReportMapper baseLithologyRecognitionReportMapper;
    @Autowired
    BaseLithologyRecognitionResultMapper baseLithologyRecognitionResultMapper;
    @Autowired
    BaseUserProjectMapper baseUserProjectMapper;

    @Override
    public String saveRecognitionResult(BaseLithologyRecognitionReportDto recognitionReportDto) {
        //校验权限
        isJurisdictionProject(recognitionReportDto.getFProjectSectionId());
        //保存报告
        BaseLithologyRecognitionReportEntity baseLithologyRecognitionReportEntity = CommonBeanUtils.dtoTransfer(recognitionReportDto, BaseLithologyRecognitionReportEntity.class);
        int insert = baseLithologyRecognitionReportMapper.insert(baseLithologyRecognitionReportEntity);
        if (insert > 0) {
            return baseLithologyRecognitionReportEntity.getFId();
        }
        return "保存失败";
    }


    @Override
    public void saveRemark(RemarkDto remarkDto) {
        //校验权限
        isJurisdictionProject(remarkDto.getFProjectSectionId());
        //添加识别结果备注、反馈
        LambdaUpdateWrapper<BaseLithologyRecognitionReportEntity> wrapper = Wrappers.<BaseLithologyRecognitionReportEntity>lambdaUpdate()
                .eq(BaseLithologyRecognitionReportEntity::getFId, remarkDto.getFId());
        if (remarkDto.getFRemarkInformation() != null) {
            wrapper.set(BaseLithologyRecognitionReportEntity::getFRemarkInformation, remarkDto.getFRemarkInformation());
        }
        if (remarkDto.getFFeedbackInformation() != null) {
            wrapper.set(BaseLithologyRecognitionReportEntity::getFFeedbackInformation, remarkDto.getFFeedbackInformation());
        }
        // 只有至少一个字段非空时才执行更新
        if (wrapper.getSqlSet() != null && !wrapper.getSqlSet().isEmpty()) {
            baseLithologyRecognitionReportMapper.update(wrapper);
        }
    }

    @Override
    public Page getProjectReport(String fId, Integer current, Integer size) {
        Page page = new Page<>(current == null ? 1 : current, size == null ? 10 : size);
        //获取出当前项目下所有识别报告日期
        Page<String> fReportDateList = baseLithologyRecognitionReportMapper.getFProtDateList(page, fId);
        //查找出所有识别日期下的记录
        List<String> records = fReportDateList.getRecords();
        List<ProjectReportAllDto> objects = new ArrayList<>();
        for (String record : records) {
            if (StringUtils.isNotEmpty(record)) {
                ProjectReportAllDto projectResultAllDto = new ProjectReportAllDto();
                List<BaseLithologyRecognitionReportEntity> resultEntities = baseLithologyRecognitionReportMapper.selectList(Wrappers.<BaseLithologyRecognitionReportEntity>lambdaQuery()
                        .eq(BaseLithologyRecognitionReportEntity::getFProjectSectionId, fId)
                        .eq(BaseLithologyRecognitionReportEntity::getFReportDate, record));
                projectResultAllDto.setFReportDate(record);
                List<ReportAllDto> resultDtoList = CommonBeanUtils.dtoListTransfer(resultEntities, ReportAllDto.class);
                //查询
                for (ReportAllDto reportAllDto : resultDtoList) {
                    //查询报告的项目和标段
                    BaseUserProjectEntity reportUserProject = baseUserProjectMapper.selectById(reportAllDto.getFProjectSectionId());
                    if (StringUtils.isNotEmpty(reportUserProject.getFParentProjectId())) {
                        //有父项目id  标段名称赋值
                        reportAllDto.setFSectionName(reportUserProject.getFProjectSectionName());
                        //查询父项目名称
                        BaseUserProjectEntity baseUserProject = baseUserProjectMapper.selectById(reportUserProject.getFParentProjectId());
                        reportAllDto.setFProjectName(baseUserProject.getFProjectSectionName());
                    } else {
                        //无父项目id  则直接给项目名称赋值
                        reportAllDto.setFProjectName(reportUserProject.getFProjectSectionName());
                    }
                }
                projectResultAllDto.setReportDtoList(resultDtoList);
                objects.add(projectResultAllDto);
            }
        }
        // 创建全新的Page对象，完全替换内容
        Page<ProjectReportAllDto> newPage = new Page<>();
        newPage.setRecords(objects);
        newPage.setCurrent(fReportDateList.getCurrent());
        newPage.setSize(fReportDateList.getSize());
        newPage.setTotal(fReportDateList.getTotal());
        //返回分页信息
        return newPage;
    }

    @Override
    public Page getProjectReportTest(RecognitonReportDto recognitonReportDto, String fId, Integer current, Integer size) {
        Page page = new Page<>(current == null ? 1 : current, size == null ? 10 : size);
        if (recognitonReportDto.getFIsAll() == 1) {
            recognitonReportDto.setFUserId(UserUtil.getUser().getFId());
        }
        recognitonReportDto.setFProjectSectionId(fId);
        //获取出当前项目下所有识别结果日期（如果筛选条件有日期范围，则根据日期范围查询）
        Page<String> fReportDateList = baseLithologyRecognitionReportMapper.getFProtDateListTest(page, fId, recognitonReportDto);
        //查找出所有识别日期下的记录
        List<String> records = fReportDateList.getRecords();
        List<ProjectReportAllDto> objects = new ArrayList<>();
        //判断是否有数据
        for (String record : records) {
            if (StringUtils.isNotEmpty(record)) {
                ProjectReportAllDto projectResultAllDto = new ProjectReportAllDto();
                //根据筛选条件查找出当前项目/标段下的数据
                List<BaseLithologyRecognitionReportEntity> resultEntities = baseLithologyRecognitionReportMapper.getReportTest(recognitonReportDto, record);
                List<ReportAllDto> resultDtoList = CommonBeanUtils.dtoListTransfer(resultEntities, ReportAllDto.class);
                //查询项目/标段名称
                if (ObjectUtils.isNotEmpty(resultDtoList)) {
                    //每个日期为一组数据
                    projectResultAllDto.setFReportDate(record);
                    for (ReportAllDto reportAllDto : resultDtoList) {
                        //查询报告的项目和标段
                        BaseUserProjectEntity reportUserProject = baseUserProjectMapper.selectById(reportAllDto.getFProjectSectionId());
                        if (StringUtils.isNotEmpty(reportUserProject.getFParentProjectId())) {
                            //有父项目id  标段名称赋值
                            reportAllDto.setFSectionName(reportUserProject.getFProjectSectionName());
                            //查询父项目名称
                            BaseUserProjectEntity baseUserProject = baseUserProjectMapper.selectById(reportUserProject.getFParentProjectId());
                            reportAllDto.setFProjectName(baseUserProject.getFProjectSectionName());
                        } else {
                            //无父项目id  则直接给项目名称赋值
                            reportAllDto.setFProjectName(reportUserProject.getFProjectSectionName());
                        }
                    }
                    projectResultAllDto.setReportDtoList(resultDtoList);
                    objects.add(projectResultAllDto);
                }else {
                    fReportDateList.setTotal(0);
                }

            }
        }
        // 创建全新的Page对象，完全替换内容
        Page<ProjectReportAllDto> newPage = new Page<>();
        newPage.setRecords(objects);
        newPage.setCurrent(fReportDateList.getCurrent());
        newPage.setSize(fReportDateList.getSize());
        newPage.setTotal(fReportDateList.getTotal());
        //返回分页信息
        return newPage;
    }

    @Override
    public void moveProjectReport(MoveProjectResultDto moveProjectResultDto) {
        //校验权限
        if (StringUtils.isNotEmpty(moveProjectResultDto.getFTargetProjectId())) {
            isJurisdiction(moveProjectResultDto.getFId(), moveProjectResultDto.getFTargetProjectId());
        }
        if (StringUtils.isNotEmpty(moveProjectResultDto.getFTargetSectionId())) {
            isJurisdiction(moveProjectResultDto.getFId(), moveProjectResultDto.getFTargetSectionId());
        }

        //移动识别结果
        LambdaUpdateWrapper<BaseLithologyRecognitionReportEntity> wrapper = Wrappers.<BaseLithologyRecognitionReportEntity>lambdaUpdate()
                .eq(BaseLithologyRecognitionReportEntity::getFId, moveProjectResultDto.getFId());
        if (moveProjectResultDto.getFTargetProjectId() != null) {
            wrapper.set(BaseLithologyRecognitionReportEntity::getFProjectSectionId, moveProjectResultDto.getFTargetProjectId());
        }
        if (moveProjectResultDto.getFTargetSectionId() != null) {
            wrapper.set(BaseLithologyRecognitionReportEntity::getFProjectSectionId, moveProjectResultDto.getFTargetSectionId());
        }
        baseLithologyRecognitionReportMapper.update(null, wrapper);
    }

    @Override
    @Transactional
    public void deleteProjectReport(String fId) {
        //校验权限
        isJurisdiction(fId, null);
        baseLithologyRecognitionReportMapper.update(null, Wrappers.<BaseLithologyRecognitionReportEntity>lambdaUpdate()
                .set(BaseLithologyRecognitionReportEntity::getFIsDelete, 1)
                .eq(BaseLithologyRecognitionReportEntity::getFId, fId));
    }

    @Override
    public ReportAllDto getReportById(String recognitionReportid) {
        BaseLithologyRecognitionReportEntity baseLithologyRecognitionReportEntity = baseLithologyRecognitionReportMapper.selectById(recognitionReportid);
        ReportAllDto reportAllDto = CommonBeanUtils.dtoTransfer(baseLithologyRecognitionReportEntity, ReportAllDto.class);
        //查询报告的项目和标段
        BaseUserProjectEntity reportUserProject = baseUserProjectMapper.selectById(reportAllDto.getFProjectSectionId());
        if (StringUtils.isNotEmpty(reportUserProject.getFParentProjectId())) {
            //有父项目id  标段名称赋值
            reportAllDto.setFSectionName(reportUserProject.getFProjectSectionName());
            //查询父项目名称
            BaseUserProjectEntity baseUserProject = baseUserProjectMapper.selectById(reportUserProject.getFParentProjectId());
            reportAllDto.setFProjectName(baseUserProject.getFProjectSectionName());
        } else {
            //无父项目id  则直接给项目名称赋值
            reportAllDto.setFProjectName(reportUserProject.getFProjectSectionName());
        }
        //查询每个报告下的所有识别结果           识别结果根据类别分组？
        String fLithologyRecognitionResultId = baseLithologyRecognitionReportEntity.getFLithologyRecognitionResultId();
        if (StringUtils.isNotEmpty(fLithologyRecognitionResultId)) {
            List<String> stringList = Arrays.asList(fLithologyRecognitionResultId.split(","));
            List<BaseLithologyRecognitionResultEntity> baseLithologyRecognitionResultEntities = baseLithologyRecognitionResultMapper.selectList(Wrappers.<BaseLithologyRecognitionResultEntity>lambdaQuery()
                    .in(BaseLithologyRecognitionResultEntity::getFId, stringList));
            List<BaseLithologyRecognitionResultDto> resultDtoList = CommonBeanUtils.dtoListTransfer(baseLithologyRecognitionResultEntities, BaseLithologyRecognitionResultDto.class);
            if (CollectionUtils.isNotEmpty(resultDtoList)){
                //查询出关联的结果下的项目名称和标段名称
                for (BaseLithologyRecognitionResultDto baseLithologyRecognitionResultDto : resultDtoList) {
                    if (StringUtils.isNotEmpty(baseLithologyRecognitionResultDto.getFProjectSectionId())) {
                        //查询项目名称
                        BaseUserProjectEntity baseUserProjectEntity = baseUserProjectMapper.selectById(baseLithologyRecognitionResultDto.getFProjectSectionId());
                        baseLithologyRecognitionResultDto.setFProjectName(baseUserProjectEntity.getFProjectSectionName());
                    }
                    if (StringUtils.isNotEmpty(baseLithologyRecognitionResultDto.getFSectionId())) {
                        //查询标段名称
                        BaseUserProjectEntity baseUserProjectEntity = baseUserProjectMapper.selectById(baseLithologyRecognitionResultDto.getFSectionId());
                        baseLithologyRecognitionResultDto.setFSectionName(baseUserProjectEntity.getFProjectSectionName());
                        //查询父项目名称
                        BaseUserProjectEntity baseUserProject = baseUserProjectMapper.selectById(baseUserProjectEntity.getFParentProjectId());
                        baseLithologyRecognitionResultDto.setFProjectName(baseUserProject.getFProjectSectionName());
                    }
                }
                Map<String, List<BaseLithologyRecognitionResultDto>> map = resultDtoList.stream()
                        .collect(Collectors.groupingBy(BaseLithologyRecognitionResultDto::getFClassName));
                List<ReportResultAllDto> collect = map.entrySet().stream()
                        .map(entry -> new ReportResultAllDto(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList());
                reportAllDto.setResultDtoList(collect);
            }

        }
        return reportAllDto;
    }

    @Override
    public List<BaseLithologyRecognitionReportDto> getReport(RecognitonReportDto recognitonReportDto) {
        if (recognitonReportDto.getFIsAll() != null) {
            recognitonReportDto.setFUserId(UserUtil.getUser().getFId());
        }
        return baseLithologyRecognitionReportMapper.getReport(recognitonReportDto);
    }

    /**
     * 判断是否有操作权限（保存/编辑时）
     */
    void isJurisdiction(String fId, String targetId) {
        BaseLithologyRecognitionReportEntity baseLithologyRecognitionReportEntity = baseLithologyRecognitionReportMapper.selectOne(Wrappers.<BaseLithologyRecognitionReportEntity>lambdaQuery()
                .eq(BaseLithologyRecognitionReportEntity::getFId, fId));
        if (ObjectUtils.isEmpty(baseLithologyRecognitionReportEntity)) {
            throw new ServiceException(SysResultEnum.USER_PROJECT_NOT_REPORT);
        }
        isJurisdictionProject(baseLithologyRecognitionReportEntity.getFProjectSectionId());
        if (targetId != null) {
            isJurisdictionProject(targetId);
        }
    }

    void isJurisdictionProject(String fId) {
        BaseUserEntity user = UserUtil.getUser();
        BaseUserProjectEntity baseUserProjectEntity = baseUserProjectMapper.selectOne(Wrappers.<BaseUserProjectEntity>lambdaQuery()
                .eq(BaseUserProjectEntity::getFProjectSectionId, fId)
                .eq(BaseUserProjectEntity::getFUserId, user.getFId()));
        if (ObjectUtils.isEmpty(baseUserProjectEntity) || baseUserProjectEntity.getFRole() == CommonConstant.THREE_ROLE_CODE) {
            throw new ServiceException(SysResultEnum.USER_PROJECT_NOT_JURISDICTION);
        }
    }
}