package com.rockapp.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rockapp.core.constant.TokenConstant;
import com.rockapp.core.exception.ServiceException;
import com.rockapp.dto.*;
import com.rockapp.entity.BaseUserEntity;
import com.rockapp.enums.result.ResultStatusEnum;
import com.rockapp.enums.result.SysResultEnum;
import com.rockapp.mapper.BaseUserMapper;
import com.rockapp.service.BaseUserService;
import com.rockapp.service.RedisService;
import com.rockapp.utils.*;
import com.rockapp.vo.TokenVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;


@Service("baseUserService")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BaseUserServiceImpl extends ServiceImpl<BaseUserMapper, BaseUserEntity> implements BaseUserService {

    private final RedisService redisService;
    @Autowired
    private BaseUserMapper baseUserMapper;


    @Override
    public void insertUser(AccountDto account) {
        if (validate(account.getFPassword())) {
            BaseUserEntity baseUser = baseUserMapper.selectOne(Wrappers.<BaseUserEntity>query().lambda().eq(BaseUserEntity::getFUserName, account.getFUserName()));
            if (ObjectUtils.isNotEmpty(baseUser)) {
                throw new ServiceException(SysResultEnum.USER_CODE_EXISTED);
            } else {
                Map<String, String> map = encryptPassword(account.getFPassword());
                BaseUserEntity user = new BaseUserEntity()
                        .setFUserName(account.getFUserName())
                        .setFNickname(account.getFNickname())
                        .setFPassword(map.get("password"))
                        .setFSalt(map.get("salt"));
                baseUserMapper.insert(user);
            }
        }
    }


    @Override
    public void updatepsd(BaseUserPassWordDto baseUserPassWordDto) {
        log.info("===============进入根据原密码修改密码方式============");
        //验证原密码
        //1.密码密文解密
        CurrentlyLoggedInDto baseUserDto = baseUserLogin(UserUtil.getUser().getFUserName(), AesUtil.decrypt(baseUserPassWordDto.getFOldPassWord()));
        if (ObjectUtils.isNotEmpty(baseUserDto)) {
            //2.密码加密
            String password = AesUtil.decrypt(baseUserPassWordDto.getFPassWord());
            Map<String, String> map = encryptPassword(password);
            //3.修改密码
            baseUserMapper.update(null,
                    Wrappers.<BaseUserEntity>update().lambda().
                            eq(BaseUserEntity::getFId, UserUtil.getUser().getFId()).
                            set(BaseUserEntity::getFPassword, map.get("password")).
                            set(BaseUserEntity::getFSalt, map.get("salt")));
            //4.删除redis中原用户token和用户信息
            redisService.del(TokenConstant.ACCESS_TOKEN + "_" + UserUtil.getUser().getFId());
            redisService.del(TokenConstant.LOGIN_USER_REDIS_KEY + "_" + UserUtil.getUser().getFId());
        }
    }

    @Override
    public CurrentlyLoggedInDto getUserInfo() {
        CurrentlyLoggedInDto user = (CurrentlyLoggedInDto) redisService.get(TokenConstant.LOGIN_USER_REDIS_KEY + "_" + UserUtil.getUser().getFId());
        return user;
    }

    @Override
    public void updateUser(BaseUserDto baseUserDto) {
        log.info("===============进入修改用户信息方式============");
        //修改用户信息
        BaseUserEntity baseUser = CommonBeanUtils.dtoTransfer(baseUserDto, BaseUserEntity.class);
        baseUserMapper.updateById(baseUser);
        BaseUserEntity user = baseUserMapper.selectOne(Wrappers.<BaseUserEntity>lambdaQuery()
                .eq(BaseUserEntity::getFId, UserUtil.getUser().getFId()));
        //刷新缓存中用户信息
        updateRdisUser(user);
    }

    @Override
    public void updatePhone(UpdatePhoneDto updatePhoneDto) {
        log.info("===============进入绑定用户手机号方式============");
        //校验手机号是否唯一
        if (baseUserMapper.selectOne(Wrappers.<BaseUserEntity>lambdaQuery()
                .eq(BaseUserEntity::getFPhone, updatePhoneDto.getFPhone())) != null) {
            throw new ServiceException(SysResultEnum.BINDING_PHONE_ERROR);
        }
        // TODO 添加验证码方式
        String code = String.valueOf(redisService.get(TokenConstant.PHONE_CODE + "_" + updatePhoneDto.getFPhone()));
        if (code == null) {
            //验证码过期
            throw new ServiceException(SysResultEnum.INVALID_CAPTCHA);
        }
        if (!code.equals(updatePhoneDto.getCode())) {
            //验证码不通过
            throw new ServiceException(SysResultEnum.ERROR_CAPTCHA);
        } else {
            //删除redis中手机验证码
            redisService.del(TokenConstant.PHONE_CODE + "_" + updatePhoneDto.getFPhone());
            //验证码通过，修改手机号
            baseUserMapper.update(Wrappers.<BaseUserEntity>lambdaUpdate()
                    .set(BaseUserEntity::getFPhone, updatePhoneDto.getFPhone())
                    .eq(BaseUserEntity::getFId, UserUtil.getUser().getFId()));
            BaseUserEntity user = baseUserMapper.selectOne(Wrappers.<BaseUserEntity>lambdaQuery()
                    .eq(BaseUserEntity::getFId, UserUtil.getUser().getFId()));
            //刷新缓存中用户信息
            updateRdisUser(user);
        }
    }

    @Override
    public TokenVO login(AccountLoginDto account, HttpServletRequest httpServletRequest) {
        log.info("===============进入后端管理认证 用户名和密码的登录方式============");
        log.info("##account={}", account);
        if (Objects.isNull(account)) {
            log.error("参数错误");
            throw new ServiceException(ResultStatusEnum.ERROR_ARG);
        }
        //密码密文解密
        String password = AesUtil.decrypt(account.fPassword);
        CurrentlyLoggedInDto baseUser = null;
        try {
            //验证密码是否正确
            baseUser = baseUserLogin(account.fUserName, password);
            if (ObjectUtils.isNull(baseUser)) {
                throw new ServiceException(SysResultEnum.USER_NAME_PASSWORD_ERROR);
            }
            String ip = httpServletRequest.getRemoteAddr();
            baseUser.setLoginIp(ip);
            baseUser.setLoginTime(new Date());
        } catch (Exception e) {
            log.error("##登录认证失败:{}", e.getMessage());
            throw new ServiceException(SysResultEnum.USER_NAME_PASSWORD_ERROR);
        }
        // 如果认证通过，使用user生成jwt jwt存入ResultUtil 返回
        TokenVO tokenVO = new TokenVO();
        String accessToken = JwtUtils.generateTokenUser(baseUser, TokenConstant.TOKEN_IN);
        //把token存入redis
        redisService.set(TokenConstant.ACCESS_TOKEN + "_" + baseUser.getFId(), accessToken, TokenConstant.EXPIRES_IN);
        // 把完整的用户信息存入redis
        redisService.set(TokenConstant.LOGIN_USER_REDIS_KEY + "_" + baseUser.getFId(), baseUser, TokenConstant.EXPIRES_IN);
        tokenVO.setAccess_token(accessToken).setExpires_in(TokenConstant.EXPIRES_IN).setUser_info(baseUser);
        return tokenVO;
    }

    @Override
    public void logout() {
        // 清除redis中的token和用户信息
        if (redisService.hasKey(TokenConstant.ACCESS_TOKEN + "_" + UserUtil.getUser().getFId())) {
            redisService.del(TokenConstant.ACCESS_TOKEN + "_" + UserUtil.getUser().getFId());
            redisService.del(TokenConstant.LOGIN_USER_REDIS_KEY + "_" + UserUtil.getUser().getFId());
        } else {
            throw new ServiceException(SysResultEnum.APP_USER_TIMEOUT);
        }
    }

    @Override
    public void sign(AccountDto account) {
        log.info("===============进入用户注册方式============");
        if (Objects.isNull(account)) {
            log.error("参数错误");
            throw new ServiceException(ResultStatusEnum.ERROR_ARG);
        }
        //密码密文解密
        account.setFPassword(AesUtil.decrypt(account.fPassword));
        //插入用户
        insertUser(account);
    }

    @Override
    public void getBackPsd(UpdatePasswordDto updatePasswordDto) {
        log.info("===============进入找回密码方式============");
        // TODO 添加验证码方式
        String code = String.valueOf(redisService.get(TokenConstant.PHONE_CODE + "_" + updatePasswordDto.getFPhone()));
        if (code == null) {
            //验证码过期
            throw new ServiceException(SysResultEnum.INVALID_CAPTCHA);
        }
        if (!code.equals(updatePasswordDto.getCode())) {
            //验证码不通过
            throw new ServiceException(SysResultEnum.ERROR_CAPTCHA);
        }

        //验证码通过，修改密码
        //密码密文解密
        String password = AesUtil.decrypt(updatePasswordDto.getFPassword());
        //随机生成盐值
        String salt = PasswordUtil.generateSalt();
        //密码加密
        Map<String, String> map = PasswordUtil.encryptPassword(password, salt);
        baseUserMapper.update(Wrappers.<BaseUserEntity>lambdaUpdate()
                .set(BaseUserEntity::getFPassword, map.get("password"))
                .set(BaseUserEntity::getFSalt, map.get("salt"))
                .eq(BaseUserEntity::getFPhone, updatePasswordDto.getFPhone()));
        //删除redis中用户token和用户信息（登录之后）
        if (ObjectUtils.isNotEmpty(UserUtil.getUser())) {
            redisService.del(TokenConstant.ACCESS_TOKEN + "_" + UserUtil.getUser().getFId());
            redisService.del(TokenConstant.LOGIN_USER_REDIS_KEY + "_" + UserUtil.getUser().getFId());
            //删除redis中手机验证码
            redisService.del(TokenConstant.PHONE_CODE + "_" + updatePasswordDto.getFPhone());
        }
    }

    /**
     * 校验密码规则
     *
     * @param password
     * @return
     */
    public Boolean validate(String password) {
        final int MIN_LENGTH = 8;
        final Pattern PATTERN = Pattern.compile(
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=._])(?=\\S+$).{" + MIN_LENGTH + ",}$"
        );
        if (password == null || !PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException(
                    "密码必须包含大小写字母、数字和特殊字符，且长度至少" + MIN_LENGTH + "位"
            );
        }
        return true;
    }

    /**
     * 用户密码加密
     *
     * @param password
     * @return
     */
    public Map<String, String> encryptPassword(String password) {
        try {
            //随机生成盐值
            String salt = PasswordUtil.generateSalt();
            //密码加密
            Map<String, String> map = PasswordUtil.encryptPassword(password, salt);
            return map;
        } catch (Exception e) {
            throw new ServiceException(SysResultEnum.ENCRYPT_PASSWORD_ERROR);
        }
    }

    /**
     * 刷新用户信息
     *
     * @param user
     */
    void updateRdisUser(BaseUserEntity user) {
        redisService.set(
                TokenConstant.LOGIN_USER_REDIS_KEY + "_" + UserUtil.getUser().getFId(),
                CommonBeanUtils.dtoTransfer(user, CurrentlyLoggedInDto.class),
                TokenConstant.EXPIRES_IN
        );
    }

    /**
     * 验证密码是否正确
     *
     * @param userName
     * @param password
     * @return
     */
    CurrentlyLoggedInDto baseUserLogin(String userName, String password) {
        //获取数据中用户信息
        BaseUserEntity baseUser = baseUserMapper.selectOne(Wrappers.<BaseUserEntity>query().lambda().
                eq(BaseUserEntity::getFUserName, userName));
        CurrentlyLoggedInDto baseUserDto = CommonBeanUtils.dtoTransfer(baseUser, CurrentlyLoggedInDto.class);
        //验证密码是否正确
        boolean b = PasswordUtil.verifyPassword(password, baseUser.getFSalt(), baseUser.getFPassword());
        if (b) {
            return baseUserDto;
        }
        return null;
    }

}