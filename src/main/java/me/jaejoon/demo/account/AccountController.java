package me.jaejoon.demo.account;

import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.domain.Account;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidation signUpFormValidation;
    private final AccountRepository repository;
    private final JavaMailSender mailSender;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpFormValidation);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid @ModelAttribute SignUpForm signUpForm , Errors errors){
        if(errors.hasErrors()){
            return "account/sign-up";
        }
        Account account = Account.builder()
                .nickname(signUpForm.getNickname())
                .password(signUpForm.getPassword())
                .email(signUpForm.getEmail())
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdateByWeb(true)
                .joinedAt(LocalDateTime.now())
                .build();
        Account newAccount = repository.save(account);
        newAccount.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("회원가입 인증메일");
        mailMessage.setText("/check-email-token?token="+newAccount.getEmailCheckToken()
                +"&email="+newAccount.getEmail());
        mailSender.send(mailMessage);
        return "redirect:/";
    }
}
