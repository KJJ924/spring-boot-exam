package me.jaejoon.demo.account;

import me.jaejoon.demo.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository repository;

    @MockBean
    JavaMailSender mailSender;

    @Test
    @DisplayName("이메일 체크 오류")
    void email_check_wrong() throws Exception {
        mockMvc.perform(get("/check-email-token")
                .param("token","asdadadad")
                .param("email","test@email.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated());
    }
    @Test
    @DisplayName("이메일 체크 성공")
    void email_check_complete() throws Exception {
        Account account = Account.builder()
                .email("test@email.com")
                .nickname("kjj")
                .password("asd")
                .build();
        repository.save(account);
        account.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                .param("token",account.getEmailCheckToken())
                .param("email",account.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickName"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated());

    }

    @Test
    @DisplayName("회원가입페이지")
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());
    }
    @Test
    @DisplayName("회원 가입 처리 - 입력값 오류")
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname","jaejoon")
                .param("password","12345")
                .param("email","email")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }
    @Test
    @DisplayName("회원 가입 처리 - 정상")
    void signUpSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname","testid123")
                .param("password","123456789")
                .param("email","email@email.com")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated());

        Account account = repository.findByEmail("email@email.com");
        assertThat(account.getEmailCheckToken()).isNotNull();
        assertThat(account).isNotNull().as(account.getPassword()).isNotEqualTo("123456789");

        then(mailSender).should().send(any(SimpleMailMessage.class));
    }
}