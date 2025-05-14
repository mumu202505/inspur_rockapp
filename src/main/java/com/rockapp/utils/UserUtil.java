package com.rockapp.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rockapp.core.constant.TokenConstant;
import com.rockapp.entity.BaseUserEntity;
import com.rockapp.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Map;
@RequiredArgsConstructor
public class UserUtil {
    public static BaseUserEntity getUser() {
        //获取当前登录人信息
        BaseUserEntity baseUser = null;
        try {
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            for (GrantedAuthority authority : authorities) {
                ObjectMapper mapper = new ObjectMapper();
                baseUser = mapper.convertValue(details, BaseUserEntity.class);
            }
        } catch (Exception e) {
            return null;
        } finally {
            return baseUser;
        }
    }
}
