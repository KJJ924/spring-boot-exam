package me.jaejoon.demo.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PasswordForm {
    private String newPassword;
    private String newPasswordConfirm;

}
