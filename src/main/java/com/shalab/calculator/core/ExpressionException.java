package com.shalab.calculator.core;

/** Friendly, user-facing expression evaluation error. */
public class ExpressionException extends RuntimeException {
    public ExpressionException(String message) { super(message); }
}
