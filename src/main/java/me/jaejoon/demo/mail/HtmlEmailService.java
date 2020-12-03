package me.jaejoon.demo.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Profile("dev")
@Component
@RequiredArgsConstructor
@Slf4j
public class HtmlEmailService implements EmailService{

    private final JavaMailSender mailSender;
    @Override
    public void sendEmail(EmailMessage message){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,false,"UTF-8");
            messageHelper.setTo(message.getTo());
            messageHelper.setSubject(message.getSubject());
            messageHelper.setText(message.getMessage(),true);
            mailSender.send(mimeMessage);
            log.info("sent email:{}",message.getSubject());
        } catch (MessagingException e) {
            log.error("failed to send email",e);
        }

    }
}
