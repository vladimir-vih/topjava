package ru.javawebinar.topjava.util.exception;

public class UnAuthorizedAccessException extends RuntimeException {
    public UnAuthorizedAccessException(String msg) {
        super(msg);
    }
}
