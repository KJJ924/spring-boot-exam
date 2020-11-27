package me.jaejoon.demo.account;

import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.domain.Account;
import me.jaejoon.demo.settings.Notifications;
import me.jaejoon.demo.settings.PasswordForm;
import me.jaejoon.demo.settings.Profile;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {
    private final AccountRepository repository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("회원가입 인증메일");
        mailMessage.setText("/check-email-token?token="+ newAccount.getEmailCheckToken()
                +"&email="+ newAccount.getEmail());
        mailSender.send(mailMessage);
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account = Account.builder()
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .email(signUpForm.getEmail())
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdateByWeb(true)
                .joinedAt(LocalDateTime.now())
                .build();
        return repository.save(account);
    }

    public Account processNewAccount(SignUpForm signUpForm) {
        Account account = saveNewAccount(signUpForm);
        account.generateEmailCheckToken();
        sendSignUpConfirmEmail(account);
        return account;
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
               new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String nickNameOrEmail) throws UsernameNotFoundException {
        Account account = repository.findByNickname(nickNameOrEmail);
        if (account == null){
            account = repository.findByEmail(nickNameOrEmail);
        }
        if (account == null){
            throw  new UsernameNotFoundException(nickNameOrEmail);
        }
        return new UserAccount(account);
    }

    public void completeCheck(Account account) {
        account.completeCheck();
        login(account);
    }

    public void updateProfile(Account account, Profile profile) {
        modelMapper.map(profile,account);
        repository.save(account);
    }

    public void updatePassword(Account account, PasswordForm form) {
        account.setPassword(passwordEncoder.encode(form.getNewPasswordConfirm()));
        repository.save(account);
    }

    public void updateNotifications(Account account, Notifications notifications) {
        modelMapper.map(notifications,account);
        repository.save(account);
    }
}
