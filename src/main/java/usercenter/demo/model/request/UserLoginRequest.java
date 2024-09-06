package usercenter.demo.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {


    private static final long serialVersionUID = -6768942278273385245L;

    private String userAccount;

    private String userPassword;
}
