package ru.veselov.companybot.config;

import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.service.impl.CompanyInfoServiceImpl;
import ru.veselov.companybot.service.impl.DivisionServiceImpl;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@EnableAspectJAutoProxy
public class Config {

    private final CompanyInfoServiceImpl companyInfoService;

    @Bean
    public EmailValidator emailValidator() {
        return new EmailValidator();
    }

    @EventListener({ContextRefreshedEvent.class})
    public void fillFieldsInformation() {
        companyInfoService.getLast();
    }

}
