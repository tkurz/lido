package com.github.tkurz.lido;

/**
 * ...
 * <p/>
 * Author: Thomas Kurz (tkurz@apache.org)
 */
public class EvaluationException extends Exception {
    public EvaluationException() {
        super();
    }

    public EvaluationException(String message) {
        super(message);
    }

    public EvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}
