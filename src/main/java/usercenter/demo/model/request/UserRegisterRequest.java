package usercenter.demo.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -6134444761411319404L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String vipCode;
}
