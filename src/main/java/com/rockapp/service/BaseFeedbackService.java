package com.rockapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rockapp.dto.BaseFeedbackDto;
import com.rockapp.entity.BaseFeedbackEntity;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.Map;

/**
 * 用户反馈表
 *
 * @author liyy
 * @email 32226691@qq.com
 * @date 2025-04-28 10:30:45
 */
public interface BaseFeedbackService extends IService<BaseFeedbackEntity> {

    /**
     * 保存用户反馈
     * @param baseFeedbackDto
     */
    void saveFeedback(BaseFeedbackDto baseFeedbackDto);

}

