package me.jaejoon.demo.settings;

import lombok.RequiredArgsConstructor;

import me.jaejoon.demo.account.AccountService;
import me.jaejoon.demo.account.CurrentUser;
import me.jaejoon.demo.domain.Account;

import me.jaejoon.demo.form.Notifications;
import me.jaejoon.demo.form.PasswordForm;
import me.jaejoon.demo.form.Profile;
import me.jaejoon.demo.validation.PasswordValidation;
import org.modelmapper.ModelMapper;

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

    static final String SETTINGS_NOTIFICATION_VIEW_NAME = "/settings/notifications";
    static final String SETTINGS_NOTIFICATION_URL= "/settings/notifications";

    private final AccountService service;
    private final ModelMapper modelMapper;

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(new PasswordValidation());
    }

    @GetMapping(SETTINGS_NOTIFICATION_URL)
    public String notificationsUpdateForm(@CurrentUser Account account , Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Notifications.class));
        return SETTINGS_NOTIFICATION_VIEW_NAME;
    }

    @PostMapping(SETTINGS_NOTIFICATION_URL)
    public String notificationUpdate(@CurrentUser Account account ,RedirectAttributes attributes
           , @Valid @ModelAttribute Notifications  notifications , Errors errors ,Model model){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS_NOTIFICATION_VIEW_NAME;
        }
        attributes.addFlashAttribute("message","변경되었습니다");
        service.updateNotifications(account,notifications);

        return "redirect:"+SETTINGS_NOTIFICATION_VIEW_NAME;
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
        model.addAttribute(modelMapper.map(account, Profile.class));
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
