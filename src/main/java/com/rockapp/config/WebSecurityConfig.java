package com.rockapp.config;

import com.rockapp.core.filter.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 拦截 OPTIONS 方法（禁止直接访问）
                .addFilterBefore(new OncePerRequestFilter() {
                    @Override
                    protected void doFilterInternal(HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    FilterChain filterChain)
                            throws ServletException, IOException {
                        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"OPTIONS method is not allowed\"}");
                            return;
                        }
                        filterChain.doFilter(request, response);
                    }
                }, UsernamePasswordAuthenticationFilter.class)

                // 2. 安全配置
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 启用 Basic Auth（用于 Swagger 登录）
                .httpBasic(withDefaults()) // <-- 关键修改点
                .authorizeHttpRequests(auth -> auth
                        // 禁止 TRACE 方法
                        .requestMatchers(HttpMethod.TRACE).denyAll()

                        // 公开访问的路径
                        .requestMatchers(
                                "/public/**",
                                "/universality/**",
                                "/product/**"
                        ).permitAll()

                        // Swagger 相关路径需要认证（Basic Auth 或 JWT）
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).authenticated()

                        // 其他请求需要认证（JWT）
                        .anyRequest().authenticated()
                )
                // 添加 JWT 过滤器
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    // CORS 配置（保持不变）
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}