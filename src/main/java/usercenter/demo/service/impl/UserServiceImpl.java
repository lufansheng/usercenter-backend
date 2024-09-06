package usercenter.demo.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import usercenter.demo.common.ErrorCode;
import usercenter.demo.exception.BusinessException;
import usercenter.demo.mapper.UserMapper;
import usercenter.demo.model.domain.User;
import usercenter.demo.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    private static final String SALT = "lfs";

    private static final String USER_LOGIN_STATE = "userLoginState";

    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String vipCode) {
        System.out.println("进入该方法");
        //传入的字符串非空
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,vipCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"有空字符串");
        }

        if (vipCode.length() > 6){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"VIP编号长度大于6");
        }

        //账号>=4
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号短");
        }
        //密码>=6
        if (userPassword.length() < 6 || checkPassword.length() < 6){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码短");
        }

        //正则表达式匹配规则，不能包含特色字符串
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){

            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }

        //密码相同
        if (!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码要相同");
        }

        //账号不能重复
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("userAccount",userAccount);
        Long count = userMapper.selectCount(qw);
        if (count > 0){

            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号重复");
        }

        qw = new QueryWrapper<>();
        qw.eq("vipCode",userAccount);
        count = userMapper.selectCount(qw);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号重复");
        }

        //加密

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userAccount).getBytes());
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setVipCode(vipCode);
        boolean Result = this.save(user);
        if (!Result){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"插入失败");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //传入的字符串非空
        System.out.println("登入");
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            System.out.println("有空字符串");
            return null;
        }

        //账号>=4
        if (userAccount.length() < 4){
            System.out.println("账号短");
            return null;
        }
        //密码>=6
        if (userPassword.length() < 6){
            System.out.println("密码短");
            return null;
        }

        //正则表达式匹配规则，不能包含特色字符串
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            System.out.println("账号包含特殊字符");
            return null;
        }

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userAccount).getBytes());

        //账号不能重复
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("userAccount",userAccount);
        qw.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(qw);
        if (user == null){
            log.info("user login failed,userAccount cannot match");
            return null;
        }

        //脱敏
        User safetyUser = getSafetyUser(user);

        //到了这里代表登入成功，记录一下用户登入状态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);

        return safetyUser;

    }

    @Override
    public User getSafetyUser(User originUser){
        if (originUser == null) return null;
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setVipCode(originUser.getVipCode());
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




