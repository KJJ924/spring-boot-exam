package me.jaejoon.demo.domain;

import me.jaejoon.demo.account.UserAccount;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class StudyTest {
    Study study;
    Account account;
    UserAccount userAccount;

    @BeforeEach
    void beforeEach(){
        study = new Study();
        account = new Account();
        account.setNickname("jaejoon");
        account.setPassword("123");
        userAccount = new UserAccount(account);
    }

    @DisplayName("스터디 공개, 모집중 and 이미 맴버나 관리자가 아니면 가입가능")
    @Test
    void isJoinEnable(){
        study.setRecruiting(true);
        study.setPublished(true);
        Assertions.assertThat(study.isJoinable(userAccount)).isTrue();
    }

    @DisplayName("스터디 공개, 모집중 and 맴버가 관리자가 면 가입 불가능")
    @Test
    void isJoin_false_for_manager(){
        study.setRecruiting(true);
        study.setPublished(true);
        study.getManagers().add(account);
        Assertions.assertThat(study.isJoinable(userAccount)).isFalse();
    }

    @DisplayName("스터디 공개, 모집중 and 멤버가 이미 멤버 면 가입 불가능")
    @Test
    void isJoin_false_for_member(){
        study.setRecruiting(true);
        study.setPublished(true);
        study.getMembers().add(account);
        Assertions.assertThat(study.isJoinable(userAccount)).isFalse();
    }

    @DisplayName("스터디 맴버인지 확인")
    @Test
    void check_member(){
        study.addMember(account);
        Assertions.assertThat(study.isMember(userAccount)).isTrue();
    }


    @DisplayName("스터디 관리자인지 확인")
    @Test
    void check_manager(){
        study.addManger(account);
        Assertions.assertThat(study.isManager(userAccount)).isTrue();
    }

}