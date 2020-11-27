package me.jaejoon.demo.settings;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.jaejoon.demo.domain.Account;

@Data
@NoArgsConstructor
public class Notifications {

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;

    public Notifications(Account account) {
        this.studyCreatedByEmail = account.isStudyCreatedByEmail();
        this.studyCreatedByWeb = account.isStudyCreatedByWeb();
        this.studyEnrollmentResultByEmail = account.isStudyEnrollmentResultByEmail();
        this.studyEnrollmentResultByWeb = account.isStudyUpdateByWeb();
        this.studyUpdatedByEmail = account.isStudyUpdateByEmail();
        this.studyUpdatedByWeb = account.isStudyUpdateByWeb();

    }
}