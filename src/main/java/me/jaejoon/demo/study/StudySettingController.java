package me.jaejoon.demo.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.account.CurrentUser;
import me.jaejoon.demo.domain.Account;
import me.jaejoon.demo.domain.Study;
import me.jaejoon.demo.domain.Tag;
import me.jaejoon.demo.domain.Zone;
import me.jaejoon.demo.form.TagForm;
import me.jaejoon.demo.form.ZoneForm;
import me.jaejoon.demo.study.form.StudyDescriptionForm;
import me.jaejoon.demo.tag.TagRepository;
import me.jaejoon.demo.tag.TagService;
import me.jaejoon.demo.zone.ZoneRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("/study/{path}/settings")
public class StudySettingController {

    private final ModelMapper modelMapper;
    private final StudyService studyService;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;


    @GetMapping("/description")
    public String viewStudySetting(@CurrentUser Account account, Model model, @PathVariable String path){
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(study,StudyDescriptionForm.class));
        return "study/description";
    }

    @PostMapping("/description")
    public String updateStudyInfo(@CurrentUser Account account, @PathVariable String path,
                                  Model model, @Valid  StudyDescriptionForm studyDescriptionForm, Errors errors,
                                  RedirectAttributes attributes){
        Study study = studyService.getStudyToUpdate(account, path);
        if(errors.hasErrors()){
            model.addAttribute(account);
            model.addAttribute(study);
            return "study/description";
        }
        studyService.updateStudyDescription(study,studyDescriptionForm);
        attributes.addFlashAttribute("message","스터디 소개를 수정했습니다.");
        return "redirect:/study/"+ getPath(path)+"/settings/description";
    }
    private String getPath(String path){
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    @GetMapping("banner")
    public String viewStudyBanner(@CurrentUser Account account , @PathVariable String path, Model model){
        Study studyToUpdate = studyService.getStudyToUpdate(account, path);
        model.addAttribute(studyToUpdate);
        model.addAttribute(account);
        return "study/banner";
    }
    @PostMapping("/banner")
    public String studyImageSubmit(@CurrentUser Account account, @PathVariable String path,
                                   String image, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.updateStudyImage(study, image);
        attributes.addFlashAttribute("message", "스터디 이미지를 수정했습니다.");
        return "redirect:/study/" + getPath(path) + "/settings/banner";
    }

    @PostMapping("banner/enable")
    public String bannerEnable(@CurrentUser Account account , @PathVariable String path,RedirectAttributes attributes){
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.onOffBanner(study,true);
        attributes.addFlashAttribute("message","변경되었습니다");
        return "redirect:/study/"+getPath(path)+"/settings/banner";
    }

    @PostMapping("banner/disable")
    public String bannerDisable(@CurrentUser Account account , @PathVariable String path,RedirectAttributes attributes){
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.onOffBanner(study,false);
        attributes.addFlashAttribute("message","변경되었습니다");
        return "redirect:/study/"+getPath(path)+"/settings/banner";
    }

    @GetMapping("/zones")
    public String viewSettingsZones(@CurrentUser Account account ,Model model,@PathVariable String path) throws JsonProcessingException {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute("zones",study.getZones()
                .stream().map(Zone::toString).collect(Collectors.toList()));
        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist",objectMapper.writeValueAsString(allZones));
        return "study/zones";
    }

    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity addZones(@CurrentUser Account account,@PathVariable String path, @RequestBody ZoneForm zoneForm){
        Study study = studyService.getStudyZonesToUpdate(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(),
                zoneForm.getProvinceName());
        if(zone==null){
            return ResponseEntity.badRequest().build();
        }

        studyService.addZones(study,zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/zones/remove")
    @ResponseBody
    public ResponseEntity removeZones(@CurrentUser Account account , @PathVariable String path, @RequestBody ZoneForm zoneForm){
        Study study = studyService.getStudyZonesToUpdate(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(),
                zoneForm.getProvinceName());
        if(zone==null){
            return ResponseEntity.badRequest().build();
        }

        studyService.removeZones(study,zone);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tags")
    public String viewSettingsTags(@CurrentUser Account account, Model model , @PathVariable String path) throws JsonProcessingException {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute("tags",study.getTags()
                .stream().map(Tag::getTitle).collect(Collectors.toList()));
        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist",objectMapper.writeValueAsString(allTags));
        return "study/tags";
    }

    @ResponseBody
    @PostMapping("tags/add")
    public ResponseEntity addTags(@CurrentUser Account account , @RequestBody TagForm tagForm ,@PathVariable String path){
        Study study = studyService.getStudyTagsToUpdate(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if(tag==null){
            tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        }
        studyService.addTags(study,tag);
        return ResponseEntity.ok().build();
    }


    @ResponseBody
    @PostMapping("tags/remove")
    public ResponseEntity removeTags(@CurrentUser Account account , @RequestBody TagForm tagForm ,@PathVariable String path){
        Study study = studyService.getStudyTagsToUpdate(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if(tag==null){
            return ResponseEntity.badRequest().build();
        }
        studyService.removeTags(study,tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/study")
    public String studySettingForm(@CurrentUser Account account , @PathVariable String path, Model model){
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(study);
        model.addAttribute(account);
        return "study/study";
    }

    @PostMapping("/study/publish")
    public String publishStudy(@CurrentUser Account account,@PathVariable String path,
                               RedirectAttributes attributes){
        Study study = studyService.getStudyToUpdateStatus(account,path);
        studyService.publish(study);
        attributes.addFlashAttribute("message","스터디를 공개했습니다");
        return "redirect:/study/"+getPath(path)+"/settings/study";
    }

    @PostMapping("/study/close")
    public String closedStudy(@CurrentUser Account account, @PathVariable String path,
                              RedirectAttributes attributes){
        Study study = studyService.getStudyToUpdateStatus(account, path);
        studyService.closed(study);
        attributes.addFlashAttribute("message","스터디를 종료했습니다");
        return "redirect:/study/"+getPath(path)+"/settings/study";
    }

    @PostMapping("/recruit/start")
    public String startRecruit(@CurrentUser Account account, @PathVariable String path,
                               RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        if (!study.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다");
            return "redirect:/study/" + getPath(path) + "/settings/study";
        }
        studyService.startRecruit(study);
        attributes.addFlashAttribute("message", "인원모집을 시작 했습니다");
        return "redirect:/study/" + getPath(path) + "/settings/study";

    }
    @PostMapping("/recruit/stop")
    public String stopRecruit(@CurrentUser Account account, @PathVariable String path,
                               RedirectAttributes attributes){
        Study study = studyService.getStudyToUpdateStatus(account, path);
        if(!study.canUpdateRecruiting()){
            attributes.addFlashAttribute("message","1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다");
            return "redirect:/study/"+getPath(path)+"/settings/study";
        }
        studyService.stopRecruit(study);
        attributes.addFlashAttribute("message","인원모집을 종료 했습니다");
        return "redirect:/study/"+getPath(path)+"/settings/study";
    }

    @PostMapping("/study/path")
    public String editPath(@CurrentUser Account account, @PathVariable String path,
                           @RequestParam String newPath,RedirectAttributes attributes){
        Study study = studyService.getStudyToUpdate(account, path);

        if(!studyService.isPathValid(newPath)) {
            attributes.addFlashAttribute("studyPathError", "해당 스터디 경로는 사용할 수 없습니다");
            return "redirect:/study/"+getPath(path)+"/settings/study";
        }
        studyService.updateStudyPath(study,newPath);
        return "redirect:/study/"+getPath(newPath)+"/settings/study";

    }
}
