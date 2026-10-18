package com.tertiary.verify.mail;

import com.tertiary.verify.data_model.model.repositories.lookup.AppConfigRepository;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class EmailConfig {

  @Autowired private AppConfigRepository repository;

  @Bean
  public JavaMailSender getMailSender() throws Exception {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    mailSender.setHost("smtp.gmail.com");
    mailSender.setPort(587);
    mailSender.setUsername(repository.findByCodeAndDeleted("MAIL_USER", false).get(0).getValue());
    mailSender.setPassword(repository.findByCodeAndDeleted("MAIL_PASS", false).get(0).getValue());
    Properties javaMailProperties = new Properties();
    javaMailProperties.put("mail.smtp.starttls.enable", "true");
    javaMailProperties.put("mail.smtp.auth", "true");
    javaMailProperties.put("mail.transport.protocol", "smtp");
    javaMailProperties.put("mail.debug", "true");

    mailSender.setJavaMailProperties(javaMailProperties);
    return mailSender;
  }

  /** HTML Email config */
  @Bean
  public ITemplateResolver templateResolver() {
    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setPrefix("emailTemplate/");
    templateResolver.setSuffix(".html");
    templateResolver.setTemplateMode(TemplateMode.HTML);
    return templateResolver;
  }
}
