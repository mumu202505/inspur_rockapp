package com.rockapp.service.impl;

import com.rockapp.entity.BaseTaskDescribeEntity;
import com.rockapp.mapper.BaseTaskDescribeMapper;
import com.rockapp.service.BaseTaskDescribeService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;


@Service("baseTaskDescribeService")
@Transactional
public class BaseTaskDescribeServiceImpl extends ServiceImpl<BaseTaskDescribeMapper, BaseTaskDescribeEntity> implements BaseTaskDescribeService {

}