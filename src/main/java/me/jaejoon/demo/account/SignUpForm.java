package me.jaejoon.demo.account;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class SignUpForm {
    @NotNull
    @Length(min = 3 , max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-](3,20)$")
    private String nickname;

    @NotNull
    @Email
    private String email;
    @NotNull
    @Length(min = 8 ,max =50)
    private String password;
}
