package com.shalab.calculator.gui;

import com.shalab.calculator.history.CalculationHistory;
import javax.swing.*;import javax.swing.border.EmptyBorder;import java.awt.*;

/** Application shell: shared navigation, content tabs, status and optional history drawer. */
public final class MainFrame extends JFrame {
    private final CalculationHistory history=new CalculationHistory();
    private final JPanel historySlot=UITheme.panel(new BorderLayout());private final HistoryPanel historyPanel;
    private final CalculatorPanel calculator;
    private final JLabel status=UITheme.muted("Ready");
    private boolean historyVisible;
    public MainFrame(){super("Engineering Calculator");setDefaultCloseOperation(EXIT_ON_CLOSE);setMinimumSize(new Dimension(900,790));setSize(1100,870);getContentPane().setBackground(UITheme.BACKGROUND);setLayout(new BorderLayout());
        calculator=new CalculatorPanel(history,this::refreshHistory);historyPanel=new HistoryPanel(history,source->{calculator.reuse(source);selectTab(0);});
        JTabbedPane tabs=new JTabbedPane();tabs.addTab("Calculator",calculator);tabs.addTab("Engineering",new EngineeringPanel(history,this::refreshHistory));tabs.addTab("Converter",new ConverterPanel(history,this::refreshHistory));tabs.addTab("Help",new HelpPanel());styleTabs(tabs);
        add(header(),BorderLayout.NORTH);add(tabs,BorderLayout.CENTER);historySlot.add(historyPanel);historySlot.setVisible(false);add(historySlot,BorderLayout.EAST);
        JPanel footer=UITheme.panel(new BorderLayout());footer.setBorder(new EmptyBorder(5,18,10,18));footer.add(status,BorderLayout.WEST);add(footer,BorderLayout.SOUTH);setLocationRelativeTo(null);
    }
    private JPanel header(){JPanel h=UITheme.panel(new BorderLayout());h.setBorder(new EmptyBorder(15,20,10,18));JLabel name=UITheme.label("ENGINEERING CALCULATOR");name.setFont(UITheme.TITLE);JButton historyButton=UITheme.button("History");historyButton.addActionListener(e->toggleHistory());JPanel right=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));right.setOpaque(false);right.add(UITheme.muted("Scientific • Engineering • Conversion"));right.add(historyButton);h.add(name,BorderLayout.WEST);h.add(right,BorderLayout.EAST);return h;}
    private void toggleHistory(){historyVisible=!historyVisible;historySlot.setVisible(historyVisible);revalidate();}
    private void refreshHistory(){historyPanel.refresh();}
    private void selectTab(int index){Container p=getContentPane();for(Component c:p.getComponents())if(c instanceof JTabbedPane tabs)tabs.setSelectedIndex(index);}
    private void styleTabs(JTabbedPane tabs){tabs.setFont(UITheme.BODY.deriveFont(Font.BOLD));tabs.setBackground(UITheme.BACKGROUND);tabs.setForeground(UITheme.TEXT);tabs.setBorder(new EmptyBorder(0,12,0,12));}
}
