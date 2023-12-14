package ru.veselov.companybot.config;

import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import ru.veselov.companybot.cache.UserStateCache;
import ru.veselov.companybot.service.impl.CompanyInfoServiceImpl;

@Configuration
@RequiredArgsConstructor
@EnableAspectJAutoProxy
public class Config {

    private final CompanyInfoServiceImpl companyInfoService;

    private final UserStateCache userStateCache;

    @Bean
    public EmailValidator emailValidator() {
        return new EmailValidator();
    }

    @EventListener({ContextRefreshedEvent.class})
    public void reset() {
        companyInfoService.getLast();
        userStateCache.reset();
    }

}
