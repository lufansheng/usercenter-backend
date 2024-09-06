package usercenter.demo.service;


import com.baomidou.mybatisplus.extension.service.IService;
import usercenter.demo.model.domain.User;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface UserService extends IService<User> {



    long userRegister(String userAccount,String userPassword,String checkPassword,String vipCode);

    /**
     *
     * @param userAccount 账号
     * @param userPassword 密码
     * @param request
     * @return
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getSafetyUser(User originUser);

    int userLogout(HttpServletRequest request);
}
