package com.shalab.calculator.util;
import java.text.DecimalFormat;import java.text.DecimalFormatSymbols;import java.util.Locale;
/** Consistent concise formatting for calculator results. */
public final class NumberFormatter { private NumberFormatter(){} public static String format(double value){if(!Double.isFinite(value))return "Error";if(Math.abs(value)<1e-12)value=0;double a=Math.abs(value);DecimalFormat f=new DecimalFormat(a>=1e10|| (a>0&&a<1e-8)?"0.##########E0":"0.##########",DecimalFormatSymbols.getInstance(Locale.US));f.setGroupingUsed(false);return f.format(value);} }
