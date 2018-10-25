package org.github.nathandelane.average.distribution.recursive;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

public class RecursiveTest {

  @Test
  public void test1_3() {
    final RecursiveCalculate calculate = new RecursiveCalculate(BigDecimal.valueOf(1.3), BigDecimal.valueOf(1), BigDecimal.valueOf(5));
    calculate.calculate();
    
    assertTrue(false);
  }

  @Test
  public void test4_3() {
    final RecursiveCalculate calculate = new RecursiveCalculate(BigDecimal.valueOf(4.3), BigDecimal.valueOf(1), BigDecimal.valueOf(5));
    calculate.calculate();
    
    assertTrue(false);
  }
  
  @Test
  public void test4_35() {
    final RecursiveCalculate calculate = new RecursiveCalculate(BigDecimal.valueOf(4.35), BigDecimal.valueOf(1), BigDecimal.valueOf(5));
    calculate.calculate();
    
    assertTrue(false);
  }
  
  @Test
  public void test4_7() {
    final RecursiveCalculate calculate = new RecursiveCalculate(BigDecimal.valueOf(4.7), BigDecimal.valueOf(1), BigDecimal.valueOf(5));
    calculate.calculate();
    
    assertTrue(false);
  }
  
  @Test
  public void test3_142() {
    final RecursiveCalculate calculate = new RecursiveCalculate(BigDecimal.valueOf(3.142), BigDecimal.valueOf(1), BigDecimal.valueOf(5));
    calculate.calculate();
    
    assertTrue(false);
  }

}
