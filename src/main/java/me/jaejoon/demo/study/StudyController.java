package me.jaejoon.demo.study;

import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.account.CurrentUser;
import me.jaejoon.demo.domain.Account;
import me.jaejoon.demo.domain.Study;
import me.jaejoon.demo.study.form.StudyForm;
import me.jaejoon.demo.study.validation.StudyFormValidation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private final ModelMapper modelMapper;
    private final StudyService studyService;
    private final StudyFormValidation studyFormValidation;
    private final StudyRepository studyRepository;

    @InitBinder("studyForm")
    public void studyFormInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(studyFormValidation);
    }

    @GetMapping("/new-study")
    public String newStudyForm(@CurrentUser Account account , Model model){
        model.addAttribute(account);
        model.addAttribute(new StudyForm());
        return "study/form";
    }

    @PostMapping("/new-study")
    public String studyCreate(@CurrentUser Account account ,
                              @Valid StudyForm studyForm, Errors errors){
        if (errors.hasErrors()){
            return "redirect:/new-study";
        }
        Study study =studyService.createStudy(account,modelMapper.map(studyForm,Study.class));
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/study/{path}")
    public String viewStudy(@CurrentUser Account account , Model model, @PathVariable String path){
        Study study = studyRepository.findByPath(path);
        model.addAttribute(account);
        model.addAttribute(study);
        return "study/view";
    }
}
