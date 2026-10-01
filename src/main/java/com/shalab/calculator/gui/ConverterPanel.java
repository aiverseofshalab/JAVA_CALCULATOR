package com.shalab.calculator.gui;

import com.shalab.calculator.conversion.UnitConverter;
import com.shalab.calculator.history.CalculationHistory;
import com.shalab.calculator.util.NumberFormatter;
import javax.swing.*;import javax.swing.border.EmptyBorder;import java.awt.*;

/** Focused unit-conversion card with category-aware unit selection. */
public final class ConverterPanel extends JPanel {
    private final JComboBox<UnitConverter.Category> category=new JComboBox<>(UnitConverter.Category.values());
    private final JComboBox<String> from=new JComboBox<>(),to=new JComboBox<>();private final JTextField value=UITheme.field();
    private final JLabel result=UITheme.label("Enter a value and choose units to convert.");private final CalculationHistory history;private final Runnable changed;
    public ConverterPanel(CalculationHistory history,Runnable changed){super(new GridBagLayout());this.history=history;this.changed=changed;setBackground(UITheme.BACKGROUND);UITheme.styleCombo(category);UITheme.styleCombo(from);UITheme.styleCombo(to);value.setText("1");
        JPanel card=UITheme.card(new GridBagLayout());card.setPreferredSize(new Dimension(540,490));GridBagConstraints c=new GridBagConstraints();c.gridx=0;c.gridwidth=2;c.fill=GridBagConstraints.HORIZONTAL;c.weightx=1;c.insets=new Insets(8,8,8,8);
        JLabel title=UITheme.label("Unit converter");title.setFont(UITheme.TITLE);c.gridy=0;card.add(title,c);c.gridy=1;card.add(UITheme.muted("Convert values between common engineering units."),c);
        c.gridwidth=1;c.gridy=2;c.gridx=0;c.weightx=0.35;card.add(labeled("Category"),c);c.gridx=1;c.weightx=1;card.add(category,c);
        c.gridy=3;c.gridx=0;c.weightx=0.35;card.add(labeled("Value"),c);c.gridx=1;c.weightx=1;card.add(value,c);
        c.gridy=4;c.gridx=0;c.weightx=0.35;card.add(labeled("From"),c);c.gridx=1;c.weightx=1;card.add(from,c);
        c.gridy=5;c.gridx=0;c.weightx=0.35;card.add(labeled("To"),c);c.gridx=1;c.weightx=1;card.add(to,c);
        JButton swap=UITheme.button("Swap units ↕");c.gridx=0;c.gridy=6;c.gridwidth=2;c.weightx=1;card.add(swap,c);
        JButton convert=UITheme.button("Convert",UITheme.ACCENT);c.gridy=7;card.add(convert,c);
        JPanel resultCard=UITheme.card(new BorderLayout());result.setFont(new Font("Monospaced",Font.BOLD,18));resultCard.add(UITheme.muted("RESULT"),BorderLayout.NORTH);resultCard.add(result,BorderLayout.CENTER);c.gridy=8;c.fill=GridBagConstraints.BOTH;c.weighty=1;card.add(resultCard,c);
        GridBagConstraints outer=new GridBagConstraints();outer.anchor=GridBagConstraints.CENTER;outer.fill=GridBagConstraints.NONE;add(card,outer);
        category.addActionListener(e->loadUnits());swap.addActionListener(e->{Object a=from.getSelectedItem(),b=to.getSelectedItem();from.setSelectedItem(b);to.setSelectedItem(a);});convert.addActionListener(e->convert());loadUnits();
    }
    private JLabel labeled(String text){JLabel l=UITheme.label(text);l.setFont(UITheme.BODY.deriveFont(Font.BOLD));return l;}
    private void loadUnits(){var type=(UnitConverter.Category)category.getSelectedItem();String previousFrom=(String)from.getSelectedItem(),previousTo=(String)to.getSelectedItem();DefaultComboBoxModel<String> fm=new DefaultComboBoxModel<>(UnitConverter.units(type).toArray(String[]::new));from.setModel(fm);to.setModel(new DefaultComboBoxModel<>(UnitConverter.units(type).toArray(String[]::new)));if(previousFrom!=null)from.setSelectedItem(previousFrom);if(previousTo!=null)to.setSelectedItem(previousTo);}
    private void convert(){try{double amount=Double.parseDouble(value.getText().trim());if(!Double.isFinite(amount))throw new NumberFormatException();var type=(UnitConverter.Category)category.getSelectedItem();String src=(String)from.getSelectedItem(),dst=(String)to.getSelectedItem();double converted=UnitConverter.convert(amount,type,src,dst);String display=NumberFormatter.format(amount)+" "+src+"  =  "+NumberFormatter.format(converted)+" "+dst;result.setForeground(UITheme.TEXT);result.setText(display);history.add("Convert: "+NumberFormatter.format(amount)+" "+src+" → "+dst,NumberFormatter.format(converted)+" "+dst);changed.run();}catch(NumberFormatException ex){showError("Please enter a valid number.");}catch(RuntimeException ex){showError(ex.getMessage()==null?"Please select valid units.":ex.getMessage());}}
    private void showError(String message){result.setForeground(new Color(255,145,145));result.setText(message);}
}
