package com.shalab.calculator.gui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/** Normal-mode keypad. Every key delegates its token to the shared calculator controller. */
public final class NormalCalculatorPanel extends JPanel {
    private static final String[][] KEYS = {
            {"AC", "DEL", "(", ")"},
            {"%", "÷", "×", "−"},
            {"7", "8", "9", "+"},
            {"4", "5", "6", "±"},
            {"1", "2", "3", "^"},
            {"0", "00", ".", "="},
            {"π", "e", "ANS", ""}
    };

    public NormalCalculatorPanel(Consumer<String> onInput) {
        super(new BorderLayout());
        setOpaque(false);
        add(createKeys(onInput), BorderLayout.CENTER);
    }

    static JPanel createKeys(Consumer<String> onInput) {
        JPanel grid = new JPanel(new GridLayout(KEYS.length, KEYS[0].length, 8, 8));
        grid.setOpaque(false);
        for (String[] row : KEYS) for (String key : row) {
            if (key.isEmpty()) {
                JPanel spacer = new JPanel(); spacer.setOpaque(false); grid.add(spacer); continue;
            }
            JButton button = UITheme.button(key, key.equals("=") ? UITheme.ACCENT : key.equals("AC") || key.equals("DEL") ? UITheme.DANGER : UITheme.KEY);
            button.setPreferredSize(new Dimension(60, 42));
            button.setMinimumSize(new Dimension(40, 38));
            button.setActionCommand(key);
            button.addActionListener(e -> onInput.accept(e.getActionCommand()));
            grid.add(button);
        }
        return grid;
    }
}
