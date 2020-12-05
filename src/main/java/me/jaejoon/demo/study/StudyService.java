package me.jaejoon.demo.study;

import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.domain.Account;
import me.jaejoon.demo.domain.Study;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;

    public Study createStudy(Account account, Study study) {
        Study newStudy = studyRepository.save(study);
        newStudy.addManger(account);
        return newStudy;
    }
}
