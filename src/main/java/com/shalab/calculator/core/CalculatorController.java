package com.shalab.calculator.core;

import com.shalab.calculator.util.NumberFormatter;

import java.util.Objects;

/** Single source of truth for calculator input, answer, mode, and evaluation feedback. */
public final class CalculatorController {
    private final CalculatorEngine engine;
    private String currentExpression = "";
    private String resultText = "";
    private String errorMessage = "";
    private double lastResult;
    private boolean scientificMode;
    private boolean evaluated;
    private AngleMode angleMode = AngleMode.DEG;

    public CalculatorController() { this(new CalculatorEngine()); }
    public CalculatorController(CalculatorEngine engine) { this.engine = Objects.requireNonNull(engine); }

    /** Handles one keypad or keyboard token. Returns true when a calculation succeeded. */
    public boolean handleInput(String token) {
        Objects.requireNonNull(token);
        switch (token) {
            case "=" -> { return calculate(); }
            case "AC", "CLEAR" -> { clear(); return false; }
            case "DEL" -> { delete(); return false; }
            case "±" -> { toggleSign(); return false; }
            default -> { append(token); return false; }
        }
    }

    private void append(String token) {
        String value = switch (token) {
            case "×" -> "*"; case "÷" -> "/"; case "−" -> "-";
            case "π" -> "pi"; case "ANS" -> "ANS";
            case "x²" -> "^2"; case "xʸ" -> "^";
            case "sin", "cos", "tan", "asin", "acos", "atan", "log", "ln", "sqrt", "cbrt", "abs", "exp", "floor", "ceil", "round" -> token + "(";
            default -> token;
        };
        boolean continues = isOperator(value) || value.equals("!") || value.equals("%") || value.equals(")") || value.equals("^2");
        if (evaluated && !continues) currentExpression = "";
        evaluated = false;
        resultText = "";
        errorMessage = "";
        currentExpression += value;
    }

    private boolean calculate() {
        if (currentExpression.isBlank()) {
            errorMessage = "Enter an expression.";
            resultText = "Error";
            evaluated = false;
            return false;
        }
        try {
            currentExpression = closeOpenGroups(currentExpression);
            lastResult = engine.evaluate(currentExpression, angleMode, lastResult);
            resultText = NumberFormatter.format(lastResult);
            errorMessage = "";
            evaluated = true;
            return true;
        } catch (ExpressionException ex) {
            resultText = "Error";
            errorMessage = ex.getMessage();
            evaluated = false;
            return false;
        }
    }

    private static String closeOpenGroups(String expression) {
        int depth = 0;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') depth--;
            if (depth < 0) return expression;
        }
        return depth > 0 ? expression + ")".repeat(depth) : expression;
    }

    private void clear() { currentExpression = ""; resultText = ""; errorMessage = ""; evaluated = false; }

    private void delete() {
        if (currentExpression.isEmpty()) return;
        currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
        resultText = ""; errorMessage = ""; evaluated = false;
    }

    private void toggleSign() {
        resultText = ""; errorMessage = ""; evaluated = false;
        if (currentExpression.isEmpty()) { currentExpression = "-"; return; }
        int end = currentExpression.length();
        int start = end;
        while (start > 0 && (Character.isDigit(currentExpression.charAt(start - 1)) || currentExpression.charAt(start - 1) == '.')) start--;
        if (start == end) return;
        String number = currentExpression.substring(start, end);
        if (start > 0 && currentExpression.charAt(start - 1) == '-' && (start == 1 || isUnaryContext(currentExpression.charAt(start - 2)))) {
            currentExpression = currentExpression.substring(0, start - 1) + number;
        } else if (start == 0) {
            currentExpression = "-" + currentExpression;
        } else {
            char before = currentExpression.charAt(start - 1);
            if (before == '+' || before == '-') {
                currentExpression = currentExpression.substring(0, start - 1) + before + "(-" + number + ")";
            } else {
                currentExpression = currentExpression.substring(0, start) + "(-" + number + ")";
            }
        }
    }

    private static boolean isUnaryContext(char c) { return "+-*/^(".indexOf(c) >= 0; }
    private static boolean isOperator(String token) { return token.length() == 1 && "+-*/^".indexOf(token.charAt(0)) >= 0; }

    public String currentExpression() { return currentExpression; }
    public String resultText() { return resultText; }
    public String errorMessage() { return errorMessage; }
    public double lastResult() { return lastResult; }
    public boolean scientificMode() { return scientificMode; }
    public AngleMode angleMode() { return angleMode; }
    public void setScientificMode(boolean enabled) { scientificMode = enabled; }
    public void setAngleMode(AngleMode mode) { angleMode = Objects.requireNonNull(mode); }
}
