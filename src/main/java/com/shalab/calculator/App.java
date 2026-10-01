package com.shalab.calculator;
import com.shalab.calculator.gui.MainFrame;import javax.swing.*;import java.awt.*;
/** Application entry point. */
public final class App {private App(){}public static void main(String[] args){if(!GraphicsEnvironment.isHeadless())SwingUtilities.invokeLater(()->new MainFrame().setVisible(true));}}
