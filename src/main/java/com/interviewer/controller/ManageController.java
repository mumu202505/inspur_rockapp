package com.interviewer.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hyh
 */
@CrossOrigin
@RestController
@PreAuthorize("hasAuthority('admin')") //配置角色，拥有该角色的用户方可访问
@RequestMapping("/manage")
public class ManageController {

}
