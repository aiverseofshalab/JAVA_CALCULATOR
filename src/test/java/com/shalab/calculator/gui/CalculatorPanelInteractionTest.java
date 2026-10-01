package com.shalab.calculator.gui;

import com.shalab.calculator.core.AngleMode;
import com.shalab.calculator.core.CalculatorController;
import com.shalab.calculator.history.CalculationHistory;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorPanelInteractionTest {
    @Test void buttonsUpdateSeparateExpressionAndResultDisplays() throws Exception {
        runOnEdt(() -> {
            CalculatorPanel panel = new CalculatorPanel(new CalculationHistory(), () -> {});
            assertEquals("", panel.expressionDisplay().getText());
            click(panel, "2"); assertEquals("2", panel.expressionDisplay().getText());
            click(panel, "+"); assertEquals("2 + ", panel.expressionDisplay().getText());
            click(panel, "3"); click(panel, "=");
            assertEquals("2 + 3", panel.expressionDisplay().getText());
            assertEquals("5", panel.resultDisplay().getText());
            assertFalse(panel.expressionDisplay().isEditable());
            assertFalse(panel.resultDisplay().isEditable());
            return null;
        });
    }

    @Test void normalArithmeticPrecedenceParenthesesAndEditingWork() throws Exception {
        runOnEdt(() -> {
            CalculatorPanel panel = new CalculatorPanel(new CalculationHistory(), () -> {});
            enter(panel, "7", "×", "8", "="); assertEquals("56", panel.resultDisplay().getText());
            enter(panel, "AC", "2", "+", "3", "×", "4", "="); assertEquals("14", panel.resultDisplay().getText());
            enter(panel, "AC", "(", "2", "+", "3", ")", "×", "4", "="); assertEquals("20", panel.resultDisplay().getText());
            enter(panel, "AC", "1", "0", "÷", "4", "="); assertEquals("2.5", panel.resultDisplay().getText());
            enter(panel, "AC", "5", "DEL"); assertEquals("", panel.expressionDisplay().getText());
            return null;
        });
    }

    @Test void normalScientificCardsSwitchWithoutLosingControllerState() throws Exception {
        runOnEdt(() -> {
            CalculatorPanel panel = new CalculatorPanel(new CalculationHistory(), () -> {});
            enter(panel, "2", "+", "3");
            click(panel, "SCIENTIFIC");
            assertTrue(panel.controller().scientificMode());
            assertEquals("2 + 3", panel.expressionDisplay().getText());
            assertEquals(1, visibleButtons(panel, "sin").size());
            assertEquals(1, visibleButtons(panel, "AC").size(), "Scientific mode retains the normal keypad.");
            click(panel, "NORMAL");
            assertFalse(panel.controller().scientificMode());
            assertEquals("2 + 3", panel.expressionDisplay().getText());
            assertEquals(0, visibleButtons(panel, "sin").size());
            assertEquals(1, visibleButtons(panel, "AC").size());
            click(panel, "="); assertEquals("5", panel.resultDisplay().getText());
            return null;
        });
    }

    @Test void scientificFunctionsAnglesAnsAndErrorsAreVisible() throws Exception {
        runOnEdt(() -> {
            CalculatorPanel panel = new CalculatorPanel(new CalculationHistory(), () -> {});
            click(panel, "SCIENTIFIC");
            enter(panel, "sin", "3", "0", "=");
            assertEquals("0.5", panel.resultDisplay().getText());
            enter(panel, "AC", "sqrt", "2", "5", ")", "="); assertEquals("5", panel.resultDisplay().getText());
            enter(panel, "AC", "2", "x²", "="); assertEquals("4", panel.resultDisplay().getText());
            enter(panel, "AC", "2", "xʸ", "3", "="); assertEquals("8", panel.resultDisplay().getText());
            enter(panel, "AC", "5", "!", "="); assertEquals("120", panel.resultDisplay().getText());
            enter(panel, "AC", "2", "=", "ANS", "+", "5", "="); assertEquals("7", panel.resultDisplay().getText());
            enter(panel, "AC", "2", "0", "÷", "0", "=");
            assertEquals("Error", panel.resultDisplay().getText());
            assertTrue(panel.controller().errorMessage().toLowerCase().contains("zero"));
            assertEquals("20 ÷ 0", panel.expressionDisplay().getText());
            click(panel, "RAD"); assertEquals(AngleMode.RAD, panel.controller().angleMode());
            return null;
        });
    }

    @Test void keyboardBindingsRouteThroughControllerAndAcClearsBothDisplays() throws Exception {
        runOnEdt(() -> {
            CalculatorPanel panel = new CalculatorPanel(new CalculationHistory(), () -> {});
            for (String key : new String[]{"typed 2", "typed +", "typed 3", "ENTER"}) invokeBinding(panel, key);
            assertEquals("2 + 3", panel.expressionDisplay().getText());
            assertEquals("5", panel.resultDisplay().getText());
            click(panel, "AC");
            assertEquals("", panel.expressionDisplay().getText());
            assertEquals("", panel.resultDisplay().getText());
            return null;
        });
    }

    @Test void plusMinusTogglesOnlyTheLastEnteredNumber() {
        CalculatorController controller = new CalculatorController();
        controller.handleInput("5"); controller.handleInput("±"); assertEquals("-5", controller.currentExpression());
        controller.handleInput("±"); assertEquals("5", controller.currentExpression());
        controller.handleInput("AC");
        for (String token : new String[]{"2", "+", "5"}) controller.handleInput(token);
        controller.handleInput("±");
        assertEquals("2+(-5)", controller.currentExpression());
        assertTrue(controller.handleInput("=")); assertEquals("-3", controller.resultText());
    }

    private static void enter(CalculatorPanel panel, String... keys) { for (String key : keys) click(panel, key); }
    private static void click(CalculatorPanel panel, String label) {
        AbstractButton button = visibleButtons(panel, label).stream().findFirst().orElseThrow(() -> new AssertionError("No visible button: " + label));
        button.doClick();
    }
    private static List<AbstractButton> visibleButtons(Component root, String label) {
        List<AbstractButton> buttons = new ArrayList<>(); collect(root, true, buttons, label); return buttons;
    }
    private static void collect(Component node, boolean ancestorsVisible, List<AbstractButton> found, String label) {
        boolean visible = ancestorsVisible && node.isVisible();
        if (visible && node instanceof AbstractButton button && button.getText().equals(label)) found.add(button);
        if (node instanceof Container container) for (Component child : container.getComponents()) collect(child, visible, found, label);
    }
    private static void invokeBinding(CalculatorPanel panel, String stroke) {
        Object key = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(KeyStroke.getKeyStroke(stroke));
        assertNotNull(key, "Missing key binding: " + stroke);
        panel.getActionMap().get(key).actionPerformed(new java.awt.event.ActionEvent(panel, java.awt.event.ActionEvent.ACTION_PERFORMED, stroke));
    }
    private static <T> T runOnEdt(java.util.concurrent.Callable<T> task) throws Exception {
        AtomicReference<T> value = new AtomicReference<>(); AtomicReference<Throwable> error = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> { try { value.set(task.call()); } catch (Throwable t) { error.set(t); } });
        if (error.get() != null) throw new AssertionError("Swing interaction failed", error.get());
        return value.get();
    }
}
