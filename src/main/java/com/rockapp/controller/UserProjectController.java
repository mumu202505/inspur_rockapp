package com.rockapp.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rockapp.dto.*;
import com.rockapp.entity.BaseUserProjectEntity;
import com.rockapp.service.BaseUserProjectService;
import com.rockapp.utils.CommonBeanUtils;
import com.rockapp.utils.ResultUtil;
import com.rockapp.utils.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 用户项目管理
 *
 * @Author liyy
 * @PreAuthorize("hasAuthority('admin')")//只允许有admin角色的用户访问 hasAnyAuthority([auth1, auth2])
 */
@Slf4j
@CrossOrigin
@RestController
//@PreAuthorize("hasAnyAuthority('2')")
@RequestMapping("/userProject")
@RequiredArgsConstructor
@Tag(name = "项目管理", description = "项目管理相关操作")
public class UserProjectController {
    @Autowired
    private BaseUserProjectService baseUserProjectService;

    @PostMapping("/save")
    @ResponseBody
    @Operation(summary = "新增项目/标段")
    public ResultUtil saveProject(@RequestBody UserProjectDto userProjectDto) {
        baseUserProjectService.saveProject(userProjectDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @PostMapping("/update")
    @ResponseBody
    @Operation(summary = "修改项目/重命名")
    public ResultUtil updateUserProject(@RequestBody SaveUserProjectDto saveUserProjectDto) {
        baseUserProjectService.updateUserProject(saveUserProjectDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @GetMapping("/delete")
    @Operation(summary = "删除项目")
    public ResultUtil deleteUserProject(@RequestParam("fId") String fId) {
        baseUserProjectService.deleteUserProject(fId);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @PostMapping("/move")
    @Operation(summary = "移动标段")
    public ResultUtil moveSection(@RequestBody MoveSectionDto moveSectionDto) {
        baseUserProjectService.moveSection(moveSectionDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @GetMapping("/userList")
    @Operation(summary = "查询项目成员列表")
    public ResultUtil listUserProject(@RequestParam("fId") String fId) {
        List<UserProjectListDto> userProjectList = baseUserProjectService.getUserProjectList(fId);
        return ResultUtil.success(userProjectList);
    }

    @PostMapping("/deletUser")
    @Operation(summary = "移除项目成员")
    public ResultUtil deletUserProject(@RequestBody DeleteUserProjectDto deleteUserProjectDto) {
        baseUserProjectService.deletUserProject(deleteUserProjectDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @PostMapping("/saveUser")
    @Operation(summary = "新增项目成员")
    public ResultUtil saveUserProject(@RequestBody DeleteUserProjectDto deleteUserProjectDto) {
        baseUserProjectService.saveUserProject(deleteUserProjectDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @PostMapping("/updateUserRole")
    @Operation(summary = "修改用户权限")
    public ResultUtil updateUserRole(@RequestBody UserProjectListDto userProjectListDto) {
        baseUserProjectService.updateUserRole(userProjectListDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @GetMapping("/list")
    @Operation(summary = "查询登录账号下所属项目列表")
    public ResultUtil listUserProject(@RequestParam(value = "current", required = false) Integer current,
                                      @RequestParam(value = "size", required = false) Integer size,
                                      @RequestParam(value = "fProjectSectionName", required = false) String fProjectSectionName) {
        Page<BaseUserProjectEntity> page = new Page<>(current == null ? 1 : current, size == null ? 10 : size);
        Page<BaseUserProjectDto> projectPage = baseUserProjectService.getProjectPage(page, fProjectSectionName);
        return ResultUtil.success(projectPage);
    }

    @GetMapping("/getProjectById")
    @Operation(summary = "查询某项目下所有标段信息")
    public ResultUtil getProjectById(@RequestParam("parentProjectId") String parentProjectId) {
        List<BaseUserProjectEntity> baseUserProjectEntityList = baseUserProjectService.list(Wrappers.<BaseUserProjectEntity>lambdaQuery()
                .eq(BaseUserProjectEntity::getFParentProjectId, parentProjectId)
                .eq(BaseUserProjectEntity::getFUserId, UserUtil.getUser().getFId())
                .groupBy(BaseUserProjectEntity::getFId));
        List<BaseUserProjectDto> baseUserProjectDtos = CommonBeanUtils.dtoListTransfer(baseUserProjectEntityList, BaseUserProjectDto.class);
        return ResultUtil.success(baseUserProjectDtos);
    }

    @GetMapping("/listAll")
    @Operation(summary = "查询登录账号下所属项目和标段列表(树结构)")
    public ResultUtil listAll() {
        return ResultUtil.success( baseUserProjectService.listAll());
    }


}
