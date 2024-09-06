package usercenter.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import usercenter.demo.common.BaseResponse;
import usercenter.demo.common.ErrorCode;
import usercenter.demo.common.ResultUtils;
import usercenter.demo.exception.BusinessException;
import usercenter.demo.model.domain.User;
import usercenter.demo.model.request.UserLoginRequest;
import usercenter.demo.model.request.UserRegisterRequest;
import usercenter.demo.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static usercenter.demo.constant.UserConstant.ADMIN_ROLE;
import static usercenter.demo.constant.UserConstant.USER_LOGIN_STATE;

@RequestMapping("/user")
@RestController
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (currentUser == null) throw new BusinessException(ErrorCode.NOT_LOGIN);
        Long userId = currentUser.getId();
        User user = userService.getById(userId);
        User safeUser = userService.getSafetyUser(user);
        return ResultUtils.success(safeUser);
    }

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest request){

        if (request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = request.getUserAccount();
        String userPassword = request.getUserPassword();
        String checkPassword = request.getCheckPassword();
        String vipCode = request.getVipCode();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,vipCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long result = userService.userRegister(userAccount,userPassword,checkPassword, vipCode);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount,userPassword,request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String userName,HttpServletRequest request){
        System.out.println(request.getSession().getAttribute(USER_LOGIN_STATE));
        if (!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.like(StringUtils.isNotBlank(userName),"userName",userName);
        List<User> list = userService.list(qw);
        System.out.println(list);
        List<User> result = list.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(result);
    }

    @PostMapping("/{delete}")
    public BaseResponse<Boolean> deleteUser(@RequestBody Long id,HttpServletRequest request){
        if (!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    private boolean isAdmin(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}
