package me.jaejoon.demo.study;

import me.jaejoon.demo.WithAccount;
import me.jaejoon.demo.WithAccountAndStudyPage;
import me.jaejoon.demo.account.AccountRepository;
import me.jaejoon.demo.domain.Account;
import me.jaejoon.demo.domain.Study;
import me.jaejoon.demo.study.form.StudyForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StudyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyService studyService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ModelMapper modelMapper;
    @Test
    @DisplayName("스터디 생성 폼")
    @WithAccount("kjj924")
    void newStudyForm() throws Exception {
        mockMvc.perform(get("/new-study"))
                .andExpect(view().name("study/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("스터디 생성 - 성공")
    @WithAccount("kjj924")
    void createStudy_success() throws Exception{
        mockMvc.perform(post("/new-study")
                .with(csrf())
                .param("path","테스트")
                .param("title","testTitle")
                .param("fullDescription","full Description")
                .param("shortDescription","short Description"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/"+ URLEncoder.encode("테스트", StandardCharsets.UTF_8)));

        Study study = studyRepository.findByPath("테스트");

        assertThat(study).isNotNull();
        assertThat(study.getPath()).isEqualTo("테스트");
    }

    @Test
    @DisplayName("스터디 생성 - 실패")
    @WithAccount("kjj924")
    void createStudy_wrong() throws Exception{
        mockMvc.perform(post("/new-study")
                .with(csrf())
                .param("path","테 스 트")
                .param("title","testTitle")
                .param("fullDescription","full Description")
                .param("shortDescription","short Description"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"));

        Study study = studyRepository.findByPath("테스트");

        assertThat(study).isNull();
    }

    @Test
    @DisplayName("스터디 조회")
    @WithAccount("kjj924")
    void viewStudy()throws Exception{
        Account account = accountRepository.findByNickname("kjj924");
        StudyForm studyForm = new StudyForm();
        studyForm.setShortDescription("shotDescription");
        studyForm.setFullDescription("fullDescription");
        studyForm.setPath("테스트");
        studyForm.setTitle("test title");

        studyService.createStudy(account,modelMapper.map(studyForm,Study.class));

        mockMvc.perform(get("/study/테스트"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @DisplayName("스터디 맴버 조회")
    @WithAccount("kjj924")
    void viewMembersStudy()throws Exception{
        Account account = accountRepository.findByNickname("kjj924");
        StudyForm studyForm = new StudyForm();
        studyForm.setShortDescription("shotDescription");
        studyForm.setFullDescription("fullDescription");
        studyForm.setPath("테스트");
        studyForm.setTitle("test title");

        studyService.createStudy(account,modelMapper.map(studyForm,Study.class));

        mockMvc.perform(get("/study/테스트/members"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @DisplayName("스터디 맴버 join")
    @WithAccountAndStudyPage(value = "kjj924",path = "test", title = "testTitle")
    void memberJoin() throws Exception {
        String path = URLEncoder.encode("test", StandardCharsets.UTF_8);
        mockMvc.perform(get("/study/"+path+"/join"))
                .andExpect(status().is3xxRedirection());

        Account joinMember = accountRepository.findByNickname("kjj924");
        Study study = studyRepository.findByPath(path);

        assertThat(study.getMembers().contains(joinMember)).isTrue();
    }

    @Test
    @DisplayName("스터디 맴버 탈퇴")
    @WithAccountAndStudyPage(value = "kjj924",path = "test", title = "testTitle")
    void leaveJoin() throws Exception {
        String path = URLEncoder.encode("test", StandardCharsets.UTF_8);
        Account joinMember = accountRepository.findByNickname("kjj924");
        Study study = studyRepository.findByPath(path);
        study.getMembers().add(joinMember);

        mockMvc.perform(get("/study/"+path+"/leave"))
                .andExpect(status().is3xxRedirection());

        assertThat(study.getMembers().contains(joinMember)).isFalse();
    }

}