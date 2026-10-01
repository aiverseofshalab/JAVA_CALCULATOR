package com.shalab.calculator.conversion;

import java.util.*;
/** Unit conversion registry. Linear units use a base-unit scale; temperature uses affine conversion. */
public final class UnitConverter {
    public enum Category { LENGTH, MASS, TEMPERATURE, TIME, AREA, SPEED }
    private static final Map<Category,List<String>> UNITS=new EnumMap<>(Category.class);
    private static final Map<Category,Map<String,Double>> SCALE=new EnumMap<>(Category.class);
    static { define(Category.LENGTH,Map.of("meter",1d,"kilometer",1000d,"centimeter",.01,"millimeter",.001,"inch",.0254,"foot",.3048,"mile",1609.344));define(Category.MASS,Map.of("kilogram",1d,"gram",.001,"milligram",.000001,"pound",.45359237));define(Category.TIME,Map.of("second",1d,"minute",60d,"hour",3600d,"day",86400d));define(Category.AREA,Map.of("square meter",1d,"square kilometer",1e6,"square foot",.09290304));define(Category.SPEED,Map.of("m/s",1d,"km/h",1d/3.6,"mph",.44704));define(Category.TEMPERATURE,Map.of("Celsius",1d,"Fahrenheit",1d,"Kelvin",1d)); }
    private static void define(Category c,Map<String,Double> map){SCALE.put(c,map);UNITS.put(c,map.keySet().stream().sorted().toList());}
    public static List<String> units(Category c){return UNITS.get(c);} public static double convert(double value,Category c,String from,String to){if(!Double.isFinite(value))throw new IllegalArgumentException("Value must be a finite number.");Map<String,Double> units=SCALE.get(c);if(!units.containsKey(from)||!units.containsKey(to))throw new IllegalArgumentException("Choose units from the selected category.");if(c==Category.TEMPERATURE){double k=switch(from){case "Celsius"->value+273.15;case "Fahrenheit"->(value-32)*5/9+273.15;default->value;};double converted=switch(to){case "Celsius"->k-273.15;case "Fahrenheit"->(k-273.15)*9/5+32;default->k;};if(!Double.isFinite(converted))throw new IllegalArgumentException("Conversion exceeds the supported numeric range.");return converted;}double converted=value*units.get(from)/units.get(to);if(!Double.isFinite(converted))throw new IllegalArgumentException("Conversion exceeds the supported numeric range.");return converted;}
}
