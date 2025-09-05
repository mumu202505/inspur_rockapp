package com.interviewer.core.filter;

import com.interviewer.core.constant.TokenConstant;
import com.interviewer.core.exception.ServiceException;
import com.interviewer.enums.result.SysResultEnum;
import com.interviewer.service.RedisService;
import com.interviewer.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;


/**
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private RedisService redisService;

    private static final PathMatcher pathmatcher = new AntPathMatcher();
    //哪些请求需要进行安全校验
    private String[] protectUrlPattern = {"/manage/**",
            "/member/**",
            "/auth/**",
            "/userProject/**",
            "/recognitionReport/**",
            "/recognitionResult/**",
            "/ai/**",
            "/file/**"
    };

    public JwtAuthenticationFilter() {

    }


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        log.info("##jwtAuthen认证处理开始##");
        try {
            if (isProtectedUrl(httpServletRequest)) {

                Map<String, Object> claims = JwtUtils.validateTokenAndGetClaims(httpServletRequest);
                String role = String.valueOf(claims.get("frole"));
                String userid = String.valueOf(claims.get("fid"));
                String remoteAddr = httpServletRequest.getRemoteAddr();
                String loginIp = String.valueOf(claims.get("loginIp"));
                if (!remoteAddr.equals(loginIp)) {
                    throw new ServiceException(SysResultEnum.APP_USER_IP);
                }
                //TODO 获取到令牌信息之后和redis中的数据进行对比，如果令牌过期，则返回错误信息
                if (!redisService.hasKey(TokenConstant.ACCESS_TOKEN + "_" + userid)) {
                    redisService.del(TokenConstant.LOGIN_USER_REDIS_KEY + "_" + userid);
                    throw new ServiceException(SysResultEnum.APP_USER_TIMEOUT);
                }else {
                    String authorizationHeader = httpServletRequest.getHeader("Passport");
                    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                        authorizationHeader = authorizationHeader.substring(7); // 去掉"Bearer "前缀
                    }
                    Object o = redisService.get(TokenConstant.ACCESS_TOKEN + "_" + userid);
                    String token = String.valueOf(o).substring(7);
                    if (!token.equals(authorizationHeader)){
                        throw new ServiceException(SysResultEnum.APP_USER_TIMEOUT);
                    }
                }
                //最关键的部分就是这里, 我们直接注入了
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userid, null, Arrays.asList(() -> role)
                );
                authentication.setDetails(claims);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            e.printStackTrace();
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    //是否是保护连接
    private boolean isProtectedUrl(HttpServletRequest request) {
        boolean flag = false;
        for (int i = 0; i < protectUrlPattern.length; i++) {
            if (pathmatcher.match(protectUrlPattern[i], request.getServletPath())) {
                return true;
            }
        }
        return false;
    }
}
