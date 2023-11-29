package ru.veselov.companybot.exception;

public class WrongContactException extends NoAvailableActionException{
    public WrongContactException(String message, String chatId){
        super(message, chatId);
    }
}
