package com.shalab.calculator.gui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/** Scientific mode adds function keys and reuses the normal keypad token flow. */
public final class ScientificCalculatorPanel extends JPanel {
    private static final String[] FUNCTIONS = {
            "sin", "cos", "tan", "asin", "acos", "atan", "log", "ln", "sqrt", "cbrt",
            "abs", "exp", "floor", "ceil", "round", "!", "x²", "xʸ"
    };

    public ScientificCalculatorPanel(Consumer<String> onInput) {
        super(new BorderLayout(0, 10));
        setOpaque(false);
        JPanel functions = new JPanel(new GridLayout(0, 6, 7, 7));
        functions.setOpaque(false);
        for (String key : FUNCTIONS) {
            JButton button = UITheme.button(key);
            button.setPreferredSize(new Dimension(60, 35));
            button.addActionListener(e -> onInput.accept(key));
            functions.add(button);
        }
        add(functions, BorderLayout.NORTH);
        add(NormalCalculatorPanel.createKeys(onInput), BorderLayout.CENTER);
    }
}
