package me.jaejoon.demo.Main;

import me.jaejoon.demo.account.AccountRepository;
import me.jaejoon.demo.account.AccountService;
import me.jaejoon.demo.account.SignUpForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository repository;
    @Autowired
    AccountService service;
    @BeforeEach
    void before(){
        SignUpForm signUpForm  = new SignUpForm();
        signUpForm.setEmail("test@email.com");
        signUpForm.setNickname("jaejoon");
        signUpForm.setPassword("12345678");
        service.processNewAccount(signUpForm);
    }

    @AfterEach
    void after(){
        repository.deleteAll();
    }
    @Test
    @DisplayName("로그인_이메일_성공")
    void email_login_success() throws Exception {
        mockMvc.perform(post("/login")
                .param("username","test@email.com")
                .param("password","12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("jaejoon"));
    }

    @Test
    @DisplayName("로그인_이메일_성공")
    void nickname_login_success() throws Exception {
        mockMvc.perform(post("/login")
                .param("username","jaejoon")
                .param("password","12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("jaejoon"));
    }


    @Test
    @DisplayName("로그 아웃")
    void logout() throws Exception {
        mockMvc.perform(post("/logout")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("로그인 실패")
    void login_fail() throws Exception{
        mockMvc.perform(post("/login")
                .param("username","failID")
                .param("password","123131313")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());

    }
}