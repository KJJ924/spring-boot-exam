package me.jaejoon.demo.validation;

import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.account.AccountRepository;
import me.jaejoon.demo.form.SignUpForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidation  implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        SignUpForm signUpForm = (SignUpForm) o;
        if(accountRepository.existsByEmail(signUpForm.getEmail())){
            errors.rejectValue("email","email.invalid", new Object[]{signUpForm.getEmail()}
            ,"이미 사용중인 이메일입니다");
        }

        if(accountRepository.existsByNickname(signUpForm.getNickname())){
            errors.rejectValue("nickname","nickname.invalid",new Object[]{signUpForm.getNickname()},
            "이미 사용중인 닉네임 입니다");
        }
    }
}
