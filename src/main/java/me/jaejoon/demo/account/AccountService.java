package me.jaejoon.demo.account;

import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.domain.Account;
import me.jaejoon.demo.domain.Tag;
import me.jaejoon.demo.form.*;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public void sendSignUpConfirmEmail(Account newAccount) {
        newAccount.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("회원가입 인증메일");
        mailMessage.setText("/check-email-token?token="+ newAccount.getEmailCheckToken()
                +"&email="+ newAccount.getEmail());
        mailSender.send(mailMessage);
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);
        return accountRepository.save(account);
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
        Account account = accountRepository.findByNickname(nickNameOrEmail);
        if (account == null){
            account = accountRepository.findByEmail(nickNameOrEmail);
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
        accountRepository.save(account);
    }

    public void updatePassword(Account account, PasswordForm form) {
        account.setPassword(passwordEncoder.encode(form.getNewPasswordConfirm()));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, Notifications notifications) {
        modelMapper.map(notifications,account);
        accountRepository.save(account);
    }

    public void updateNickName(Account account, NicknameForm nicknameForm) {
        modelMapper.map(nicknameForm,account);
        accountRepository.save(account);
        login(account);
    }

    public void sendLoginLink(Account account) {
        account.generateEmailCheckToken();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(account.getEmail());
        message.setSubject("로그인 메일");
        message.setText("/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        mailSender.send(message);
    }

    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a ->a.getTags().add(tag));
    }

    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getTags();
    }

    public void removeTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a-> a.getTags().remove(tag));
    }
}
