package me.jaejoon.demo.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.account.AccountService;
import me.jaejoon.demo.account.CurrentUser;
import me.jaejoon.demo.domain.Account;
import me.jaejoon.demo.domain.Tag;
import me.jaejoon.demo.domain.Zone;
import me.jaejoon.demo.form.*;
import me.jaejoon.demo.tag.TagRepository;
import me.jaejoon.demo.validation.NicknameFormValidation;
import me.jaejoon.demo.validation.PasswordValidation;
import me.jaejoon.demo.zone.ZoneRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SettingController {
    static final String SETTINGS_PROFILE_VIEW_NAME = "/settings/profile";
    static final String SETTINGS_PROFILE_URL= "/settings/profile";

    static final String SETTINGS_PASSWORD_VIEW_NAME = "/settings/password";
    static final String SETTINGS_PASSWORD_URL= "/settings/password";

    static final String SETTINGS_NOTIFICATION_VIEW_NAME = "/settings/notifications";
    static final String SETTINGS_NOTIFICATION_URL= "/settings/notifications";

    static final String SETTINGS_ACCOUNT_VIEW_NAME = "/settings/account";
    static final String SETTINGS_ACCOUNT_URL= "/settings/account";

    static final String SETTINGS_TAGS_VIEW_NAME = "/settings/tags";
    static final String SETTINGS_TAGS_URL= "/settings/tags";

    static final String SETTINGS_ZONES_VIEW_NAME = "/settings/zones";
    static final String SETTINGS_ZONES_URL= "/settings/zones";


    private final AccountService service;
    private final ModelMapper modelMapper;
    private final NicknameFormValidation nicknameFormValidation;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(new PasswordValidation());
    }

    @InitBinder("nicknameForm")
    public void initBinder2(WebDataBinder webDataBinder){
        webDataBinder.addValidators(nicknameFormValidation);
    }

    @GetMapping(SETTINGS_ZONES_URL)
    public String zoneUpdateForm(@CurrentUser Account account , Model model) throws JsonProcessingException {
        Set<Zone> zones = service.getZoneTag(account);
        model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));

        List<String> collect = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist",objectMapper.writeValueAsString(collect));
        return SETTINGS_ZONES_VIEW_NAME;
    }

    @PostMapping(SETTINGS_ZONES_URL+"/add")
    @ResponseBody
    public ResponseEntity zoneUpdate(@CurrentUser Account account, @RequestBody ZoneForm zoneForm){
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(),zoneForm.getProvinceName());
        if(zone == null){
            return ResponseEntity.badRequest().build();
        }
        service.addZone(account, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping(SETTINGS_ZONES_URL+"/remove")
    @ResponseBody
    public ResponseEntity zoneRemove(@CurrentUser Account account, @RequestBody ZoneForm zoneForm){
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(),zoneForm.getProvinceName());
        if(zone == null){
            return ResponseEntity.badRequest().build();
        }
        service.removeZone(account, zone);
        return ResponseEntity.ok().build();
    }



    @GetMapping(SETTINGS_TAGS_URL)
    public String tagsUpdateForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
        Set<Tag> tags = service.getTags(account);
        model.addAttribute(account);
        model.addAttribute("tags",tags.stream().map(Tag::getTitle).collect(Collectors.toList()));

        List<String> collect = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whiteList",objectMapper.writeValueAsString(collect));
        return SETTINGS_TAGS_VIEW_NAME;
    }

    @PostMapping(SETTINGS_TAGS_URL+"/add")
    @ResponseBody
    public ResponseEntity tageUpdate(@CurrentUser Account account, @RequestBody TagForm tagForm){
        String title = tagForm.getTagTitle();
        Tag tag =  tagRepository.findByTitle(title);
        if(tag == null){
            tag = tagRepository.save(Tag.builder().title(title).build());
        }
        service.addTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping(SETTINGS_TAGS_URL+"/remove")
    @ResponseBody
    public ResponseEntity tageRemove(@CurrentUser Account account , @RequestBody TagForm tagForm){
        String title = tagForm.getTagTitle();
        Tag byTitle = tagRepository.findByTitle(title);
        if(byTitle == null){
            return ResponseEntity.badRequest().build();
        }
        service.removeTag(account,byTitle);
        return ResponseEntity.ok().build();
    }

    @GetMapping(SETTINGS_ACCOUNT_URL)
    public String accountUpdateForm(@CurrentUser Account account , Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));

        return SETTINGS_ACCOUNT_VIEW_NAME;
    }

    @PostMapping(SETTINGS_ACCOUNT_URL)
    public String accountUpdate(@CurrentUser Account account ,@Valid NicknameForm nicknameForm ,Errors errors
            ,Model model ,RedirectAttributes attributes ){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS_ACCOUNT_VIEW_NAME;
        }
        attributes.addFlashAttribute("message","변경 되었습니다");
        service.updateNickName(account,nicknameForm);
        return "redirect:"+SETTINGS_ACCOUNT_VIEW_NAME;
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
