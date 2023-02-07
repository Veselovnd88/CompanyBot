package ru.veselov.CompanyBot.config;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.veselov.CompanyBot.model.DivisionModel;
import ru.veselov.CompanyBot.service.CompanyInfoService;
import ru.veselov.CompanyBot.service.DivisionService;

@Configuration
public class Config {

    @Autowired
    CompanyInfoService companyInfoService;

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public EmailValidator emailValidator(){
        return new EmailValidator();
    }

    @Bean
    @Profile("test")
    public CommandLineRunner dataLoader(DivisionService divisionService){
        return args -> {
            divisionService.save(
            DivisionModel.builder().divisionId("LEUZE").name("Ультразвуковые, оптические датчики LEUZE").build());
            divisionService.save(
                    DivisionModel.builder().divisionId("PRESSURE").name("Датчики давления, расхода, температуры").build());
            divisionService.save(
                    DivisionModel.builder().divisionId("LPKF").name("Станки для печатных плат LPKF").build());
            divisionService.save(
                    DivisionModel.builder().divisionId("COMMON").name("Общие вопросы").build());

        };
    }

    @EventListener({ContextRefreshedEvent.class})
    public void fillFieldsInformation(){
        companyInfoService.getLast();
    }

    @Bean
    BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

}
