package com.rockapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import com.rockapp.service.BaseUserProjectService;
import com.rockapp.utils.CommonBeanUtils;
import com.rockapp.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service("baseUserProjectService")
@Transactional
public class BaseUserProjectServiceImpl extends ServiceImpl<BaseUserProjectMapper, BaseUserProjectEntity> implements BaseUserProjectService {
    @Autowired
    private BaseUserProjectMapper baseUserProjectMapper;
    @Autowired
    private BaseLithologyRecognitionResultMapper resultMapper;
    @Autowired
    private BaseLithologyRecognitionReportMapper reportMapper;

    @Override
    public List<UserProjectListDto> getUserProjectList(String id) {
        String userId = UserUtil.getUser().getFId();
        List<UserProjectListDto> userProjectList = baseUserProjectMapper.getUserProjectList(id);
        //去除本人信息
        userProjectList.removeIf(dto -> userId.equals(dto.getFUserId()));
        return userProjectList;
    }

    @Override
    public List<UserProjectListDto> getProjectList() {
        return baseUserProjectMapper.getProjectList();
    }

    @Override
    public Page<BaseUserProjectDto> getProjectPage(Page<BaseUserProjectEntity> page, String fProjectSectionName) {
        String fId = UserUtil.getUser().getFId();
        Page<BaseUserProjectDto> projectPage = baseUserProjectMapper.getProjectPage(page, fId, fProjectSectionName);
        return projectPage;
    }

    @Override
    public void saveProject(UserProjectDto userProjectDto) {
        //获取当前用户
        BaseUserEntity user = UserUtil.getUser();
        if (userProjectDto.getFParentProjectId() != null) {
            //判断此项目是否是本人创建
            isAmdinUser(userProjectDto.getFParentProjectId());
        }
        //保存项目
        BaseUserProjectEntity baseUserProjectEntity = CommonBeanUtils.dtoTransfer(userProjectDto, BaseUserProjectEntity.class);
        String uuid = UUID.randomUUID().toString();
        baseUserProjectEntity.setFId(uuid);
        baseUserProjectEntity.setFProjectSectionId(uuid);
        baseUserProjectEntity.setFUserId(user.getFId());
        baseUserProjectEntity.setFRole(CommonConstant.ONE_ROLE_CODE);
        baseUserProjectMapper.insert(baseUserProjectEntity);
    }

    @Override
    @Transactional
    public void updateUserProject(SaveUserProjectDto saveUserProjectDto) {
        //判断此项目是否是本人创建
        isAmdinUser(saveUserProjectDto.getFId());
        baseUserProjectMapper.update(Wrappers.<BaseUserProjectEntity>lambdaUpdate()
                .set(BaseUserProjectEntity::getFProjectSectionName, saveUserProjectDto.getFProjectSectionName())
                .eq(BaseUserProjectEntity::getFId, saveUserProjectDto.getFId()));
    }

    @Override
    @Transactional
    public void deleteUserProject(String fId) {
        //判断此项目是否是本人创建
        isAmdinUser(fId);
        //判断项目下是否有关联的标段
        List<BaseUserProjectEntity> baseUserProjectEntityList = baseUserProjectMapper.selectList(Wrappers.<BaseUserProjectEntity>lambdaQuery()
                .eq(BaseUserProjectEntity::getFParentProjectId, fId));
        if (baseUserProjectEntityList.size() > 0) {
            throw new ServiceException(SysResultEnum.USER_PROJECT_EXISTED);
        }
        //判断项目下是否有关联的结果
        List<BaseLithologyRecognitionResultEntity> resultEntities = resultMapper.selectList(Wrappers.<BaseLithologyRecognitionResultEntity>lambdaQuery()
                .eq(BaseLithologyRecognitionResultEntity::getFProjectSectionId, fId));
        if (resultEntities.size() > 0) {
            throw new ServiceException(SysResultEnum.USER_PROJECT_RESULT_EXISTED);
        }
        //判断项目下是否有关联的报告
        List<BaseLithologyRecognitionReportEntity> reportEntities = reportMapper.selectList(Wrappers.<BaseLithologyRecognitionReportEntity>lambdaQuery()
                .eq(BaseLithologyRecognitionReportEntity::getFProjectSectionId, fId));
        if (reportEntities.size() > 0) {
            throw new ServiceException(SysResultEnum.USER_PROJECT_REPORT_EXISTED);
        }
        baseUserProjectMapper.update(Wrappers.<BaseUserProjectEntity>lambdaUpdate()
                .set(BaseUserProjectEntity::getFIsDelete, 1)
                .eq(BaseUserProjectEntity::getFId, fId));
    }

