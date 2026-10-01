package com.shalab.calculator.core;

import java.util.Locale;

/** Recursive-descent parser. Supports arithmetic, constants, postfix %, factorial and scientific functions. */
public final class ExpressionParser {
    private final String input; private final AngleMode mode; private final double ans; private int pos;
    public ExpressionParser(String input, AngleMode mode, double ans) { this.input=input; this.mode=mode; this.ans=ans; }
    public double parse() {
        if (input == null || input.isBlank()) throw error("Enter an expression.");
        double value=expression(); skip(); if(pos<input.length()) throw error("Unexpected character '"+input.charAt(pos)+"'."); return finite(value);
    }
    private double expression(){ double v=term(); while(true){ if(take('+'))v+=term(); else if(take('-'))v-=term(); else return v; } }
    private double term(){ double v=unary(); while(true){ if(take('*'))v*=unary(); else if(take('/')){double d=unary(); if(d==0)throw error("Cannot divide by zero."); v/=d;} else if(moduloAhead()){take('%');double d=unary();if(d==0)throw error("Cannot divide by zero.");v%=d;} else return v; } }
    private double unary(){ if(take('+'))return unary(); if(take('-'))return -unary(); return power(); }
    private double power(){ double v=postfix(); if(take('^')) v=Math.pow(v,unary()); return v; }
    private double postfix(){ double v=primary(); while(true){if(!moduloAhead()&&take('%'))v/=100.0;else if(take('!'))v=factorial(v);else return v;} }
    private double primary(){ skip(); if(take('(')){double v=expression(); if(!take(')'))throw error("Unmatched parentheses: missing a closing ')'.");return v;}
      if(pos<input.length()&&(Character.isDigit(input.charAt(pos))||input.charAt(pos)=='.'))return number();
      if(pos<input.length()&&Character.isLetter(input.charAt(pos))){String name=identifier(); String n=name.toLowerCase(Locale.ROOT); if(n.equals("pi"))return Math.PI;if(n.equals("e"))return Math.E;if(n.equals("ans"))return ans;
       if(!isFunction(n))throw error("Unknown constant or function: "+name+".");
       if(!take('('))throw error("Function "+name+" needs parentheses.");double x=expression();if(!take(')'))throw error("Unmatched parentheses: missing a closing ')'.");return function(n,x); }
      if(pos>=input.length())throw error("Incomplete expression: expected a value.");
      if(input.charAt(pos)==')')throw error("Unmatched closing parenthesis.");
      throw error("Invalid expression near '"+input.charAt(pos)+"'."); }
    private boolean isFunction(String n){return switch(n){case "sin","cos","tan","asin","acos","atan","sqrt","cbrt","abs","exp","ln","log","floor","ceil","round"->true;default->false;};}
    private double function(String n,double x){ double angle=mode==AngleMode.DEG?Math.toRadians(x):x; return switch(n){case "sin"->Math.sin(angle);case "cos"->Math.cos(angle);case "tan"->{if(Math.abs(Math.cos(angle))<1e-14)throw error("Tangent is undefined at this angle.");yield Math.tan(angle);}case "asin"->inverse(Math.asin(x));case "acos"->inverse(Math.acos(x));case "atan"->inverse(Math.atan(x));case "sqrt"->{if(x<0)throw error("Square root needs a non-negative value.");yield Math.sqrt(x);}case "cbrt"->Math.cbrt(x);case "abs"->Math.abs(x);case "exp"->Math.exp(x);case "ln"->{if(x<=0)throw error("Natural logarithm needs a positive value.");yield Math.log(x);}case "log"->{if(x<=0)throw error("Logarithm needs a positive value.");yield Math.log10(x);}case "floor"->Math.floor(x);case "ceil"->Math.ceil(x);case "round"->Math.floor(x+0.5);default->throw error("Unknown function: "+n);}; }
    private double inverse(double x){return mode==AngleMode.DEG?Math.toDegrees(x):x;}
    private double factorial(double x){if(x<0||x!=Math.rint(x)||x>170)throw error("Factorial requires a whole number from 0 to 170.");double r=1;for(int i=2;i<=(int)x;i++)r*=i;return r;}
    private double number(){int start=pos;while(pos<input.length()&&(Character.isDigit(input.charAt(pos))||input.charAt(pos)=='.'))pos++;try{return Double.parseDouble(input.substring(start,pos));}catch(NumberFormatException ex){throw error("Invalid number.");}}
    private String identifier(){int start=pos;while(pos<input.length()&&Character.isLetter(input.charAt(pos)))pos++;return input.substring(start,pos);}
    private boolean moduloAhead(){skip();if(pos>=input.length()||input.charAt(pos)!='%')return false;int next=pos+1;while(next<input.length()&&Character.isWhitespace(input.charAt(next)))next++;return next<input.length()&&(Character.isDigit(input.charAt(next))||input.charAt(next)=='.'||input.charAt(next)=='('||Character.isLetter(input.charAt(next)));}
    private boolean take(char c){skip();if(pos<input.length()&&input.charAt(pos)==c){pos++;return true;}return false;}
    private void skip(){while(pos<input.length()&&Character.isWhitespace(input.charAt(pos)))pos++;}
    private ExpressionException error(String m){return new ExpressionException(m);}
    private double finite(double v){if(!Double.isFinite(v))throw error("Result is outside the supported numeric range.");return v;}
}
