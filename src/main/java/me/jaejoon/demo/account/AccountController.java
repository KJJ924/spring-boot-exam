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
    private final AccountService service;
    private final AccountRepository repository;

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
        Account account = service.processNewAccount(signUpForm);
        service.login(account);
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token ,String email, Model model){
        Account account = repository.findByEmail(email);
        String view = "account/checked-email";
        if(account ==null){
            model.addAttribute("error","not.account");
            return view;
        }
        if(!account.emailTokenValid(token)){
            model.addAttribute("error","wrong.token");
            return view;
        }
        account.completeCheck();
        service.login(account);
        model.addAttribute("numberOfUser",repository.count());
        model.addAttribute("nickName",account.getNickname());
        return view;
    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentUser Account account , Model model){
        model.addAttribute(account);
        return "account/check-email";
    }

    @GetMapping("/resend-email")
    public String reSendEmail(@CurrentUser Account account,Model model){
        if (!account.canSendConfirmEmail()){
            model.addAttribute("error","1시간 이내에 이메일을 재전송 할 수 없습니다");
            model.addAttribute("email",account.getEmail());
            return "account/check-email";
        }
        service.sendSignUpConfirmEmail(account);
        return "redirect:/";
    }

}
