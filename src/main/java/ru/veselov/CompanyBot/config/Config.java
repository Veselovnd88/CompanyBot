package ru.veselov.CompanyBot.config;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.service.DivisionService;

@Configuration
public class Config {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public EmailValidator emailValidator(){
        return new EmailValidator();
    }

    @Bean//FIXME потом перенесется в тест профиль
    public CommandLineRunner dataLoader(DivisionService divisionService){
        return args -> {
            divisionService.save(
            Division.builder().divisionId("LEUZE").name("Ультразвуковые, оптические датчики LEUZE").build());
            divisionService.save(
                    Division.builder().divisionId("PRESSURE").name("Датчики давления, расхода, температуры").build());
            divisionService.save(
                    Division.builder().divisionId("LPKF").name("Станки для печатных плат LPKF").build());
            divisionService.save(
                    Division.builder().divisionId("COMMON").name("Общие вопросы").build());

        };
    }
}
