package com.api.MoviePedia.exception;


public class DuplicateDatabaseEntryException extends RuntimeException{
    public DuplicateDatabaseEntryException(String message){
        super(message);
    }
}
