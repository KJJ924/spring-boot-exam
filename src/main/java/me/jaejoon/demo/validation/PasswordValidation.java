package me.jaejoon.demo.validation;

import me.jaejoon.demo.form.PasswordForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PasswordValidation implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PasswordForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordForm form = (PasswordForm) target;
        if(!form.getNewPassword().equals(form.getNewPasswordConfirm())){
            errors.rejectValue("newPassword" ,"wrong.value","패스워드가 일치하지 않음");
        }

    }
}