    @Override
    @Transactional
    public void deletUserProject(DeleteUserProjectDto deleteUserProjectDto) {
        //判断权限
        isAmdinUser(deleteUserProjectDto.getFId());
        try {
            baseUserProjectMapper.delete(Wrappers.<BaseUserProjectEntity>lambdaUpdate()
                    .eq(BaseUserProjectEntity::getFProjectSectionId, deleteUserProjectDto.getFId())
                    .in(BaseUserProjectEntity::getFUserId, deleteUserProjectDto.getFUserId())
            );
        } catch (Exception e) {
            throw new ServiceException(SysResultEnum.DELETE_USER_PROJECT_FAIL);
        }
    }

    @Override
    public void saveUserProject(DeleteUserProjectDto deleteUserProjectDto) {
        //判断权限
        isAmdinUser(deleteUserProjectDto.getFId(), deleteUserProjectDto.getFShareUserId());
        //先找出项目是否存在
        BaseUserProjectEntity baseUserProject = baseUserProjectMapper.selectOne(Wrappers.<BaseUserProjectEntity>lambdaQuery()
                .eq(BaseUserProjectEntity::getFId, deleteUserProjectDto.getFId()));
        if (ObjectUtils.isEmpty(baseUserProject)) {
            throw new ServiceException(SysResultEnum.USER_PROJECT_NOT_EXIST);
        }
        //新增成员全部为查看权限
        baseUserProject.setFRole(CommonConstant.THREE_ROLE_CODE);
        //存在则添加新成员
        for (String s : deleteUserProjectDto.getFUserId()) {
            LambdaQueryWrapper<BaseUserProjectEntity> eq = Wrappers.<BaseUserProjectEntity>lambdaQuery()
                    .eq(BaseUserProjectEntity::getFProjectSectionId, baseUserProject.getFProjectSectionId())
                    .eq(BaseUserProjectEntity::getFUserId, s);
            BaseUserProjectEntity baseUserProjectEntity = baseUserProjectMapper.selectOne(eq);
            if (ObjectUtils.isNotEmpty(baseUserProjectEntity)) {
                throw new ServiceException(SysResultEnum.USER_PROJECT_NOT_ADD);
            }
            baseUserProject.setFId(null);
            baseUserProject.setFProjectSectionId(baseUserProject.getFProjectSectionId());
            baseUserProject.setFUserId(s);
            baseUserProjectMapper.insert(baseUserProject);
        }
    }

    @Override
    @Transactional
    public void updateUserRole(UserProjectListDto userProjectListDto) {
        isAmdinUser(userProjectListDto.getFId());
        baseUserProjectMapper.update(Wrappers.<BaseUserProjectEntity>lambdaUpdate()
                .set(BaseUserProjectEntity::getFRole, userProjectListDto.getFRole())
                .eq(BaseUserProjectEntity::getFProjectSectionId, userProjectListDto.getFId())
                .eq(BaseUserProjectEntity::getFUserId, userProjectListDto.getFUserId()));
    }

    @Override
    public List<UserProjectAllDTO> listAll() {
        return getUserProjectTree(UserUtil.getUser().getFId());
    }

