package me.jaejoon.demo.study;

import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.domain.Account;
import me.jaejoon.demo.domain.Study;
import me.jaejoon.demo.study.form.StudyDescriptionForm;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;

    public Study createStudy(Account account, Study study) {
        Study newStudy = studyRepository.save(study);
        newStudy.addManger(account);
        return newStudy;
    }

    public Study getStudy(String path) {
        Study study = studyRepository.findByPath(path);
        if(study== null){
            throw new IllegalArgumentException(path+"에 해당하는 스터디가 없습니다");
        }
        return study;
    }

    public Study getStudyToUpdate(Account account, String path) {
        Study study = getStudy(path);
        if(!account.isManagerOf(study)){
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다");
        }
        return study;
    }

    public void updateStudyDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
        modelMapper.map(studyDescriptionForm,study);
    }
}
