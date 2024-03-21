package com.ppp.api.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfigForMail {

    @Bean(name = "testJavaMailSender")
    public JavaMailSender mailSender() {
       return mock(JavaMailSender.class);
    }
}