package com.shalab.calculator.gui;

import com.shalab.calculator.core.AngleMode;
import com.shalab.calculator.core.CalculatorController;
import com.shalab.calculator.history.CalculationHistory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Calculator view: a shared controller/display with mutually exclusive normal and scientific keypads. */
public final class CalculatorPanel extends JPanel {
    private final CalculatorController controller = new CalculatorController();
    private final CalculationHistory history;
    private final Runnable historyChanged;
    private final JTextField expressionDisplay = displayField(25);
    private final JTextField resultDisplay = displayField(46);
    private final JLabel status = UITheme.muted(" ");
    private final JLabel modeHint = UITheme.muted("NORMAL • DEG");
    private final CardLayout keypadLayout = new CardLayout();
    private final JPanel keypadCards = new JPanel(keypadLayout);

    public CalculatorPanel(CalculationHistory history, Runnable historyChanged) {
        super(new BorderLayout(12, 12));
        this.history = history;
        this.historyChanged = historyChanged;
        setBackground(UITheme.BACKGROUND);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        add(modeBar(), BorderLayout.NORTH);
        add(displayArea(), BorderLayout.CENTER);
        keypadCards.setOpaque(false);
        keypadCards.add(new NormalCalculatorPanel(this::handleInput), "normal");
        keypadCards.add(new ScientificCalculatorPanel(this::handleInput), "scientific");
        add(keypadCards, BorderLayout.SOUTH);
        keypadLayout.show(keypadCards, "normal");
        installKeyboardBindings();
        refreshDisplays();
    }

    private static JTextField displayField(int fontSize) {
        JTextField field = UITheme.field();
        field.setEditable(false);
        field.setFocusable(false);
        field.setHorizontalAlignment(SwingConstants.RIGHT);
        field.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
        field.setMinimumSize(new Dimension(fontSize > 30 ? 420 : 240, fontSize > 30 ? 90 : 58));
        field.setPreferredSize(new Dimension(fontSize > 30 ? 700 : 500, fontSize > 30 ? 96 : 58));
        return field;
    }

    private JPanel modeBar() {
        JPanel bar = UITheme.panel(new BorderLayout());
        JLabel heading = UITheme.label("Calculator");
        heading.setFont(UITheme.TITLE);
        bar.add(heading, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 0));
        controls.setOpaque(false);
        JToggleButton normal = toggle("NORMAL", true);
        JToggleButton scientific = toggle("SCIENTIFIC", false);
        ButtonGroup modes = new ButtonGroup(); modes.add(normal); modes.add(scientific);
        normal.addActionListener(e -> setMode(false));
        scientific.addActionListener(e -> setMode(true));
        JToggleButton deg = toggle("DEG", true);
        JToggleButton rad = toggle("RAD", false);
        ButtonGroup angles = new ButtonGroup(); angles.add(deg); angles.add(rad);
        deg.addActionListener(e -> { controller.setAngleMode(AngleMode.DEG); updateModeHint(); });
        rad.addActionListener(e -> { controller.setAngleMode(AngleMode.RAD); updateModeHint(); });
        controls.add(normal); controls.add(scientific); controls.add(Box.createHorizontalStrut(8)); controls.add(deg); controls.add(rad);
        bar.add(controls, BorderLayout.EAST);
        return bar;
    }

    private JToggleButton toggle(String text, boolean selected) {
        JToggleButton button = new JToggleButton(text, selected);
        button.setUI(new javax.swing.plaf.basic.BasicToggleButtonUI());
        button.setOpaque(true); button.setContentAreaFilled(true);
        button.setBackground(selected ? UITheme.ACCENT : UITheme.SURFACE);
        button.setForeground(UITheme.TEXT); button.setFont(UITheme.SMALL.deriveFont(Font.BOLD));
        button.setFocusPainted(false); button.setBorder(new EmptyBorder(8, 11, 8, 11));
        button.addItemListener(e -> button.setBackground(button.isSelected() ? UITheme.ACCENT : UITheme.SURFACE));
        return button;
    }

    private JPanel displayArea() {
        JPanel card = UITheme.card(new GridLayout(2, 1, 0, 9));
        card.setPreferredSize(new Dimension(700, 210));
        card.setMinimumSize(new Dimension(420, 190));
        JPanel expression = new JPanel(new BorderLayout(0, 5)); expression.setOpaque(false);
        JPanel expressionCaption = new JPanel(new BorderLayout()); expressionCaption.setOpaque(false);
        expressionCaption.add(UITheme.muted("EXPRESSION"), BorderLayout.WEST);
        expressionCaption.add(modeHint, BorderLayout.EAST);
        expression.add(expressionCaption, BorderLayout.NORTH);
        expression.add(expressionDisplay, BorderLayout.CENTER);
        JPanel output = new JPanel(new BorderLayout(0, 5)); output.setOpaque(false);
        output.add(UITheme.muted("RESULT"), BorderLayout.NORTH);
        output.add(resultDisplay, BorderLayout.CENTER);
        output.add(status, BorderLayout.SOUTH);
        card.add(expression); card.add(output);
        return card;
    }

    private void handleInput(String token) {
        boolean calculated = controller.handleInput(token);
        refreshDisplays();
        if (calculated) {
            history.add(controller.currentExpression(), controller.resultText());
            historyChanged.run();
        }
    }

    private void setMode(boolean scientific) {
        controller.setScientificMode(scientific);
        keypadLayout.show(keypadCards, scientific ? "scientific" : "normal");
        updateModeHint();
        keypadCards.revalidate(); keypadCards.repaint();
    }

    private void refreshDisplays() {
        expressionDisplay.setText(formatExpression(controller.currentExpression()));
        resultDisplay.setText(controller.resultText());
        status.setText(controller.errorMessage().isBlank() ? " " : controller.errorMessage());
        status.setForeground(controller.errorMessage().isBlank() ? UITheme.MUTED : new Color(255, 143, 143));
    }

    private static String formatExpression(String source) {
        return source.replace("*", " × ").replace("/", " ÷ ").replace("+", " + ").replace("-", " − ").replace("^", " ^ ");
    }

    private void updateModeHint() { modeHint.setText((controller.scientificMode() ? "SCIENTIFIC" : "NORMAL") + " • " + controller.angleMode()); }

    private void installKeyboardBindings() {
        InputMap input = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actions = getActionMap();
        bind(input, actions, "ENTER", "="); bind(input, actions, "BACK_SPACE", "DEL");
        bind(input, actions, "ESCAPE", "AC"); bind(input, actions, "DELETE", "AC");
        for (char c : "0123456789.+-*/()^%!".toCharArray()) bind(input, actions, "typed " + c, Character.toString(c));
    }

    private void bind(InputMap input, ActionMap actions, String stroke, String token) {
        String actionKey = "calculator-input-" + stroke;
        input.put(KeyStroke.getKeyStroke(stroke), actionKey);
        actions.put(actionKey, new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { handleInput(token); }
        });
    }

    public void reuse(String source) { controller.handleInput("AC"); for (int i = 0; i < source.length(); i++) controller.handleInput(String.valueOf(source.charAt(i))); refreshDisplays(); }
    public CalculatorController controller() { return controller; }
    public JTextField expressionDisplay() { return expressionDisplay; }
    public JTextField resultDisplay() { return resultDisplay; }
}
