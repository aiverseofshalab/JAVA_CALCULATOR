package com.shalab.calculator.gui;

import com.shalab.calculator.engineering.EngineeringCalculator;
import com.shalab.calculator.history.CalculationHistory;
import com.shalab.calculator.gui.components.FormulaCard;
import javax.swing.*;import javax.swing.border.EmptyBorder;import java.awt.*;import java.util.*;import java.util.List;

/** Categorized engineering workspace made from labeled, reusable formula cards. */
public final class EngineeringPanel extends JPanel {
    private final CardLayout categories = new CardLayout(); private final JPanel pages = new JPanel(categories);
    private final CalculationHistory history; private final Runnable historyChanged;
    private final String[] names = {"Electrical", "Mechanics & Physics", "Error Analysis", "Mathematics"};
    public EngineeringPanel(CalculationHistory history, Runnable historyChanged) {
        super(new BorderLayout(14,14));this.history=history;this.historyChanged=historyChanged;setBackground(UITheme.BACKGROUND);setBorder(new EmptyBorder(18,18,18,18));
        JLabel title=UITheme.label("Engineering workspace");title.setFont(UITheme.TITLE);
        JList<String> nav=new JList<>(names);nav.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);nav.setSelectedIndex(0);nav.setBackground(UITheme.SURFACE);nav.setForeground(UITheme.TEXT);nav.setFont(UITheme.BODY);nav.setFixedCellHeight(44);nav.setBorder(new EmptyBorder(8,8,8,8));
        JPanel left=UITheme.card(new BorderLayout());left.setPreferredSize(new Dimension(190,100));left.add(UITheme.muted("CATEGORIES"),BorderLayout.NORTH);left.add(nav,BorderLayout.CENTER);
        pages.setOpaque(false);pages.add(category(electrical()),names[0]);pages.add(category(mechanics()),names[1]);pages.add(category(errors()),names[2]);pages.add(category(math()),names[3]);
        nav.addListSelectionListener(e->{if(!e.getValueIsAdjusting())categories.show(pages,nav.getSelectedValue());});
        add(title,BorderLayout.NORTH);add(left,BorderLayout.WEST);add(pages,BorderLayout.CENTER);
    }
    private JPanel category(List<FormulaCard> cards) { JPanel grid=new JPanel(new GridLayout(0,2,12,12));grid.setOpaque(false);cards.forEach(grid::add);JPanel wrapper=UITheme.panel(new BorderLayout());wrapper.add(grid,BorderLayout.NORTH);JScrollPane scroll=new JScrollPane(wrapper);scroll.setBorder(null);scroll.getViewport().setBackground(UITheme.BACKGROUND);scroll.getVerticalScrollBar().setUnitIncrement(18);return wrapperWithScroll(scroll); }
    private JPanel wrapperWithScroll(JScrollPane scroll){JPanel p=UITheme.panel(new BorderLayout());p.add(scroll);return p;}
    private List<FormulaCard> electrical(){return List.of(
      card("Ohm's Law","V = I × R",f("Voltage","V"),f("Current","A"),f("Resistance","Ω"),(v)->{int missing=countNull(v);if(missing!=1)throw new IllegalArgumentException("Enter any two values to calculate the third.");if(v[0]==null)return FormulaCard.fmt(EngineeringCalculator.voltage(v[1],v[2]))+" V";if(v[1]==null)return FormulaCard.fmt(EngineeringCalculator.current(v[0],v[2]))+" A";return FormulaCard.fmt(EngineeringCalculator.resistance(v[0],v[1]))+" Ω";}),
      card("Electrical Power","P = V × I",f("Voltage","V"),f("Current","A"),(v)->{require(v,2);return FormulaCard.fmt(EngineeringCalculator.powerVI(v[0],v[1]))+" W";}),
      card("Series Resistance","Rtotal = R₁ + R₂ + R₃",f("Resistance 1","Ω"),f("Resistance 2","Ω"),f("Resistance 3 (optional)","Ω"),(v)->FormulaCard.fmt(EngineeringCalculator.series(requireAtLeastTwo(compact(v))))+" Ω"),
      card("Parallel Resistance","1/Rtotal = Σ(1/Ri)",f("Resistance 1","Ω"),f("Resistance 2","Ω"),f("Resistance 3 (optional)","Ω"),(v)->FormulaCard.fmt(EngineeringCalculator.parallel(requireAtLeastTwo(compact(v))))+" Ω"),
      card("Voltage Divider","Vout = Vin × R₂ / (R₁ + R₂)",f("Input voltage","V"),f("R1","Ω"),f("R2","Ω"),(v)->{require(v,3);return FormulaCard.fmt(EngineeringCalculator.voltageDivider(v[0],v[1],v[2]))+" V";}));}
    private List<FormulaCard> mechanics(){return List.of(
      card("Force","F = m × a",f("Mass","kg"),f("Acceleration","m/s²"),(v)->{require(v,2);return FormulaCard.fmt(EngineeringCalculator.force(v[0],v[1]))+" N";}),
      card("Kinetic Energy","KE = ½ × m × v²",f("Mass","kg"),f("Velocity","m/s"),(v)->{require(v,2);return FormulaCard.fmt(EngineeringCalculator.kineticEnergy(v[0],v[1]))+" J";}),
      card("Potential Energy","PE = m × g × h",f("Mass","kg"),f("Gravity","m/s²"),f("Height","m"),(v)->{require(v,3);return FormulaCard.fmt(EngineeringCalculator.potentialEnergy(v[0],v[1],v[2]))+" J";}),
      card("Density","ρ = m / V",f("Mass","kg"),f("Volume","m³"),(v)->{require(v,2);return FormulaCard.fmt(EngineeringCalculator.density(v[0],v[1]))+" kg/m³";}),
      card("Momentum","p = m × v",f("Mass","kg"),f("Velocity","m/s"),(v)->{require(v,2);return FormulaCard.fmt(EngineeringCalculator.momentum(v[0],v[1]))+" kg·m/s";}),
      card("Work","W = F × d",f("Force","N"),f("Distance","m"),(v)->{require(v,2);return FormulaCard.fmt(EngineeringCalculator.work(v[0],v[1]))+" J";}),
      card("Mechanical Power","P = W / t",f("Work","J"),f("Time","s"),(v)->{require(v,2);return FormulaCard.fmt(EngineeringCalculator.power(v[0],v[1]))+" W";}));}
    private List<FormulaCard> errors(){return List.of(
      card("Absolute Error","|measured − true|",f("Measured value",""),f("True value",""),(v)->{require(v,2);return FormulaCard.fmt(EngineeringCalculator.absoluteError(v[0],v[1]));}),
      card("Relative Error","|measured − true| / |true|",f("Measured value",""),f("True value",""),(v)->{require(v,2);return FormulaCard.fmt(EngineeringCalculator.relativeError(v[0],v[1]));}),
      card("Percentage Error","Relative error × 100%",f("Measured value",""),f("True value",""),(v)->FormulaCard.fmt(EngineeringCalculator.percentageError(required(v,0),required(v,1)))+"%"));}
    private List<FormulaCard> math(){return List.of(card("Quadratic Equation Solver","ax² + bx + c = 0",f("Coefficient a",""),f("Coefficient b",""),f("Coefficient c",""),(v)->{require(v,3);var r=EngineeringCalculator.solveQuadratic(v[0],v[1],v[2]);return "D = "+FormulaCard.fmt(r.discriminant())+"  •  x₁ = "+r.first()+"  •  x₂ = "+r.second();}));}
    private FormulaCard card(String title,String formula,FormulaCard.Field a,FormulaCard.Field b,FormulaCard.Calculation calc){return new FormulaCard(title,formula,List.of(a,b),calc,history,historyChanged);}private FormulaCard card(String title,String formula,FormulaCard.Field a,FormulaCard.Field b,FormulaCard.Field c,FormulaCard.Calculation calc){return new FormulaCard(title,formula,List.of(a,b,c),calc,history,historyChanged);}
    private FormulaCard.Field f(String label,String unit){return new FormulaCard.Field(label,unit);}private void require(Double[] v,int n){for(int i=0;i<n;i++)required(v,i);}private double required(Double[] v,int i){if(v[i]==null)throw new IllegalArgumentException("Enter a value for every required field.");return v[i];}private int countNull(Double[] values){int count=0;for(Double value:values)if(value==null)count++;return count;}private double[] compact(Double[] values){return Arrays.stream(values).filter(Objects::nonNull).mapToDouble(Double::doubleValue).toArray();}private double[] requireAtLeastTwo(double[] values){if(values.length<2)throw new IllegalArgumentException("Enter at least two resistance values.");return values;}
}
