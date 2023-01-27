package ru.veselov.CompanyBot.exception;

public class NoDivisionKeyboardException  extends Exception{
    public NoDivisionKeyboardException(){
        super("No keyboard created, let's add Manager one more time");
    }
}
