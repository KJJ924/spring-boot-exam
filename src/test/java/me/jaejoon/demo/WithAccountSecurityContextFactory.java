package me.jaejoon.demo;

import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.account.AccountService;
import me.jaejoon.demo.account.SignUpForm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final AccountService service;
    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {
        String nickName = withAccount.value();

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname(nickName);
        signUpForm.setPassword("123456789");
        signUpForm.setEmail(nickName+"@email.com");
        service.processNewAccount(signUpForm);

        UserDetails userDetails = service.loadUserByUsername(nickName);
        UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        return context;
    }
}
