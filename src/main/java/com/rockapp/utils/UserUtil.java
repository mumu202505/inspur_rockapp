package com.rockapp.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rockapp.entity.BaseUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
