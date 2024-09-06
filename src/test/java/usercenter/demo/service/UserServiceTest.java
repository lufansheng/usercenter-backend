package usercenter.demo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import usercenter.demo.model.domain.User;

import javax.annotation.Resource;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void addUser(){
        User user = new User();
        user.setUsername("lufansheng");
        user.setUserAccount("lufansheng");
        user.setAvatarUrl("https://meeting-75420.picgzc.qpic.cn/bdb03945e6cb21c447b9c13eca8690a9bc4f57b1d3236e81ab49345ad5b73b4d");
        user.setGender((byte) 0);
        user.setUserPassword("123456");
        user.setEmail("3324223450@qq.com");
        user.setPhone("19859475029");
        boolean result = userService.save(user);
        Assertions.assertTrue(result);

        System.out.println(user.getId());
    }

    @Test
    void userRegister() {
        String userAccount = "lufansheng1";
        String userPassword = "";
        String checkPassword = "123456";
        String vipCode = "1";
        long result = userService.userRegister(userAccount, userPassword, checkPassword, vipCode);
        //密码不能为空
        Assertions.assertEquals(-1,result);

        userAccount = "";
        userPassword = "123456";
        checkPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword, vipCode);
        //账号不能为空
        Assertions.assertEquals(-1,result);

        userAccount = "lufansheng";
        userPassword = "123456";
        checkPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword, vipCode);
        //账号不能重复
        Assertions.assertEquals(-1,result);

        userAccount = "lu";
        userPassword = "123456";
        checkPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword,vipCode );
        //账号长度>=4
        Assertions.assertEquals(-1,result);

        userAccount = "lufansheng1";
        userPassword = "12";
        checkPassword = "12";
        result = userService.userRegister(userAccount, userPassword, checkPassword,vipCode );
        //密码长度>=6
        Assertions.assertEquals(-1,result);

        userAccount = "lufansheng1";
        userPassword = "1234567";
        checkPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword,vipCode );
        //账号密码要相同
        Assertions.assertEquals(-1,result);

        userAccount = "lufansheng1?";
        userPassword = "123456";
        checkPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword,vipCode );
        //不能包含特殊字符串
        Assertions.assertEquals(-1,result);

        userAccount = "lufansheng33";
        userPassword = "123456";
        checkPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword,vipCode );
        //不能包含特殊字符串
        Assertions.assertEquals(33,result);
    }
}