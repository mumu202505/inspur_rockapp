package com.rockapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rockapp.core.constant.CommonConstant;
import com.rockapp.core.exception.ServiceException;
import com.rockapp.dto.*;
import com.rockapp.entity.BaseLithologyRecognitionResultEntity;
import com.rockapp.entity.BaseUserEntity;
import com.rockapp.entity.BaseUserProjectEntity;
import com.rockapp.enums.result.SysResultEnum;
import com.rockapp.mapper.BaseLithologyRecognitionResultMapper;
import com.rockapp.mapper.BaseUserProjectMapper;
import com.rockapp.service.BaseLithologyRecognitionResultService;
import com.rockapp.utils.CommonBeanUtils;
import com.rockapp.utils.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service("baseLithologyRecognitionResultService")
@Transactional
public class BaseLithologyRecognitionResultServiceImpl extends ServiceImpl<BaseLithologyRecognitionResultMapper, BaseLithologyRecognitionResultEntity> implements BaseLithologyRecognitionResultService {

    @Autowired
    BaseLithologyRecognitionResultMapper baseLithologyRecognitionResultMapper;

    @Autowired
    BaseUserProjectMapper baseUserProjectMapper;

    @Override
    public List<BaseLithologyRecognitionResultDto> getResult(RecognitonResultDto recognitionresult) {
        if (recognitionresult.getFIsAll() != null) {
            recognitionresult.setFUserId(UserUtil.getUser().getFId());
        }
        return baseLithologyRecognitionResultMapper.getResult(recognitionresult);
    }

