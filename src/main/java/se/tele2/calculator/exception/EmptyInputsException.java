package se.tele2.calculator.exception;

public class EmptyInputsException extends RuntimeException {
    public EmptyInputsException(String message) {
        super(message);
    }
}
