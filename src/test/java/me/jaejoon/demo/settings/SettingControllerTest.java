package me.jaejoon.demo.settings;

import me.jaejoon.demo.WithAccount;
import me.jaejoon.demo.account.AccountRepository;
import me.jaejoon.demo.account.AccountService;
import me.jaejoon.demo.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
class SettingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService service;

    @Autowired
    AccountRepository repository;

    @AfterEach
    void afterEach(){
        repository.deleteAll();
    }
    @Test
    @WithAccount("jaejoon")
    @DisplayName("프로필수정_페이지_이동")
    void updateForm() throws Exception {
        mockMvc.perform(get(SettingController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingController.SETTINGS_PROFILE_URL))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));

    }
    @Test
    @WithAccount("jaejoon")
    @DisplayName("프로필수정_성공")
    void updateProfile() throws Exception {
        String bio = "안녕하세요 안녕하세요 ";
        mockMvc.perform(post(SettingController.SETTINGS_PROFILE_URL)
                    .param("bio", bio)
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account account = repository.findByNickname("jaejoon");
        assertThat(account.getBio()).isEqualTo(bio);
    }


    @Test
    @WithAccount("jaejoon")
    @DisplayName("프로필수정_실패")
    void updateProfile_error() throws Exception {
        String bio = "안녕하세요 안녕하세요 안녕하세요 안녕하세요안녕하세요 안녕하세요안녕하세요 안녕하세요안녕하세요 안녕하세요";
        mockMvc.perform(post(SettingController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());


        Account account = repository.findByNickname("jaejoon");
        assertThat(account.getBio()).isNull();
    }
}