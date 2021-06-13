package ru.kircoop.gk23.exception;

import java.util.function.Supplier;

/**
 * Техническая ошибка
 */
public class ServiceException extends Exception {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
