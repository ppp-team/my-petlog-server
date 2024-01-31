package com.ppp.api.test;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.ppp.common", "com.ppp.domain"})
public class TestConfig {
}