    @Override
    public List<ProjectAllByIdDto> projectAllById(String fProjectSectionId, String fSectionId) {
        ArrayList<ProjectAllByIdDto> projectAllByIdDtos = new ArrayList<>();
        if (StringUtils.isNotEmpty(fProjectSectionId)) {
            BaseUserProjectEntity baseUserProjectEntity = baseUserProjectMapper.selectOne(Wrappers.<BaseUserProjectEntity>lambdaQuery()
                    .eq(BaseUserProjectEntity::getFProjectSectionId, fProjectSectionId));
            getProjectAllByIdDto(fProjectSectionId, baseUserProjectEntity.getFProjectSectionName(), projectAllByIdDtos);
            //查询出项目下所有标段id
            List<Map<String, String>> sectionIdList = baseUserProjectMapper.selectProjectSectionIdList(fProjectSectionId);
            for (Map sectionId : sectionIdList) {
                String id = String.valueOf(sectionId.get("f_project_section_id"));
                String name = String.valueOf(sectionId.get("f_project_section_name"));
                getProjectAllByIdDto(id, name, projectAllByIdDtos);
            }
        }
        if (StringUtils.isNotEmpty(fSectionId)) {
            BaseUserProjectEntity baseUserProjectEntity = baseUserProjectMapper.selectOne(Wrappers.<BaseUserProjectEntity>lambdaQuery()
                    .eq(BaseUserProjectEntity::getFProjectSectionId, fSectionId));
            //根据标段ID查询出标段下所有的报告和结果
            getProjectAllByIdDto(fSectionId, baseUserProjectEntity.getFProjectSectionName(), projectAllByIdDtos);
        }
        return projectAllByIdDtos;
    }

    @Override
    public List<UserProjectAllDTO> listAll(String fId) {

        return null;
    }

    @Override
    @Transactional
    public void moveSection(MoveSectionDto moveSectionDto) {
        //校验权限
        isAmdinUser(moveSectionDto.getFId());
        isAmdinUser(moveSectionDto.getTargetProjectId());
        //移动标段
        baseUserProjectMapper.update(Wrappers.<BaseUserProjectEntity>lambdaUpdate()
                .set(BaseUserProjectEntity::getFParentProjectId, moveSectionDto.getTargetProjectId())
                .eq(BaseUserProjectEntity::getFId, moveSectionDto.getFId()));
    }

    @Override
    public void isRole(String fProjectSectionId) {
        //判断此项目分享人是有编辑权限
        BaseUserProjectEntity userProject = baseUserProjectMapper.selectOne(Wrappers.<BaseUserProjectEntity>lambdaQuery()
                .eq(BaseUserProjectEntity::getFProjectSectionId, fProjectSectionId)
                .eq(BaseUserProjectEntity::getFUserId, UserUtil.getUser().getFId()));
        if (ObjectUtils.isEmpty(userProject) || userProject.getFRole().equals(CommonConstant.THREE_ROLE_CODE)) {
            throw new ServiceException(SysResultEnum.USER_PROJECT_NOT_INSERT);
        }
    }


    public List<UserProjectAllDTO> getUserProjectTree(String userId) {
        // 1. 查询用户所有项目/标段
        List<UserProjectAllDTO> allProjects = baseUserProjectMapper.getUserProjectTree(userId);

        // 2. 构建树形结构
        return buildProjectTree(allProjects);
    }

    private List<UserProjectAllDTO> buildProjectTree(List<UserProjectAllDTO> allProjects) {
        // 存储顶级项目(没有父项目的项目)
        List<UserProjectAllDTO> topLevelProjects = new ArrayList<>();

        // 按父项目ID分组
        Map<String, List<UserProjectAllDTO>> projectsByParentId = allProjects.stream()
                .filter(project -> project.getFparentProjectId() != null)
                .collect(Collectors.groupingBy(UserProjectAllDTO::getFparentProjectId));

        // 设置子项目
        allProjects.forEach(project -> {
            List<UserProjectAllDTO> children = projectsByParentId.get(project.getFprojectSectionId());
            if (children != null) {
                project.setChildren(children);
            }

            // 如果是顶级项目
            if (project.getFparentProjectId() == null ||
                    allProjects.stream().noneMatch(p -> p.getFprojectSectionId().equals(project.getFparentProjectId()))) {
                topLevelProjects.add(project);
            }
        });

        return topLevelProjects;
    }

