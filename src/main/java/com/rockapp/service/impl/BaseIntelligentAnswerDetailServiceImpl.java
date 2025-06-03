package com.rockapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rockapp.dto.DetailChatDto;
import com.rockapp.dto.SaveDetailDto;
import com.rockapp.entity.BaseIntelligentAnswerDetailEntity;
import com.rockapp.mapper.BaseIntelligentAnswerDetailMapper;
import com.rockapp.service.BaseIntelligentAnswerDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;


@Service("baseIntelligentAnswerDetailService")
@Transactional
public class BaseIntelligentAnswerDetailServiceImpl extends ServiceImpl<BaseIntelligentAnswerDetailMapper, BaseIntelligentAnswerDetailEntity> implements BaseIntelligentAnswerDetailService {
    @Autowired
    private BaseIntelligentAnswerDetailMapper baseIntelligentAnswerDetailMapper;

    @Override
    public void saveDetail(SaveDetailDto saveDetailDto) {
        String sessionId = saveDetailDto.getSessionId();
        String modelName = saveDetailDto.getModelName();
        String modelOption = saveDetailDto.getModelOption();
        baseIntelligentAnswerDetailMapper.delete(new QueryWrapper<BaseIntelligentAnswerDetailEntity>()
                .eq("f_session_log_id", sessionId));
        for (DetailChatDto detailChatDto : saveDetailDto.getChat()) {
            BaseIntelligentAnswerDetailEntity baseIntelligentAnswerDetailEntity = new BaseIntelligentAnswerDetailEntity();
            baseIntelligentAnswerDetailEntity.setFSessionLogId(sessionId);
            baseIntelligentAnswerDetailEntity.setFContent(detailChatDto.getFContent());
            baseIntelligentAnswerDetailEntity.setFRole(detailChatDto.getFRole());
            baseIntelligentAnswerDetailEntity.setFModelName(modelName);
            baseIntelligentAnswerDetailEntity.setFModelOption(modelOption);
            if (detailChatDto.getStatus() ==1) {
                baseIntelligentAnswerDetailEntity.setFLike(1);
            }else if (detailChatDto.getStatus() ==2) {
                baseIntelligentAnswerDetailEntity.setFNotLike(1);
            }
            baseIntelligentAnswerDetailMapper.insert(baseIntelligentAnswerDetailEntity);
        }
    }
}