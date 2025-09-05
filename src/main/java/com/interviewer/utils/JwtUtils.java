package com.interviewer.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewer.core.constant.TokenConstant;
import com.interviewer.core.exception.ServiceException;
import com.interviewer.dto.CurrentlyLoggedInDto;
import com.interviewer.enums.result.SysResultEnum;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hyh
 */
public class JwtUtils {

    //生成令牌环
    public static String generateToken(String userRole, String userid, long exprationtime) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(TokenConstant.ROLE, userRole);
        map.put("userid", userid);
        String jwt = Jwts.builder()
                .setClaims(map)
                .setExpiration(new Date(System.currentTimeMillis() + exprationtime))
                .signWith(SignatureAlgorithm.HS512, TokenConstant.SECRET)
                .compact();
        return TokenConstant.TOKEN_PREFIX + " " + jwt;
    }

    //生成令牌环
    public static String generateTokenUser(CurrentlyLoggedInDto baseUser, long exprationtime) {
        Map<String, Object> userMap = convertToMap(baseUser);
        String jwt = Jwts.builder()
                .setClaims(userMap)
                .setExpiration(new Date(System.currentTimeMillis() + exprationtime))
                .signWith(SignatureAlgorithm.HS512, TokenConstant.SECRET)
                .compact();
        return TokenConstant.TOKEN_PREFIX + " " + jwt;
    }

    //令牌环校验
    public static Map<String, Object> validateTokenAndGetClaims(HttpServletRequest request) {
        String token = request.getHeader(TokenConstant.HEADER_STRING);
        if (token == null) {
            throw new ServiceException(SysResultEnum.WECHAT_ACCESS_TOKEN_FAIL);
        } else {
            Map<String, Object> body = Jwts.parser()
                    .setSigningKey(TokenConstant.SECRET)
                    .parseClaimsJws(token.replace(TokenConstant.TOKEN_PREFIX, ""))
                    .getBody();
            return body;
        }
    }

    public static Map<String, Object> convertToMap(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(obj, Map.class);
    }
}
