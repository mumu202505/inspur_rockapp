package com.interviewer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interviewer.entity.BaseJobEntity;
import com.interviewer.mapper.BaseJobMapper;
import com.interviewer.service.BaseJobService;
import org.springframework.stereotype.Service;



@Service("baseJobService")
public class BaseJobServiceImpl extends ServiceImpl<BaseJobMapper, BaseJobEntity> implements BaseJobService {



}