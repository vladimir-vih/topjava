package ru.javawebinar.topjava.exception;

public class WrongRequestParameters extends RuntimeException{
    public WrongRequestParameters(String message){
        super(message);
    }
}