    /**
     * 判断登录人权限
     *
     * @param fId
     */
    void isAmdinUser(String fId) {
        //判断此项目是否是本人创建
        BaseUserProjectEntity userProject = baseUserProjectMapper.selectOne(Wrappers.<BaseUserProjectEntity>lambdaQuery()
                .eq(BaseUserProjectEntity::getFProjectSectionId, fId)
                .eq(BaseUserProjectEntity::getFUserId, UserUtil.getUser().getFId()));
        if (ObjectUtils.isEmpty(userProject) || !userProject.getFRole().equals(CommonConstant.ONE_ROLE_CODE)) {
            throw new ServiceException(SysResultEnum.USER_PROJECT_NOT_INSERT);
        }
    }

    /**
     * 判断分享人权限（如分享人为null，则判断登录人）
     *
     * @param fId
     * @param fShareUserId
     */
    void isAmdinUser(String fId, String fShareUserId) {
        //判断此项目分享人是否为最高权限
        BaseUserProjectEntity userProject = baseUserProjectMapper.selectOne(Wrappers.<BaseUserProjectEntity>lambdaQuery()
                .eq(BaseUserProjectEntity::getFProjectSectionId, fId)
                .eq(BaseUserProjectEntity::getFUserId, fShareUserId));
        if (ObjectUtils.isEmpty(userProject) || !userProject.getFRole().equals(CommonConstant.ONE_ROLE_CODE)) {
            throw new ServiceException(SysResultEnum.USER_PROJECT_NOT_INSERT);
        }
    }

