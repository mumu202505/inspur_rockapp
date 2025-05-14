package com.rockapp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rockapp.dto.BaseFeedbackDto;
import com.rockapp.entity.BaseFeedbackEntity;
import com.rockapp.mapper.BaseFeedbackMapper;
import com.rockapp.service.BaseFeedbackService;
import com.rockapp.utils.CommonBeanUtils;
import com.rockapp.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("baseFeedbackService")
public class BaseFeedbackServiceImpl extends ServiceImpl<BaseFeedbackMapper, BaseFeedbackEntity> implements BaseFeedbackService {
    @Autowired
    BaseFeedbackMapper baseFeedbackMapper;

    @Override
    public void saveFeedback(BaseFeedbackDto baseFeedbackDto) {
        BaseFeedbackEntity baseFeedbackEntity = CommonBeanUtils.dtoTransfer(baseFeedbackDto, BaseFeedbackEntity.class);
        baseFeedbackEntity.setFUserId(UserUtil.getUser().getFId());
        baseFeedbackEntity.setFNikename(UserUtil.getUser().getFUserName());
        baseFeedbackMapper.insert(baseFeedbackEntity);
    }
}