    @Override
    public Page<ProjectResultAllDto> getResultNew(RecognitonResultDto recognitionresult, String fId, Integer current, Integer size) {
        Page page = new Page<>(current == null ? 1 : current, size == null ? 10 : size);
        //是否查询本人数据
        if (recognitionresult.getFIsAll() == 1) {
            recognitionresult.setFUserId(UserUtil.getUser().getFId());
        }
        recognitionresult.setFUserId(fId);
        //获取出当前项目下所有识别结果日期（如果筛选条件有日期范围，则根据日期范围查询）
        Page<String> fIdentifyDateList = baseLithologyRecognitionResultMapper.getFIdentifyDateListNew(page, fId, recognitionresult);
        //查找出所有识别日期下的记录
        List<String> records = fIdentifyDateList.getRecords();
        List<ProjectResultAllDto> objects = new ArrayList<>();
        //判断是否有数据
        if (CollectionUtils.isNotEmpty(records)) {
            for (String record : records) {
                ProjectResultAllDto projectResultAllDto = new ProjectResultAllDto();
                //根据筛选条件查找出当前项目/标段下的数据
                List<BaseLithologyRecognitionResultDto> baseLithologyRecognitionResultDtos = baseLithologyRecognitionResultMapper.getResultNew(recognitionresult, fId, record);
                if (CollectionUtils.isNotEmpty(baseLithologyRecognitionResultDtos)) {
                    //每个日期为一组
                    projectResultAllDto.setFIdentifyDate(record);
                    for (BaseLithologyRecognitionResultDto baseLithologyRecognitionResultDto : baseLithologyRecognitionResultDtos) {
                        //查询出关联的结果下的项目名称和标段名称
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
                    projectResultAllDto.setResultDtoList(baseLithologyRecognitionResultDtos);
                    objects.add(projectResultAllDto);
                } else {
                    fIdentifyDateList.setTotal(0);
                }
            }
        }
        // 创建全新的Page对象，完全替换内容
        Page<ProjectResultAllDto> newPage = new Page<>();
        newPage.setRecords(objects);
        newPage.setCurrent(fIdentifyDateList.getCurrent());
        newPage.setSize(fIdentifyDateList.getSize());
        newPage.setTotal(fIdentifyDateList.getTotal());
        //返回分页信息
        return newPage;
    }

    @Override
    public String saveRecognitionResult(BaseLithologyRecognitionResultDto recognitionResultDto) {
        //校验权限
        if (StringUtils.isNotEmpty(recognitionResultDto.getFProjectSectionId())) {
            isJurisdictionProject(recognitionResultDto.getFProjectSectionId());
        } else {
            isJurisdictionProject(recognitionResultDto.getFSectionId());
        }

        //保存岩性识别结果
        if (StringUtils.isNotEmpty(recognitionResultDto.getFSectionId())) {
            recognitionResultDto.setFProjectSectionId(null);
        }
        BaseLithologyRecognitionResultEntity baseLithologyRecognitionResultEntity = CommonBeanUtils.dtoTransfer(recognitionResultDto, BaseLithologyRecognitionResultEntity.class);
        int insert = baseLithologyRecognitionResultMapper.insert(baseLithologyRecognitionResultEntity);
        if (insert > 0) {
            return baseLithologyRecognitionResultEntity.getFId();
        }
        return "保存失败";
    }

    @Override
    public void saveRemark(RemarkDto remarkDto) {
        //校验权限
        isJurisdiction(remarkDto.getFId(), null);
        //添加识别结果备注、反馈
        LambdaUpdateWrapper<BaseLithologyRecognitionResultEntity> wrapper = Wrappers.<BaseLithologyRecognitionResultEntity>lambdaUpdate()
                .eq(BaseLithologyRecognitionResultEntity::getFId, remarkDto.getFId());
        if (remarkDto.getFRemarkInformation() != null) {
            wrapper.set(BaseLithologyRecognitionResultEntity::getFRemarkInformation, remarkDto.getFRemarkInformation());
        }
        if (remarkDto.getFFeedbackInformation() != null) {
            wrapper.set(BaseLithologyRecognitionResultEntity::getFFeedbackInformation, remarkDto.getFFeedbackInformation());
        }
        // 只有至少一个字段非空时才执行更新
        if (wrapper.getSqlSet() != null && !wrapper.getSqlSet().isEmpty()) {
            baseLithologyRecognitionResultMapper.update(wrapper);
        }
    }

    @Override
    public Page<ProjectResultAllDto> getProjectResult(String fId, Integer current, Integer size) {
        // 1. 初始化分页参数
        Page page = new Page<>(current == null ? 1 : current, size == null ? 10 : size);

        // 2. 获取当前项目下的所有识别日期（分页）
        Page<String> fIdentifyDateList = baseLithologyRecognitionResultMapper.getFIdentifyDateList(page, fId);

        // 3. 如果没有数据，直接返回空分页
        if (fIdentifyDateList.getRecords().isEmpty()) {
            return new Page<>(fIdentifyDateList.getCurrent(), fIdentifyDateList.getSize(), fIdentifyDateList.getTotal());
        }

        // 4. 批量查询这些日期下的所有识别结果（避免循环中多次查询）
        List<BaseLithologyRecognitionResultEntity> allResults = baseLithologyRecognitionResultMapper.selectList(
                Wrappers.<BaseLithologyRecognitionResultEntity>lambdaQuery()
                        .and(wq -> wq
                                .eq(BaseLithologyRecognitionResultEntity::getFProjectSectionId, fId)
                                .or()
                                .eq(BaseLithologyRecognitionResultEntity::getFSectionId, fId)
                        )
                        .in(BaseLithologyRecognitionResultEntity::getFIdentifyDate, fIdentifyDateList.getRecords())
        );

        // 5. 收集所有关联的项目ID（避免N+1查询问题）
        Set<String> projectIds = allResults.stream()
                .flatMap(r -> Stream.of(r.getFProjectSectionId(), r.getFSectionId()))
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toSet());

        // 6. 批量查询项目名称（一次性获取所有名称）
        Map<String, String> projectNameMap = new HashMap<>();
        if (!projectIds.isEmpty()) {
            baseUserProjectMapper.selectBatchIds(projectIds).forEach(
                    p -> projectNameMap.put(p.getFProjectSectionId(), p.getFProjectSectionName())
            );
        }

        // 7. 按识别日期分组结果
        Map<String, List<BaseLithologyRecognitionResultEntity>> resultsByDate = allResults.stream()
                .collect(Collectors.groupingBy(BaseLithologyRecognitionResultEntity::getFIdentifyDate));

        // 8. 构建最终DTO列表
        List<ProjectResultAllDto> dtos = new ArrayList<>();
        for (String date : fIdentifyDateList.getRecords()) {
            List<BaseLithologyRecognitionResultEntity> dateResults = resultsByDate.get(date);
            if (dateResults == null || dateResults.isEmpty()) continue;

            ProjectResultAllDto dto = new ProjectResultAllDto();
            dto.setFIdentifyDate(date);

            // 转换实体为DTO
            List<BaseLithologyRecognitionResultDto> resultDtos = CommonBeanUtils.dtoListTransfer(dateResults,
                    BaseLithologyRecognitionResultDto.class);

            // 设置项目/标段名称（从预加载的Map中获取）
            for (BaseLithologyRecognitionResultDto resultDto : resultDtos) {
                if (StringUtils.isNotEmpty(resultDto.getFProjectSectionId())) {
                    resultDto.setFProjectName(projectNameMap.get(resultDto.getFProjectSectionId()));
                }
                if (StringUtils.isNotEmpty(resultDto.getFSectionId())) {
                    resultDto.setFSectionName(projectNameMap.get(resultDto.getFSectionId()));
                    // 如果需要，设置父项目名称
                    if (StringUtils.isNotEmpty(resultDto.getFProjectSectionId())) {
                        resultDto.setFProjectName(projectNameMap.get(resultDto.getFProjectSectionId()));
                    }
                }
            }

            dto.setResultDtoList(resultDtos);
            dtos.add(dto);
        }

        // 9. 返回分页结果
        Page<ProjectResultAllDto> resultPage = new Page<>();
        resultPage.setRecords(dtos);
        resultPage.setCurrent(fIdentifyDateList.getCurrent());
        resultPage.setSize(fIdentifyDateList.getSize());
        resultPage.setTotal(fIdentifyDateList.getTotal());

        return resultPage;
    }
//    public Page getProjectResult(String fId, Integer current, Integer size) {
//        Page page = new Page<>(current == null ? 1 : current, size == null ? 10 : size);
//        //获取出当前项目下所有识别结果日期
//        Page<String> fIdentifyDateList = baseLithologyRecognitionResultMapper.getFIdentifyDateList(page, fId);
//        //查找出所有识别日期下的记录
//        List<String> records = fIdentifyDateList.getRecords();
//        List<ProjectResultAllDto> objects = new ArrayList<>();
//        for (String record : records) {
//            if (StringUtils.isNotEmpty(record)) {
//                ProjectResultAllDto projectResultAllDto = new ProjectResultAllDto();
//                List<BaseLithologyRecognitionResultEntity> resultEntities = baseLithologyRecognitionResultMapper.selectList(Wrappers.<BaseLithologyRecognitionResultEntity>lambdaQuery()
//                        .and(wq -> wq
//                                .eq(BaseLithologyRecognitionResultEntity::getFProjectSectionId, fId)
//                                .or()
//                                .eq(BaseLithologyRecognitionResultEntity::getFSectionId, fId)
//                        )
//                        .eq(BaseLithologyRecognitionResultEntity::getFIdentifyDate, record));
//
//                projectResultAllDto.setFIdentifyDate(record);
//                List<BaseLithologyRecognitionResultDto> baseLithologyRecognitionResultDtos = CommonBeanUtils.dtoListTransfer(resultEntities, BaseLithologyRecognitionResultDto.class);
//                for (BaseLithologyRecognitionResultDto baseLithologyRecognitionResultDto : baseLithologyRecognitionResultDtos) {
//                    //查询出关联的结果下的项目名称和标段名称
//                    if (StringUtils.isNotEmpty(baseLithologyRecognitionResultDto.getFProjectSectionId())) {
//                        //查询项目名称
//                        BaseUserProjectEntity baseUserProjectEntity = baseUserProjectMapper.selectById(baseLithologyRecognitionResultDto.getFProjectSectionId());
//                        baseLithologyRecognitionResultDto.setFProjectName(baseUserProjectEntity.getFProjectSectionName());
//                    }
//                    if (StringUtils.isNotEmpty(baseLithologyRecognitionResultDto.getFSectionId())) {
//                        //查询标段名称
//                        BaseUserProjectEntity baseUserProjectEntity = baseUserProjectMapper.selectById(baseLithologyRecognitionResultDto.getFSectionId());
//                        baseLithologyRecognitionResultDto.setFSectionName(baseUserProjectEntity.getFProjectSectionName());
//                        //查询父项目名称
//                        BaseUserProjectEntity baseUserProject = baseUserProjectMapper.selectById(baseLithologyRecognitionResultDto.getFProjectSectionId());
//                        baseLithologyRecognitionResultDto.setFProjectName(baseUserProject.getFProjectSectionName());
//                    }
//                }
//                projectResultAllDto.setResultDtoList(baseLithologyRecognitionResultDtos);
//                objects.add(projectResultAllDto);
//            }
//        }
//        // 创建全新的Page对象，完全替换内容
//        Page<ProjectResultAllDto> newPage = new Page<>();
//        newPage.setRecords(objects);
//        newPage.setCurrent(fIdentifyDateList.getCurrent());
//        newPage.setSize(fIdentifyDateList.getSize());
//        newPage.setTotal(fIdentifyDateList.getTotal());
//        //返回分页信息
//        return newPage;
//    }

