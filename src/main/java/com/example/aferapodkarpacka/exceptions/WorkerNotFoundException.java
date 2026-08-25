package com.example.aferapodkarpacka.exceptions;

public class WorkerNotFoundException extends RuntimeException{
    public WorkerNotFoundException(String message) {
        super(message);
    }
}
