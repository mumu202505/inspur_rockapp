package com.interviewer.core.handle;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.interviewer.entity.BaseUserEntity;
import com.interviewer.utils.AesUtil;
import com.interviewer.utils.UserUtil;
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
        this.setFieldValByName("createTime", new Date(), metaObject);
        // 更新时间使用当前时间填充
        this.setFieldValByName("lastModifyTime", new Date(), metaObject);
        // 创建人id使用当前登录人id填充，没有的话默认设置1
        BaseUserEntity user = UserUtil.getUser();
        if (ObjectUtils.isNotEmpty(user)){
            this.setFieldValByName("createUserId", Optional.ofNullable(user.getId()).orElse("1"), metaObject);
            this.setFieldValByName("createUserName", Optional.ofNullable(user.getUserName()).orElse("1"), metaObject);
        }
        // 更新人id使用当前登录人id填充，没有的话默认设置1
        if (ObjectUtils.isNotEmpty(user)){
            this.setFieldValByName("lastModifyUserId", Optional.ofNullable(user.getId()).orElse("1"), metaObject);
            this.setFieldValByName("lastModifyUserName", Optional.ofNullable(user.getUserName()).orElse("1"), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时间使用当前时间填充
        this.setFieldValByName("lastModifyTime", new Date(), metaObject);
        // 更新人id使用当前登录人id填充，没有的话默认设置1
        this.setFieldValByName("lastModifyUserId", Optional.ofNullable(UserUtil.getUser().getId()).orElse("1"), metaObject);
        this.setFieldValByName("lastModifyUserName", Optional.ofNullable(UserUtil.getUser().getUserName()).orElse("1"), metaObject);
    }



}

