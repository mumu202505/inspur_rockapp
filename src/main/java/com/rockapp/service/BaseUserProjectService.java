package com.rockapp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rockapp.dto.*;
import com.rockapp.entity.BaseUserProjectEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 用户项目表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
public interface BaseUserProjectService extends IService<BaseUserProjectEntity> {

    /**
     * 查询项目下所有成员项目
     * @param id
     * @return
     */
      List<UserProjectListDto> getUserProjectList(String id);

    /**
     * 查询项目下所有项目
     * @return
     */
    List<UserProjectListDto> getProjectList();

    /**
     * 分页查询登录所属下所有项目
     * @return
     */
    Page<BaseUserProjectDto> getProjectPage(Page<BaseUserProjectEntity> page,String fProjectSectionName);

    /**
     * 新增项目/标段
     * @param userProjectDto
     */
    void saveProject(UserProjectDto userProjectDto);

    /**
     * 重命名项目/标段
     * @param saveUserProjectDto
     */
    void updateUserProject(SaveUserProjectDto saveUserProjectDto);

    /**
     * 删除项目/标段
     * @param fId
     */
    void deleteUserProject(String fId);

    /**
     * 移除项目成员
     * @param deleteUserProjectDto
     */
    void deletUserProject(DeleteUserProjectDto deleteUserProjectDto);

    /**
     * 新增成员
     * @param deleteUserProjectDto
     */
    void saveUserProject(DeleteUserProjectDto deleteUserProjectDto);

    /**
     * 修改成员项目权限
     * @param userProjectListDto
     */
    void updateUserRole(UserProjectListDto userProjectListDto);

    /**
     * 查询所有项目
     * @return
     */
    List<UserProjectAllDTO> listAll();

    /**
     * 查询项目/标段下报告和结果列表(树结构)
     * @return
     */
    List<ProjectAllByIdDto> projectAllById(String fProjectSectionId,String fSectionId);


    /**
     * 根据项目/标段ID查询下所有报告和详情
     * @return
     */
    List<UserProjectAllDTO> listAll(String fId);

    /**
     * 移动标段
     * @param moveSectionDto
     */
    void moveSection(MoveSectionDto moveSectionDto);

    /**
     * 判断是否有权限
     */
    void isRole(String fProjectSectionId);
}

