package me.jaejoon.demo;

import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.account.AccountService;
import me.jaejoon.demo.domain.Account;
import me.jaejoon.demo.domain.Study;
import me.jaejoon.demo.form.SignUpForm;
import me.jaejoon.demo.study.StudyService;
import me.jaejoon.demo.study.form.StudyForm;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor
public class WithAccountAndStudyPageSecurityContextFactory implements WithSecurityContextFactory<WithAccountAndStudyPage> {

    private final AccountService service;
    private final StudyService studyService;
    private final ModelMapper modelMapper;
    @Override
    public SecurityContext createSecurityContext(WithAccountAndStudyPage withAccount) {
        String nickName = withAccount.value();

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname(nickName);
        signUpForm.setPassword("123456789");
        signUpForm.setEmail(nickName+"@email.com");
        Account account = service.processNewAccount(signUpForm);
        studyService.createStudy(account,createTestStudy(withAccount.title(),withAccount.path()));

        UserDetails userDetails = service.loadUserByUsername(nickName);
        UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        return context;
    }

    private Study createTestStudy(String title, String path){
        StudyForm studyForm = new StudyForm();
        studyForm.setTitle(title);
        studyForm.setPath(path);
        studyForm.setFullDescription("test FullDescription");
        studyForm.setShortDescription("Test shotDescription");
        return modelMapper.map(studyForm,Study.class);
    }
}
