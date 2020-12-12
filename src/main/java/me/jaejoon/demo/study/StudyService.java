package me.jaejoon.demo.study;

import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.domain.Account;
import me.jaejoon.demo.domain.Study;
import me.jaejoon.demo.domain.Tag;
import me.jaejoon.demo.domain.Zone;
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
        checkExistingStudy(path, study);
        return study;
    }



    public Study getStudyToUpdate(Account account, String path) {
        Study study = getStudy(path);
        checkManager(account, study);
        return study;
    }


    public void updateStudyDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
        modelMapper.map(studyDescriptionForm,study);
    }

    public void onOffBanner(Study study, boolean result) {
        study.setUseBanner(result);
    }

    public void updateStudyImage(Study study, String image) {
        study.setImage(image);
    }

    public Study getStudyZonesToUpdate(Account account, String path) {
        Study study = studyRepository.findAccountWithZonesByPath(path);
        checkExistingStudy(path,study);
        checkManager(account,study);
        return study;
    }

    private void checkExistingStudy(String path, Study study) {
        if(study == null){
            throw new IllegalArgumentException(path +"에 해당하는 스터디가 없습니다");
        }
    }

    private void checkManager(Account account, Study study) {
        if(!account.isManagerOf(study)){
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다");
        }
    }

    public Study getStudyTagsToUpdate(Account account, String path) {
        Study study = studyRepository.findAccountWithTagsByPath(path);
        checkExistingStudy(path,study);
        checkManager(account,study);
        return study;
    }

    public void addZones(Study study, Zone zone) {
        study.getZones().add(zone);
    }

    public void removeZones(Study study, Zone zone) {
        study.getZones().remove(zone);
    }

    public void addTags(Study study, Tag tag) {
        study.getTags().add(tag);
    }

    public void removeTags(Study study, Tag tag) {
        study.getTags().remove(tag);
    }

    public Study getStudyToUpdateStatus(Account account, String path) {
        Study study = studyRepository.findAccountWithManagerByPath(path);
        checkExistingStudy(path,study);
        checkManager(account,study);
        return study;
    }

    public void publish(Study study) {
        study.publish();
    }

    public void closed(Study study) {
        study.closed();
    }

    public void startRecruit(Study study) {
        study.startRecruiting();
    }

    public void stopRecruit(Study study) {
        study.stopRecruiting();
    }

    public boolean isPathValid(String newPath) {
        if(!newPath.matches("^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$")){
            return false;
        }
        return !studyRepository.existsByPath(newPath);
    }

    public void updateStudyPath(Study study, String newPath) {
        study.setPath(newPath);
    }
}