    @Override
    public void moveProjectResult(MoveProjectResultDto moveProjectResultDto) {
        //校验权限
        if (StringUtils.isNotEmpty(moveProjectResultDto.getFTargetProjectId())) {
            isJurisdiction(moveProjectResultDto.getFId(), moveProjectResultDto.getFTargetProjectId());
        }
        if (StringUtils.isNotEmpty(moveProjectResultDto.getFTargetSectionId())) {
            isJurisdiction(moveProjectResultDto.getFId(), moveProjectResultDto.getFTargetSectionId());
        }

        //移动识别结果
        LambdaUpdateWrapper<BaseLithologyRecognitionResultEntity> wrapper = Wrappers.<BaseLithologyRecognitionResultEntity>lambdaUpdate()
                .eq(BaseLithologyRecognitionResultEntity::getFId, moveProjectResultDto.getFId());
        //项目移动到项目 直接修改项目id
        if (moveProjectResultDto.getFTargetProjectId() != null) {
            wrapper.set(BaseLithologyRecognitionResultEntity::getFProjectSectionId, moveProjectResultDto.getFTargetProjectId());
            wrapper.set(BaseLithologyRecognitionResultEntity::getFSectionId, null);
        }
        if (moveProjectResultDto.getFTargetSectionId() != null) {
            wrapper.set(BaseLithologyRecognitionResultEntity::getFProjectSectionId, null);
            wrapper.set(BaseLithologyRecognitionResultEntity::getFSectionId, moveProjectResultDto.getFTargetSectionId());
        }
        baseLithologyRecognitionResultMapper.update(null, wrapper);
    }

