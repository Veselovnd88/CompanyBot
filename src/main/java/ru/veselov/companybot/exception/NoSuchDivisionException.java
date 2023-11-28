package ru.veselov.companybot.exception;

public class NoSuchDivisionException extends Exception{

    public NoSuchDivisionException(){
        super("No division found with such id");
    }
}
