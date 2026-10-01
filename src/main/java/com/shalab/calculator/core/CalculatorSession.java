package com.shalab.calculator.core;

import java.util.Objects;

/** Stateful calculator interaction model shared by the UI and tests. */
public final class CalculatorSession {
    private final CalculatorEngine engine;
    private CalculatorMode mode = CalculatorMode.NORMAL;
    private AngleMode angleMode = AngleMode.DEG;
    private String expression = "";
    private double answer;

    public CalculatorSession() { this(new CalculatorEngine()); }
    public CalculatorSession(CalculatorEngine engine) { this.engine = Objects.requireNonNull(engine); }

    public CalculatorMode mode() { return mode; }
    public AngleMode angleMode() { return angleMode; }
    public String expression() { return expression; }
    public double answer() { return answer; }
    public void setMode(CalculatorMode mode) { this.mode = Objects.requireNonNull(mode); }
    public void setAngleMode(AngleMode angleMode) { this.angleMode = Objects.requireNonNull(angleMode); }
    public void setExpression(String expression) { this.expression = Objects.requireNonNull(expression); }

    /** Evaluates the current expression, updates ANS only after a successful calculation, and returns the result. */
    public double calculate() {
        double value = engine.evaluate(expression, angleMode, answer);
        answer = value;
        return value;
    }

    /** Clears only the editable expression; ANS remains available. */
    public void clearExpression() { expression = ""; }
}
