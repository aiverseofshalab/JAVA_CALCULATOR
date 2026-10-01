package com.shalab.calculator.history;
import java.time.LocalTime;import java.time.format.DateTimeFormatter;import java.util.ArrayList;import java.util.List;
/** In-memory calculation history; storage can later be replaced behind this small API. */
public final class CalculationHistory { private final List<Entry> entries=new ArrayList<>(); public record Entry(String expression,String result,String time){} public void add(String e,String r){entries.add(0,new Entry(e,r,LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));}public List<Entry> entries(){return List.copyOf(entries);}public void clear(){entries.clear();} }
