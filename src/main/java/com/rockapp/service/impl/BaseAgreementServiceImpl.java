package com.rockapp.service.impl;

import com.rockapp.entity.BaseAgreementEntity;
import com.rockapp.mapper.BaseAgreementMapper;
import com.rockapp.service.BaseAgreementService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;



@Service("baseAgreementService")
public class BaseAgreementServiceImpl extends ServiceImpl<BaseAgreementMapper, BaseAgreementEntity> implements BaseAgreementService {


}