    List<ReportResultAllDto> getReportResultAllDto(List<BaseLithologyRecognitionResultEntity> results) {
        List<BaseLithologyRecognitionResultDto> resultDtoList = CommonBeanUtils.dtoListTransfer(results, BaseLithologyRecognitionResultDto.class);
        for (BaseLithologyRecognitionResultDto baseLithologyRecognitionResultDto : resultDtoList) {
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(baseLithologyRecognitionResultDto.getFProjectSectionId())) {
                //查询项目名称
                BaseUserProjectEntity baseUserProjectEntity = baseUserProjectMapper.selectById(baseLithologyRecognitionResultDto.getFProjectSectionId());
                baseLithologyRecognitionResultDto.setFProjectName(baseUserProjectEntity.getFProjectSectionName());
            }
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(baseLithologyRecognitionResultDto.getFSectionId())) {
                //查询标段名称
                BaseUserProjectEntity baseUserProjectEntity = baseUserProjectMapper.selectById(baseLithologyRecognitionResultDto.getFSectionId());
                baseLithologyRecognitionResultDto.setFSectionName(baseUserProjectEntity.getFProjectSectionName());
                //查询父项目名称
                BaseUserProjectEntity baseUserProject = baseUserProjectMapper.selectById(baseUserProjectEntity.getFParentProjectId());
                baseLithologyRecognitionResultDto.setFProjectName(baseUserProject.getFProjectSectionName());
            }
        }
        Map<String, List<BaseLithologyRecognitionResultDto>> resultsMap = resultDtoList.stream()
                .collect(Collectors.groupingBy(BaseLithologyRecognitionResultDto::getFClassName));

        List<ReportResultAllDto> collectResult = resultsMap.entrySet().stream()
                .map(entry -> new ReportResultAllDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return collectResult;
    }

    ArrayList<ProjectAllByIdDto> getProjectAllByIdDto(String fProjectSectionId, String fProjectSectionName, ArrayList<ProjectAllByIdDto> projectAllByIdDtos) {
        ProjectAllByIdDto projectAllByIdDto = new ProjectAllByIdDto();
        projectAllByIdDto.setFprojectSectionId(fProjectSectionId);
        projectAllByIdDto.setFprojectSectionName(fProjectSectionName);
        //根据标段ID查询出标段下所有的结果
        List<BaseLithologyRecognitionResultEntity> baseLithologyRecognitionResultEntities = resultMapper.selectList(Wrappers.<BaseLithologyRecognitionResultEntity>lambdaQuery()
                .and(eq ->
                        eq.eq(BaseLithologyRecognitionResultEntity::getFProjectSectionId, fProjectSectionId)
                                .or()
                                .eq(BaseLithologyRecognitionResultEntity::getFSectionId, fProjectSectionId)
                ));
        //根据标段ID查询出标段下所有的报告
        List<BaseLithologyRecognitionReportEntity> baseLithologyRecognitionReportEntities = reportMapper.selectList(Wrappers.<BaseLithologyRecognitionReportEntity>lambdaQuery()
                .eq(BaseLithologyRecognitionReportEntity::getFProjectSectionId, fProjectSectionId));
        List<ReportAllDto> reportAllDtos = CommonBeanUtils.dtoListTransfer(baseLithologyRecognitionReportEntities, ReportAllDto.class);
        // 先将所有结果存入Map便于快速查找
        Map<String, BaseLithologyRecognitionResultEntity> resultMap = baseLithologyRecognitionResultEntities.stream()
                .collect(Collectors.toMap(BaseLithologyRecognitionResultEntity::getFId, result -> result));
        // 遍历报告，关联对应的结果
        if (CollectionUtils.isNotEmpty(reportAllDtos)) {
            for (ReportAllDto report : reportAllDtos) {
                //遍历报告，将报告关联的结果在标段下所有结果中取出单独放入list
                List<BaseLithologyRecognitionResultEntity> reportResults = new ArrayList<>();
                String resultId = report.getFLithologyRecognitionResultId();
                if (StringUtils.isNotBlank(resultId) && resultMap.containsKey(resultId)) {
                    String[] split = report.getFLithologyRecognitionResultId().split(",");
                    for (String s : split) {
                        List<BaseLithologyRecognitionResultEntity> baseLithologyRecognitionResultEntities1 = resultMapper.selectList(Wrappers.<BaseLithologyRecognitionResultEntity>lambdaQuery()
                                .eq(BaseLithologyRecognitionResultEntity::getFId, s));
                        //讲结果进行岩性分组(报告下结果)
                        List<ReportResultAllDto> reportResultAllDto = getReportResultAllDto(baseLithologyRecognitionResultEntities1);
                        report.setResultDtoList(reportResultAllDto);
                        reportResults.add(resultMap.get(resultId));
                    }
                }


            }
            //收集所有已被报告关联的结果ID
            Set<String> usedResultIds = baseLithologyRecognitionReportEntities.stream()
                    .map(BaseLithologyRecognitionReportEntity::getFLithologyRecognitionResultId)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());
            //从全部结果中过滤掉已被报告关联的结果
            baseLithologyRecognitionResultEntities = baseLithologyRecognitionResultEntities.stream()
                    .filter(result -> !usedResultIds.contains(result.getFId()))
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(baseLithologyRecognitionResultEntities)) {
            //剩余结果分组
            List<ReportResultAllDto> resultAllDtos = getReportResultAllDto(baseLithologyRecognitionResultEntities);
            projectAllByIdDto.setResultDtoList(resultAllDtos);
        }
        projectAllByIdDtos.add(projectAllByIdDto);
        return projectAllByIdDtos;
    }

    /**
     * 查询标段下权限
     *
     * @param user
     * @param id
     * @param nameList
     * @return
     */
    List<String> nameList(BaseUserEntity user, String id, List<String> nameList) {
        List<BaseUserProjectEntity> baseUserProjectEntityList = baseUserProjectMapper.selectList(Wrappers.<BaseUserProjectEntity>lambdaQuery()
                .in(BaseUserProjectEntity::getFParentProjectId, id)
                .eq(BaseUserProjectEntity::getFUserId, user.getFId()));
        if (CollectionUtils.isNotEmpty(baseUserProjectEntityList)) {
            for (BaseUserProjectEntity userProjectEntity : baseUserProjectEntityList) {
                if (ObjectUtils.isEmpty(userProjectEntity) || userProjectEntity.getFRole() == CommonConstant.THREE_ROLE_CODE) {
                    nameList.add(userProjectEntity.getFProjectSectionName());
                }
            }
        }
        return nameList;
    }

}