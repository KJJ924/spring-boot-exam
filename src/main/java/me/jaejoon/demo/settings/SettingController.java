package me.jaejoon.demo.settings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.account.AccountService;
import me.jaejoon.demo.account.CurrentUser;
import me.jaejoon.demo.account.PasswordForm;
import me.jaejoon.demo.account.PasswordValidation;
import me.jaejoon.demo.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingController {
    static final String SETTINGS_PROFILE_VIEW_NAME = "/settings/profile";
    static final String SETTINGS_PROFILE_URL= "/settings/profile";

    static final String SETTINGS_PASSWORD_VIEW_NAME = "/settings/password";
    static final String SETTINGS_PASSWORD_URL= "/settings/password";

    private final AccountService service;
    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(new PasswordValidation());
    }

    @GetMapping(SETTINGS_PASSWORD_URL)
    public String passwordUpdateForm(Model model){
        model.addAttribute(new PasswordForm());
        return SETTINGS_PASSWORD_VIEW_NAME;
    }
    @PostMapping(SETTINGS_PASSWORD_URL)
    public String passwordUpdate(@CurrentUser Account account,@Valid PasswordForm form ,Errors errors
            ,RedirectAttributes attributes){
        if(errors.hasErrors()){
            return SETTINGS_PASSWORD_URL;
        }
        service.updatePassword(account,form);
        attributes.addFlashAttribute("message","패스워드가 변경됨");
        return "redirect:"+SETTINGS_PASSWORD_VIEW_NAME;
    }
    @GetMapping(SETTINGS_PROFILE_URL)
    public String profileUpdateForm(@CurrentUser Account account , Model model){
        model.addAttribute(account);
        model.addAttribute(new Profile(account));
        return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    public String profileUpdate(@CurrentUser Account account, @Valid @ModelAttribute Profile profile,
                                Errors errors , Model model, RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }
        service.updateProfile(account ,profile);
        redirectAttributes.addFlashAttribute("message","수정이 완료되었습니다");
        return "redirect:"+SETTINGS_PROFILE_URL;
    }
}
