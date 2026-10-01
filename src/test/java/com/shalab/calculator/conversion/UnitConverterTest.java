package com.shalab.calculator.conversion;
import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
class UnitConverterTest {@Test void convertsLinearUnits(){assertEquals(1000,UnitConverter.convert(1,UnitConverter.Category.LENGTH,"kilometer","meter"));assertEquals(60,UnitConverter.convert(1,UnitConverter.Category.TIME,"minute","second"));assertEquals(1,UnitConverter.convert(3.6,UnitConverter.Category.SPEED,"km/h","m/s"),1e-12);}
 @Test void convertsTemperatureOffsets(){assertEquals(32,UnitConverter.convert(0,UnitConverter.Category.TEMPERATURE,"Celsius","Fahrenheit"),1e-12);assertEquals(0,UnitConverter.convert(273.15,UnitConverter.Category.TEMPERATURE,"Kelvin","Celsius"),1e-12);}}
