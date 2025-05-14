package com.rockapp.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.rockapp.enums.status.EnabledEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityUser implements UserDetails {
    /**
     * 用户基本信息
     */
    private BaseUserEntity sysUser;

    /**
     * 用户权限集合
     */
    private Set<String> permissions;

    /**
     * 角色权限集合
     */
    private Set<String> roles;

    // 权限集合
    @JSONField(serialize = false)
    private List<SimpleGrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",", permissions));
    }

    @JSONField(serialize = false)
    @Override
    public String getPassword() {
        return sysUser.getFPassword();
    }

    @Override
    public String getUsername() {
        return sysUser.getFUserName();
    }

    /**
     * 账号没有过期状态(true账号没有过期，false账号已经过期)
     *
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户没有被锁定
     *
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 身份认证是否是有效的
     *
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账户是否启用
     *
     * @return
     */
    @Override
    public boolean isEnabled() {
        return EnabledEnum.ENABLE.equals(sysUser.getFIsEnabled()) ? true : false;
    }
}
