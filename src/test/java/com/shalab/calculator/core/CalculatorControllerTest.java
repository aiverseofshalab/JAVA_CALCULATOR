package com.shalab.calculator.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorControllerTest {
    @Test void modeAndAngleTransitionsPreserveExpressionAndAnswer() {
        CalculatorController controller = new CalculatorController();
        controller.handleInput("2"); controller.handleInput("+"); controller.handleInput("3");
        controller.setScientificMode(true);
        assertTrue(controller.scientificMode());
        assertEquals("2+3", controller.currentExpression());
        controller.setScientificMode(false);
        assertFalse(controller.scientificMode());
        assertEquals("2+3", controller.currentExpression());
        assertTrue(controller.handleInput("="));
        assertEquals("5", controller.resultText());
        controller.handleInput("AC");
        for (String token : new String[]{"ANS", "+", "5"}) controller.handleInput(token);
        assertTrue(controller.handleInput("="));
        assertEquals("10", controller.resultText());
    }

    @Test void errorsRetainExpressionAndShowReadableErrorState() {
        CalculatorController controller = new CalculatorController();
        for (String token : new String[]{"2", "0", "÷", "0", "="}) controller.handleInput(token);
        assertEquals("20/0", controller.currentExpression());
        assertEquals("Error", controller.resultText());
        assertTrue(controller.errorMessage().toLowerCase().contains("zero"));
    }
}
