package it.bookmarker.service.exception.UtenteServiceException;

public class PasswordNonCorrispondentiException extends Exception {
    public PasswordNonCorrispondentiException(String message) {
        super(message);
    }
}