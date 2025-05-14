package com.rockapp.core.handle;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.rockapp.entity.BaseUserEntity;
import com.rockapp.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // 创建时间使用当前时间填充
        this.setFieldValByName("fCreateTime", new Date(), metaObject);
        // 更新时间使用当前时间填充
        this.setFieldValByName("fLastModifyTime", new Date(), metaObject);
        // 创建人id使用当前登录人id填充，没有的话默认设置1
        BaseUserEntity user = UserUtil.getUser();
        if (ObjectUtils.isNotEmpty(user)){
            this.setFieldValByName("fCreateUserId", Optional.ofNullable(user.getFId()).orElse("1"), metaObject);
            this.setFieldValByName("fCreateUserName", Optional.ofNullable(user.getFUserName()).orElse("1"), metaObject);
        }
        // 更新人id使用当前登录人id填充，没有的话默认设置1
        if (ObjectUtils.isNotEmpty(user)){
            this.setFieldValByName("fLastModifyUserId", Optional.ofNullable(user.getFId()).orElse("1"), metaObject);
            this.setFieldValByName("fLastModifyUserName", Optional.ofNullable(user.getFUserName()).orElse("1"), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时间使用当前时间填充
        this.setFieldValByName("fLastModifyTime", new Date(), metaObject);
        // 更新人id使用当前登录人id填充，没有的话默认设置1
        this.setFieldValByName("fLastModifyUserId", Optional.ofNullable(UserUtil.getUser().getFId()).orElse("1"), metaObject);
        this.setFieldValByName("fLastModifyUserName", Optional.ofNullable(UserUtil.getUser().getFUserName()).orElse("1"), metaObject);
    }

}

