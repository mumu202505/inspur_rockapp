package com.rockapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rockapp.dto.BaseUserProjectDto;
import com.rockapp.dto.UserProjectAllDTO;
import com.rockapp.dto.UserProjectListDto;
import com.rockapp.entity.BaseUserProjectEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户项目表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
@Mapper
public interface BaseUserProjectMapper extends BaseMapper<BaseUserProjectEntity> {
    /**
     * 查询项目下所有成员项目
     *
     * @param id
     * @return
     */
    List<UserProjectListDto> getUserProjectList(@Param("id") String id);

    /**
     * 查询项目下所有项目
     *
     * @return
     */
    List<UserProjectListDto> getProjectList();

    /**
     * 分页查询项目下所有项目
     *
     * @return
     */
    Page<BaseUserProjectDto> getProjectPage(@Param("page") Page page, @Param("fId") String fId, @Param("fProjectSectionName") String fProjectSectionName);


    /**
     * 查询用户所有项目/标段
     * @param userId
     * @return
     */
    List<UserProjectAllDTO> getUserProjectTree(@Param("userId") String userId);


}
