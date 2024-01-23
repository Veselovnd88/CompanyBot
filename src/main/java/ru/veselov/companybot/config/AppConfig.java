package ru.veselov.companybot.config;

import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.veselov.companybot.cache.UserStateCache;
import ru.veselov.companybot.service.impl.CompanyInfoServiceImpl;
import ru.veselov.companybot.util.MessageUtils;

@Configuration
@RequiredArgsConstructor
@EnableAspectJAutoProxy
public class AppConfig {

    private final CompanyInfoServiceImpl companyInfoService;

    private final UserStateCache userStateCache;

    @Bean
    public EmailValidator emailValidator() {
        return new EmailValidator();
    }

    @EventListener({ContextRefreshedEvent.class})
    public void reset() {
        Message last = companyInfoService.getLast();
        MessageUtils.setAbout(last);
        userStateCache.reset();
    }

}
