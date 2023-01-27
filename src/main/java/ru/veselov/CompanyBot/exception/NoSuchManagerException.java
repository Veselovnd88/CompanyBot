package ru.veselov.CompanyBot.exception;

public class NoSuchManagerException extends Exception{

    public NoSuchManagerException(){
        super("No manager with such Id in database");
    }
}
