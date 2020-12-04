package me.jaejoon.demo.study.validation;

import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.study.StudyRepository;
import me.jaejoon.demo.study.form.StudyForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class StudyFormValidation implements Validator {
    private final StudyRepository studyRepository;
    @Override
    public boolean supports(Class<?> clazz) {
        return StudyForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StudyForm studyForm = (StudyForm) target;

        if(studyRepository.existsByPath(studyForm.getPath())){
            errors.rejectValue("path","wrong.path","해당 URL 은 사용하실수 없습니다.");
        }
    }
}
