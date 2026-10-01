package com.shalab.calculator.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/** Central colors, spacing and factories for the application visual system. */
public final class UITheme {
    public static final Color BACKGROUND = new Color(18, 23, 31);
    public static final Color SURFACE = new Color(27, 34, 45);
    public static final Color CARD = new Color(32, 40, 53);
    public static final Color BORDER = new Color(54, 67, 85);
    public static final Color TEXT = new Color(232, 238, 246);
    public static final Color MUTED = new Color(151, 166, 185);
    public static final Color ACCENT = new Color(35, 133, 180);
    public static final Color KEY = new Color(43, 54, 70);
    public static final Color DANGER = new Color(177, 74, 79);
    public static final Font BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font SMALL = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font SECTION = new Font("SansSerif", Font.BOLD, 17);
    public static final Font TITLE = new Font("SansSerif", Font.BOLD, 21);
    private UITheme() { }

    public static JPanel panel(LayoutManager layout) { JPanel p = new JPanel(layout); p.setBackground(BACKGROUND); return p; }
    public static JLabel label(String text) { JLabel l = new JLabel(text); l.setForeground(TEXT); l.setFont(BODY); return l; }
    public static JButton button(String text) { return button(text, KEY); }
    public static JButton button(String text, Color color) {
        JButton b = new JButton(text);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setOpaque(true); b.setContentAreaFilled(true); b.setBorderPainted(false);
        b.setFocusPainted(false); b.setBackground(color); b.setForeground(TEXT);
        b.setFont(BODY.deriveFont(Font.BOLD)); b.setMargin(new Insets(9, 13, 9, 13));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new CompoundBorder(new LineBorder(color, 1, true), new EmptyBorder(3, 5, 3, 5)));
        b.getModel().addChangeListener(e -> {
            ButtonModel model = b.getModel();
            if (model.isPressed()) b.setBackground(color.darker());
            else if (model.isRollover()) b.setBackground(color.brighter());
            else b.setBackground(color);
        });
        return b;
    }
    public static JTextField field() {
        JTextField f = new JTextField(); f.setFont(BODY); f.setForeground(TEXT); f.setCaretColor(TEXT);
        f.setSelectionColor(ACCENT); f.setSelectedTextColor(Color.WHITE); f.setBackground(SURFACE);
        f.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 10, 8, 10))); return f;
    }
    public static void styleCombo(JComboBox<?> combo) { combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI()); combo.setFont(BODY); combo.setForeground(TEXT); combo.setBackground(SURFACE); combo.setBorder(new LineBorder(BORDER, 1, true)); combo.setRenderer(new DefaultListCellRenderer(){@Override public Component getListCellRendererComponent(JList<?> list,Object value,int index,boolean selected,boolean focus){JLabel label=(JLabel)super.getListCellRendererComponent(list,value,index,selected,focus);label.setOpaque(true);label.setBackground(selected?ACCENT:SURFACE);label.setForeground(TEXT);label.setBorder(new EmptyBorder(6,8,6,8));return label;}}); }
    public static JPanel card(LayoutManager layout) {
        JPanel p = new JPanel(layout); p.setBackground(CARD);
        p.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(16, 16, 16, 16))); return p;
    }
    public static JLabel muted(String text) { JLabel l = new JLabel(text); l.setForeground(MUTED); l.setFont(SMALL); return l; }
}
