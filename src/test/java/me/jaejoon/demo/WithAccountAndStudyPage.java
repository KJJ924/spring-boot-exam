package me.jaejoon.demo;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithAccountAndStudyPageSecurityContextFactory.class)
public @interface WithAccountAndStudyPage {
    String value();
    String path();
    String title();

}
