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
import me.jaejoon.demo.tag.TagService;
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
@RequestMapping("/settings")
public class SettingController {

    static final String ROOT = "/";
    static final String SETTINGS ="settings";
    static final String PROFILE = "/profile";
    static final String PASSWORD = "/password";
    static final String NOTIFICATIONS = "/notifications";
    static final String ACCOUNT = "/account";
    static final String TAGS = "/tags";
    static final String ZONES = "/zones";


    private final AccountService service;
    private final ModelMapper modelMapper;
    private final NicknameFormValidation nicknameFormValidation;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;
    private final TagService tagService;

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(new PasswordValidation());
    }

    @InitBinder("nicknameForm")
    public void initBinder2(WebDataBinder webDataBinder){
        webDataBinder.addValidators(nicknameFormValidation);
    }

    @GetMapping(ZONES)
    public String zoneUpdateForm(@CurrentUser Account account , Model model) throws JsonProcessingException {
        Set<Zone> zones = service.getZoneTag(account);
        model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));

        List<String> collect = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist",objectMapper.writeValueAsString(collect));
        return SETTINGS+ZONES;
    }

    @PostMapping(ZONES +"/add")
    @ResponseBody
    public ResponseEntity zoneUpdate(@CurrentUser Account account, @RequestBody ZoneForm zoneForm){
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(),zoneForm.getProvinceName());
        if(zone == null){
            return ResponseEntity.badRequest().build();
        }
        service.addZone(account, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ZONES +"/remove")
    @ResponseBody
    public ResponseEntity zoneRemove(@CurrentUser Account account, @RequestBody ZoneForm zoneForm){
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(),zoneForm.getProvinceName());
        if(zone == null){
            return ResponseEntity.badRequest().build();
        }
        service.removeZone(account, zone);
        return ResponseEntity.ok().build();
    }



    @GetMapping(TAGS)
    public String tagsUpdateForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
        Set<Tag> tags = service.getTags(account);
        model.addAttribute(account);
        model.addAttribute("tags",tags.stream().map(Tag::getTitle).collect(Collectors.toList()));

        List<String> collect = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist",objectMapper.writeValueAsString(collect));
        return SETTINGS+TAGS;
    }

    @PostMapping(TAGS +"/add")
    @ResponseBody
    public ResponseEntity tageUpdate(@CurrentUser Account account, @RequestBody TagForm tagForm){
        String title = tagForm.getTagTitle();
        Tag tag = tagService.findOrCreateNew(title);
        service.addTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping(TAGS +"/remove")
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

    @GetMapping(ACCOUNT)
    public String accountUpdateForm(@CurrentUser Account account , Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));

        return SETTINGS+ACCOUNT;
    }

    @PostMapping(ACCOUNT)
    public String accountUpdate(@CurrentUser Account account ,@Valid NicknameForm nicknameForm ,Errors errors
            ,Model model ,RedirectAttributes attributes ){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS+ACCOUNT;
        }
        attributes.addFlashAttribute("message","변경 되었습니다");
        service.updateNickName(account,nicknameForm);
        return "redirect:/"+SETTINGS+ACCOUNT;
    }

    @GetMapping(NOTIFICATIONS)
    public String notificationsUpdateForm(@CurrentUser Account account , Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Notifications.class));
        return SETTINGS+NOTIFICATIONS;
    }

    @PostMapping(NOTIFICATIONS)
    public String notificationUpdate(@CurrentUser Account account ,RedirectAttributes attributes
           , @Valid @ModelAttribute Notifications  notifications , Errors errors ,Model model){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS+NOTIFICATIONS;
        }
        attributes.addFlashAttribute("message","변경되었습니다");
        service.updateNotifications(account,notifications);

        return "redirect:/"+SETTINGS+NOTIFICATIONS;
    }
    @GetMapping(PASSWORD)
    public String passwordUpdateForm(Model model){
        model.addAttribute(new PasswordForm());
        return SETTINGS+PASSWORD;
    }
    @PostMapping(PASSWORD)
    public String passwordUpdate(@CurrentUser Account account,@Valid PasswordForm form ,Errors errors
            ,RedirectAttributes attributes){
        if(errors.hasErrors()){
            return SETTINGS+PASSWORD;
        }
        service.updatePassword(account,form);
        attributes.addFlashAttribute("message","패스워드가 변경됨");
        return "redirect:/"+SETTINGS+PASSWORD;
    }
    @GetMapping(PROFILE)
    public String profileUpdateForm(@CurrentUser Account account , Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Profile.class));
        return SETTINGS+PROFILE;
    }

    @PostMapping(PROFILE)
    public String profileUpdate(@CurrentUser Account account, @Valid @ModelAttribute Profile profile,
                                Errors errors , Model model, RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS+PROFILE;
        }
        service.updateProfile(account ,profile);
        redirectAttributes.addFlashAttribute("message","수정이 완료되었습니다");
        return "redirect:/"+SETTINGS+PROFILE;
    }
}
