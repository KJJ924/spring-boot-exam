package me.jaejoon.demo.study;

import me.jaejoon.demo.WithAccountAndStudyPage;
import me.jaejoon.demo.account.CurrentUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
class StudySettingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("스터디 설정 page 보기")
    @WithAccountAndStudyPage(value ="kjj924",title = "봄싹스터디",path = "test")
    void viewStudySetting() throws Exception {
        String path = URLEncoder.encode("test", StandardCharsets.UTF_8);
        mockMvc.perform(get("/study/"+ path +"/settings/description"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("studyDescriptionForm"));
    }

}