package ru.veselov.CompanyBot.config;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
