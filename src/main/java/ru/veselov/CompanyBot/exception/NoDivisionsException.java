package ru.veselov.CompanyBot.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoDivisionsException extends Exception{
    public NoDivisionsException(){
        super("Нет отделов в базе данных");
        log.info("Exception {}: {}",this.getClass(),this.getMessage());
    }


}
