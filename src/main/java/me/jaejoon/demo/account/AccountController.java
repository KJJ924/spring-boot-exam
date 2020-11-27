package me.jaejoon.demo.account;

import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.domain.Account;
import me.jaejoon.demo.form.SignUpForm;
import me.jaejoon.demo.validation.SignUpFormValidation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

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
        service.completeCheck(account);
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

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname , Model model ,@CurrentUser Account account){
        Account byNickname = repository.findByNickname(nickname);
        if (byNickname == null){
            throw new IllegalArgumentException(nickname+"에 해당하는 계정이 존재하지 않습니다. ");
        }
        // account 로 모델에 담김
        model.addAttribute(byNickname);
        // 내가 생각한건 if 문으로 구분하여 값을 셋팅 할려고 생각했는데 아래 방법이 있었음.
        model.addAttribute("isOwner",byNickname.equals(account));

        return "account/profile";
    }

    @GetMapping("/email-login")
    public String emailLoginForm() {
        return "account/email-login";
    }

    @GetMapping("/check-login-email")
    public String checkEmailPage() {
        return "account/check-login-email";
    }

    @PostMapping("/email-login")
    public String emailLogin(@RequestParam String email , Model model , RedirectAttributes attributes){
        String view = "account/email-login";
        Account account = repository.findByEmail(email);
        if(account == null){
            model.addAttribute("error","존재하지 않는 이메일입니다 ");
            model.addAttribute("email",email);
            return view;
        }

        if(!account.canSendConfirmEmail()){
            model.addAttribute("error","1시간 이내에 이메일 로그인을 여러번 요청할 수 없습니다");
            return view;
        }
        service.sendLoginLink(account);
        attributes.addFlashAttribute("email", email);
        return "redirect:/check-login-email";
    }

    @GetMapping("/login-by-email")
    public String loginByEmail(String token, String email, Model model) {
        String view = "account/logged-in-by-email";
        Account account = repository.findByEmail(email);
        if(account == null || !account.emailTokenValid(token)){
            model.addAttribute("error", "로그인할 수 없습니다.");
            return view;
        }
        service.login(account);
        return view;
    }
}
