package com.rockapp.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 实体转换工具类
 *
 * @author cheng
 */
public class CommonBeanUtils {

    /**
     * dto 转换为Do 工具类
     */
    public static <T> T dtoTransfer(Object sourceEntity, Class<T> targetClass) {
        // 判断dto是否为空!
        if (sourceEntity == null) {
            return null;
        }
        // 判断DoClass 是否为空
        if (targetClass == null) {
            return null;
        }
        try {
            T newInstance = targetClass.newInstance();
            BeanUtils.copyProperties(sourceEntity, newInstance);
            // Dto转换Do
            return newInstance;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * do 转换为Dto 工具类
     */
    public static <T> List<T> dtoListTransfer(List<?> sourceEntityList, Class<T> targetClass) {
        // 判断dto是否为空!
        if (CollectionUtils.isEmpty(sourceEntityList)) {
            return null;
        }
        // 判断DoClass 是否为空
        if (targetClass == null) {
            return null;
        }
        try {
            List<T> objects = new ArrayList<>();
            for (Object object : sourceEntityList) {
                T newInstance = targetClass.newInstance();
                BeanUtils.copyProperties(object, newInstance);
                objects.add(newInstance);
            }
            // Dto转换Do
            return objects;
        } catch (Exception e) {
            return null;
        }
    }

}