    @Override
    public void deleteProjectResult(String fId) {
        //校验权限
        isJurisdiction(fId, null);
        baseLithologyRecognitionResultMapper.update(null, Wrappers.<BaseLithologyRecognitionResultEntity>lambdaUpdate()
                .set(BaseLithologyRecognitionResultEntity::getFIsDelete, 1)
                .eq(BaseLithologyRecognitionResultEntity::getFId, fId));
    }

    @Override
    public BaseLithologyRecognitionResultDto getResultById(String recognitionresultid) {
        BaseLithologyRecognitionResultEntity baseLithologyRecognitionResultEntity = baseLithologyRecognitionResultMapper.selectById(recognitionresultid);
        BaseLithologyRecognitionResultDto baseLithologyRecognitionResultDto = CommonBeanUtils.dtoTransfer(baseLithologyRecognitionResultEntity, BaseLithologyRecognitionResultDto.class);
        //查询出关联的结果下的项目名称和标段名称
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
        return baseLithologyRecognitionResultDto;
    }

    /**
     * 判断是否有操作权限（保存/编辑时）
     *
     * @Param fId 结果id
     * @Param targetId 移动到项目/标段id
     */
    void isJurisdiction(String fId, String targetId) {
        BaseLithologyRecognitionResultEntity recognitionResult = baseLithologyRecognitionResultMapper.selectOne(Wrappers.<BaseLithologyRecognitionResultEntity>lambdaQuery()
                .eq(BaseLithologyRecognitionResultEntity::getFId, fId));
        if (ObjectUtils.isEmpty(recognitionResult)) {
            throw new ServiceException(SysResultEnum.USER_PROJECT_NOT_RESULT);
        }
        //当前识别结果校验权限
        if (StringUtils.isNotEmpty(recognitionResult.getFSectionId())) {
            isJurisdictionProject(recognitionResult.getFSectionId());
        }
        if (StringUtils.isNotEmpty(recognitionResult.getFProjectSectionId())) {
            isJurisdictionProject(recognitionResult.getFProjectSectionId());
        }
        //目标项目/标段校验
        if (StringUtils.isNotEmpty(targetId)) {
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