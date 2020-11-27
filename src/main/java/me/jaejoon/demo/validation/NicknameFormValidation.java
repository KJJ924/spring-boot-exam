package me.jaejoon.demo.validation;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.account.AccountRepository;
import me.jaejoon.demo.form.NicknameForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class NicknameFormValidation implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(NicknameForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
       NicknameForm nicknameForm = (NicknameForm) target;
        boolean result = accountRepository.existsByNickname(nicknameForm.getNickname());
        if(result){
           errors.rejectValue("nickname","nickname.duplicate"
                   ,"이미 존재하는 닉네임입니다.");
       }
    }
}
