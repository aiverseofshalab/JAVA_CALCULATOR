package com.shalab.calculator.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorSessionTest {
    private final CalculatorSession session = new CalculatorSession();

    @Test void evaluatesNormalCalculatorExpressions() {
        assertEquals(5, eval("2 + 3"));
        assertEquals(6, eval("10 - 4"));
        assertEquals(30, eval("5 * 6"));
        assertEquals(5, eval("20 / 4"));
        assertEquals(6, eval("2.5 + 3.5"));
        assertEquals(5, eval("-5 + 10"));
        assertEquals(20, eval("200 * 10%"));
        assertEquals(1, eval("10 % 3"));
        assertEquals(8, eval("2 ^ 3"));
        assertEquals(20, eval("(2 + 3) * 4"));
        assertEquals(14, eval("2 + 3 * 4"));
        assertEquals(30, eval("((2 + 3) * (4 + 2))"));
        assertEquals(Math.PI * 2, eval("pi * 2"), 1e-12);
        assertEquals(Math.E + 1, eval("e + 1"), 1e-12);
    }

    @Test void answerIsUpdatedOnlyBySuccessAndSurvivesClearAndModeChanges() {
        assertEquals(10, eval("5 + 5"));
        assertEquals(10, session.answer());
        session.clearExpression();
        session.setMode(CalculatorMode.SCIENTIFIC);
        assertEquals(20, eval("ans * 2"));
        session.setExpression("12 + 5");
        for (int i = 0; i < 5; i++) {
            session.setMode(CalculatorMode.NORMAL);
            assertEquals("12 + 5", session.expression());
            session.setMode(CalculatorMode.SCIENTIFIC);
            assertEquals("12 + 5", session.expression());
        }
        assertEquals(34, eval("ans + 14"));
        session.setExpression("1 / 0");
        assertThrows(ExpressionException.class, session::calculate);
        assertEquals(34, session.answer(), "A failed calculation must not overwrite ANS.");
    }

    @Test void modeAndAngleStateAreExplicitAndIndependent() {
        assertEquals(CalculatorMode.NORMAL, session.mode());
        assertEquals(AngleMode.DEG, session.angleMode());
        session.setMode(CalculatorMode.SCIENTIFIC);
        session.setAngleMode(AngleMode.RAD);
        assertEquals(1, eval("sin(pi/2)"), 1e-12);
        session.setMode(CalculatorMode.NORMAL);
        assertEquals(AngleMode.RAD, session.angleMode());
        session.setMode(CalculatorMode.SCIENTIFIC);
        assertEquals(AngleMode.RAD, session.angleMode());
    }

    @Test void reportsFriendlyExpressionErrors() {
        session.setExpression("10 / 0");
        assertEquals("Cannot divide by zero.", assertThrows(ExpressionException.class, session::calculate).getMessage());
        session.setExpression("(2 + 3");
        assertTrue(assertThrows(ExpressionException.class, session::calculate).getMessage().contains("Unmatched parentheses"));
        session.setExpression("2 +");
        assertTrue(assertThrows(ExpressionException.class, session::calculate).getMessage().contains("Incomplete expression"));
        session.setExpression("abc");
        assertTrue(assertThrows(ExpressionException.class, session::calculate).getMessage().contains("Unknown constant or function"));
        session.setExpression("");
        assertEquals("Enter an expression.", assertThrows(ExpressionException.class, session::calculate).getMessage());
    }

    private double eval(String expression) { session.setExpression(expression); return session.calculate(); }
}
