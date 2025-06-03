package com.rockapp.service.impl;

import com.rockapp.entity.BaseSyncLithologyKnowledgeEntity;
import com.rockapp.mapper.BaseSyncLithologyKnowledgeMapper;
import com.rockapp.service.BaseSyncLithologyKnowledgeService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;


@Service("baseSyncLithologyKnowledgeService")
@Transactional
public class BaseSyncLithologyKnowledgeServiceImpl extends ServiceImpl<BaseSyncLithologyKnowledgeMapper, BaseSyncLithologyKnowledgeEntity> implements BaseSyncLithologyKnowledgeService {


}