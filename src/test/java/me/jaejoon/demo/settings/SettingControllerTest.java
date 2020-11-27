package me.jaejoon.demo.settings;

import me.jaejoon.demo.WithAccount;
import me.jaejoon.demo.account.AccountRepository;
import me.jaejoon.demo.account.AccountService;
import me.jaejoon.demo.domain.Account;
import me.jaejoon.demo.form.SignUpForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    PasswordEncoder passwordEncoder;

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

    @Test
    @WithAccount("jaejoon") // 기본 비밀번호 12345689
    @DisplayName("패스워드 수정 페이지 이동")
    void updatePasswordForm() throws Exception {
        mockMvc.perform(get(SettingController.SETTINGS_PASSWORD_URL))
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingController.SETTINGS_PASSWORD_VIEW_NAME));

    }

    @Test
    @WithAccount("jaejoon")
    @DisplayName("패스워드 변경 성공")
    void updatePassword_success() throws Exception {
        mockMvc.perform(post(SettingController.SETTINGS_PASSWORD_URL)
                .param("newPassword","1234567890")
                .param("newPasswordConfirm","1234567890")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingController.SETTINGS_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account jaejoon = repository.findByNickname("jaejoon");
        boolean matches = passwordEncoder.matches("1234567890", jaejoon.getPassword());
        assertThat(matches).isTrue();
    }

    @Test
    @WithAccount("jaejoon")
    @DisplayName("패스워드 변경 실패")
    void updatePassword_wrong() throws Exception {
        mockMvc.perform(post(SettingController.SETTINGS_PASSWORD_URL)
                .param("newPassword","12323231490")
                .param("newPasswordConfirm","1234567890")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithAccount("jaejoon")
    @DisplayName("닉네임 변경 수정 페이지 이동")
    void updateNickNameForm() throws Exception {
        mockMvc.perform(get(SettingController.SETTINGS_ACCOUNT_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingController.SETTINGS_ACCOUNT_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }
    @Test
    @WithAccount("jaejoon")
    @DisplayName("닉네임 변경_중복실패")
    void updateNickName_fail() throws Exception {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("중복닉네임");
        signUpForm.setEmail("test@gmail.com");
        signUpForm.setPassword("123123131313");
        service.processNewAccount(signUpForm);

        mockMvc.perform(post(SettingController.SETTINGS_ACCOUNT_URL)
                .param("nickname","중복닉네임")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingController.SETTINGS_ACCOUNT_VIEW_NAME))
                .andExpect(model().hasErrors());

        boolean beforeName = repository.existsByNickname("jaejoon");
        assertThat(beforeName).isTrue();
    }

    @Test
    @WithAccount("jaejoon")
    @DisplayName("닉네임 변경_성공")
    void updateNickName() throws Exception {
        mockMvc.perform(post(SettingController.SETTINGS_ACCOUNT_URL)
                .param("nickname","변경닉네임")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingController.SETTINGS_ACCOUNT_URL))
                .andExpect(flash().attributeExists("message"));
        boolean beforeName = repository.existsByNickname("jaejoon");
        boolean afterName = repository.existsByNickname("변경닉네임");

        assertThat(beforeName).isFalse();
        assertThat(afterName).isTrue();
    }

}