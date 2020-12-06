package me.jaejoon.demo.study;

import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.account.CurrentUser;
import me.jaejoon.demo.domain.Account;
import me.jaejoon.demo.domain.Study;
import me.jaejoon.demo.study.form.StudyDescriptionForm;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Controller
@RequestMapping("/study/{path}/settings")
public class StudySettingController {

    private final ModelMapper modelMapper;
    private final StudyService studyService;

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
}
