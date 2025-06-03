package com.rockapp.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rockapp.core.constant.TokenConstant;
import com.rockapp.dto.BaseRegionModelDto;
import com.rockapp.dto.CurrentlyLoggedInDto;
import com.rockapp.entity.BaseRegionModelEntity;
import com.rockapp.mapper.BaseRegionModelMapper;
import com.rockapp.service.BaseRegionModelService;
import com.rockapp.service.RedisService;
import com.rockapp.utils.CommonBeanUtils;
import com.rockapp.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service("baseRegionModelService")
@RequiredArgsConstructor
@Transactional
public class BaseRegionModelServiceImpl extends ServiceImpl<BaseRegionModelMapper, BaseRegionModelEntity> implements BaseRegionModelService {
    private final RedisService redisService;
    @Autowired
    private BaseRegionModelMapper baseRegionModelMapper;

    @Override
    public List<BaseRegionModelDto> getBaseRegionModel() {
        CurrentlyLoggedInDto user = (CurrentlyLoggedInDto) redisService.get(TokenConstant.LOGIN_USER_REDIS_KEY + "_" + UserUtil.getUser().getFId());
        List<BaseRegionModelDto> baseRegionModelDtoList = new ArrayList<>();

        List<BaseRegionModelEntity> baseRegionModelEntities = baseRegionModelMapper.selectList(Wrappers.<BaseRegionModelEntity>lambdaQuery()
                .and(wq -> wq
                        .eq(BaseRegionModelEntity::getFProvinces, user.getFProvinces())
                        .or()
                        .isNull(BaseRegionModelEntity::getFProvinces)
                )
                .and(wq -> wq
                        .eq(BaseRegionModelEntity::getFCities, user.getFCities())
                        .or()
                        .isNull(BaseRegionModelEntity::getFCities))
                .and(wq -> wq
                        .eq(BaseRegionModelEntity::getFDistrictsCounties, user.getFDistrictsCounties())
                        .or()
                        .isNull(BaseRegionModelEntity::getFDistrictsCounties)
                )
                .eq(BaseRegionModelEntity::getFModelMode, "1"));
        for (BaseRegionModelEntity baseRegionModelEntity : baseRegionModelEntities) {
            BaseRegionModelDto baseRegionModelDto = CommonBeanUtils.dtoTransfer(baseRegionModelEntity, BaseRegionModelDto.class);
            // 1. 去除花括号 {}
            String content = baseRegionModelEntity.getFModelClass().substring(1, baseRegionModelEntity.getFModelClass().length() - 1);
            // 2. 使用正则表达式分割并清理
            String[] array = Arrays.stream(content.split(","))
                    .map(s -> s.replaceAll("^\\s*\"|\"\\s*$", "").trim()) // 去除引号和空格
                    .toArray(String[]::new);
            baseRegionModelDto.setFModelClass(array);
            baseRegionModelDtoList.add(baseRegionModelDto);
        }
        return baseRegionModelDtoList;
    }

    @Override
    public List<BaseRegionModelDto> getRegionModel() {
        CurrentlyLoggedInDto user = (CurrentlyLoggedInDto) redisService.get(TokenConstant.LOGIN_USER_REDIS_KEY + "_" + UserUtil.getUser().getFId());
        List<BaseRegionModelEntity> baseRegionModelEntities = baseRegionModelMapper.selectList(Wrappers.<BaseRegionModelEntity>lambdaQuery()
                .eq(BaseRegionModelEntity::getFProvinces, user.getFProvinces())
                .eq(BaseRegionModelEntity::getFCities, user.getFCities())
                .eq(BaseRegionModelEntity::getFDistrictsCounties, user.getFDistrictsCounties())
                .eq(BaseRegionModelEntity::getFModelMode, "2"));
        return CommonBeanUtils.dtoListTransfer(baseRegionModelEntities, BaseRegionModelDto.class);
    }

    @Override
    public List<BaseRegionModelDto> getModel() {
        List<BaseRegionModelEntity> baseRegionModelEntities = baseRegionModelMapper.selectList(Wrappers.<BaseRegionModelEntity>lambdaQuery()
                .eq(BaseRegionModelEntity::getFModelMode, "3"));
        return CommonBeanUtils.dtoListTransfer(baseRegionModelEntities, BaseRegionModelDto.class);
    }

    @Override
    public String[] getAllModel() {
        List<BaseRegionModelEntity> baseRegionModelEntities = baseRegionModelMapper.selectList(Wrappers.<BaseRegionModelEntity>lambdaQuery()
                .eq(BaseRegionModelEntity::getFModelMode, "1"));
        Set<String> set = new TreeSet<>();
        baseRegionModelEntities.forEach(baseRegionModelEntity -> {
            // 1. 去除花括号 {} 和引号 ""
            String content = baseRegionModelEntity.getFModelClass()
                    .substring(1, baseRegionModelEntity.getFModelClass().length() - 1)  // 去除 {}
                    .replaceAll("\"", "")  // 去除所有引号
                    .replaceAll("\\s+", " ").trim();  // 合并多个空格并去除首尾空格

            // 2. 按逗号分割，并清理每个元素
            String[] parts = content.split(",");
            for (String part : parts) {
                set.add(part.trim());  // 去除空格后加入 Set（自动去重）
            }
        });

        // 3. 将 Set 转为 String[]
        String[] resultArray = set.toArray(new String[0]);
        return resultArray;
    }
}