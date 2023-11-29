package ru.veselov.companybot.exception;

public class NoDivisionKeyboardException  extends Exception{
    public NoDivisionKeyboardException(){
        super("No keyboard created, let's add Manager one more time");
    }
}
