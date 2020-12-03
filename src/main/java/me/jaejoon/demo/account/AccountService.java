package me.jaejoon.demo.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jaejoon.demo.config.AppProperties;
import me.jaejoon.demo.domain.Account;
import me.jaejoon.demo.domain.Tag;
import me.jaejoon.demo.domain.Zone;
import me.jaejoon.demo.form.*;
import me.jaejoon.demo.mail.EmailMessage;
import me.jaejoon.demo.mail.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final AppProperties properties;
    private final TemplateEngine templateEngine;

    public void sendSignUpConfirmEmail(Account newAccount) {
        newAccount.generateEmailCheckToken();
        Context context = new Context();
        context.setVariable("nickname",newAccount.getNickname());
        context.setVariable("message","이메일을 인증하려면 아래를 클릭하세요");
        context.setVariable("host",properties.getHost());
        context.setVariable("link","/check-email-token?token=" + newAccount.getEmailCheckToken()
                +"&email="+ newAccount.getEmail());
        context.setVariable("linkName","이메일 인증");

        String template = templateEngine.process("mail/send-mail", context);
        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("회원가입 인증 메일")
                .message(template)
                .build();
        emailService.sendEmail(emailMessage);
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
        Context context = new Context();
        context.setVariable("nickname",account.getNickname());
        context.setVariable("message","이메일 로그인을 이용하기 위해 아래를 클릭하세요");
        context.setVariable("host",properties.getHost());
        context.setVariable("link","/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        context.setVariable("linkName","로그인");

        String template = templateEngine.process("mail/send-mail", context);
        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("이메일 로그인 인증 메일")
                .message(template)
                .build();
        emailService.sendEmail(emailMessage);

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

    public Set<Zone> getZoneTag(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZones();
    }

    public void addZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a->a.getZones().add(zone));
    }

    public void removeZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a->a.getZones().remove(zone));
    }
}
