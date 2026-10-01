package com.shalab.calculator.gui.components;

import com.shalab.calculator.gui.UITheme;
import com.shalab.calculator.util.NumberFormatter;
import com.shalab.calculator.history.CalculationHistory;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Reusable labeled form card with validation, result and reset behavior. */
public final class FormulaCard extends JPanel {
    public record Field(String label, String unit) { }
    @FunctionalInterface public interface Calculation { String calculate(Double[] values); }
    private final List<JTextField> fields = new ArrayList<>();
    private final JLabel result = UITheme.muted("Result will appear here");
    private final CalculationHistory history; private final Runnable historyChanged; private final String title; private final String formula;

    public FormulaCard(String title, String formula, List<Field> inputs, Calculation calculation, CalculationHistory history, Runnable historyChanged) {
        super(new BorderLayout(10, 10));
        this.history=history; this.historyChanged=historyChanged; this.title=title; this.formula=formula; setBackground(UITheme.CARD);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true), new EmptyBorder(15, 15, 15, 15)));
        JLabel heading = UITheme.label(title); heading.setFont(UITheme.SECTION); JLabel formulaLabel = UITheme.muted(formula);
        JPanel headingBox = new JPanel(); headingBox.setOpaque(false); headingBox.setLayout(new BoxLayout(headingBox, BoxLayout.Y_AXIS)); headingBox.add(heading); headingBox.add(Box.createVerticalStrut(4)); headingBox.add(formulaLabel); add(headingBox, BorderLayout.NORTH);
        JPanel form = new JPanel(new GridBagLayout()); form.setOpaque(false); GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(4, 0, 4, 5); c.fill = GridBagConstraints.HORIZONTAL;
        for (int i=0;i<inputs.size();i++) { Field spec=inputs.get(i); c.gridy=i;c.gridx=0;c.weightx=0.75;JLabel label=UITheme.label(spec.label());form.add(label,c);JTextField input=UITheme.field();input.setColumns(7);input.setToolTipText(spec.unit());fields.add(input);c.gridx=1;c.weightx=1;form.add(input,c);c.gridx=2;c.weightx=0;form.add(UITheme.muted(spec.unit()),c); }
        add(form, BorderLayout.CENTER);
        JPanel footer = new JPanel(new BorderLayout(8,8)); footer.setOpaque(false);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT,6,0)); actions.setOpaque(false);
        JButton calculateButton=UITheme.button("Calculate",UITheme.ACCENT), resetButton=UITheme.button("Reset");
        calculateButton.addActionListener(e -> runCalculation(calculation)); resetButton.addActionListener(e -> reset()); actions.add(calculateButton);actions.add(resetButton);
        result.setFont(UITheme.BODY.deriveFont(Font.BOLD));footer.add(actions,BorderLayout.NORTH);footer.add(result,BorderLayout.SOUTH);add(footer,BorderLayout.SOUTH);
    }
    private void runCalculation(Calculation calculation) {
        try { Double[] values=new Double[fields.size()];for(int i=0;i<fields.size();i++){String text=fields.get(i).getText().trim();values[i]=text.isEmpty()?null:Double.valueOf(text);if(values[i]!=null&&!Double.isFinite(values[i]))throw new IllegalArgumentException("Please enter finite values.");}String answer=calculation.calculate(values);if(history!=null){history.add(title+" · "+formula,answer);if(historyChanged!=null)historyChanged.run();}result.setForeground(new Color(135,220,174));result.setText("Result: "+answer); }
        catch (NumberFormatException ex) { showError("Please enter a valid number in each completed field."); }
        catch (RuntimeException ex) { showError(ex.getMessage()==null?"Unable to calculate with these inputs.":ex.getMessage()); }
    }
    private void showError(String message) { result.setForeground(new Color(255,145,145));result.setText(message); }
    private void reset() { fields.forEach(field -> field.setText(""));result.setForeground(UITheme.MUTED);result.setText("Result will appear here"); }
    public static String fmt(double value) { return NumberFormatter.format(value); }
}
