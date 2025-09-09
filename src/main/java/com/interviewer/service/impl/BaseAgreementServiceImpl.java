package com.interviewer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interviewer.entity.BaseAgreementEntity;
import com.interviewer.mapper.BaseAgreementMapper;
import com.interviewer.service.BaseAgreementService;
import org.springframework.stereotype.Service;



@Service("baseAgreementService")
public class BaseAgreementServiceImpl extends ServiceImpl<BaseAgreementMapper, BaseAgreementEntity> implements BaseAgreementService {


}