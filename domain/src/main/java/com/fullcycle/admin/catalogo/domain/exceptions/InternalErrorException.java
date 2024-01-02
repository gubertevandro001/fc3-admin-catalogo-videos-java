package com.fullcycle.admin.catalogo.domain.exceptions;

public class InternalErrorException extends NoStackTraceException{

    protected InternalErrorException(String message, Throwable t) {
        super(message, t);
    }

    public static InternalErrorException with(final String message, final Throwable t) {
        return new InternalErrorException(message, t);
    }

}
