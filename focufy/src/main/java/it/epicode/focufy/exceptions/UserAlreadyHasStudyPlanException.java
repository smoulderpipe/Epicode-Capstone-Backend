package it.epicode.focufy.exceptions;

public class UserAlreadyHasStudyPlanException extends RuntimeException{
    public UserAlreadyHasStudyPlanException(String message){
        super(message);
    }
}
