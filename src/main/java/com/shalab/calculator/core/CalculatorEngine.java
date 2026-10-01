package com.shalab.calculator.core;

/** Facade for safe mathematical expression evaluation. */
public final class CalculatorEngine {
    public double evaluate(String expression, AngleMode mode, double previousAnswer) { return new ExpressionParser(expression, mode, previousAnswer).parse(); }
}
