package me.jaejoon.demo.settings;

import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.account.AccountService;
import me.jaejoon.demo.account.CurrentUser;
import me.jaejoon.demo.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingController {
    private static final String SETTINGS_PROFILE_VIEW = "/settings/profile";
    private static final String SETTINGS_PROFILE_MAPPING= "/settings/profile";
    private final AccountService service;
    @GetMapping(SETTINGS_PROFILE_MAPPING)
    public String profileUpdateForm(@CurrentUser Account account , Model model){
        model.addAttribute(account);
        model.addAttribute(new Profile(account));
        return SETTINGS_PROFILE_VIEW;
    }

    @PostMapping(SETTINGS_PROFILE_MAPPING)
    public String profileUpdate(@CurrentUser Account account, @Valid @ModelAttribute Profile profile,
                                Errors errors , Model model, RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW;
        }
        service.updateProfile(account ,profile);
        redirectAttributes.addFlashAttribute("message","수정이 완료되었습니다");
        return "redirect:"+SETTINGS_PROFILE_MAPPING;
    }
}
