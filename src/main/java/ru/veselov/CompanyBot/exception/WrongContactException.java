package ru.veselov.CompanyBot.exception;

import lombok.Getter;

public class WrongContactException extends NoAvailableActionException{
    public WrongContactException(String message, String chatId){
        super(message, chatId);
    }
}
