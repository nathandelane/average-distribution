package org.github.nathandelane.average.distribution;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

public class CalculateTest {
  
  @Test
  public void test4_3() {
    final Calculate calculate = new Calculate(BigDecimal.valueOf(4.3), BigInteger.valueOf(1), BigInteger.valueOf(5));
    final AverageDistribution averageDistribution = calculate.calculate();
    
    assertTrue(false);
    assertNotNull(averageDistribution);
  }
  
  @Test
  public void test4_1() {
    final Calculate calculate = new Calculate(BigDecimal.valueOf(4.1), BigInteger.valueOf(1), BigInteger.valueOf(5));
    final AverageDistribution averageDistribution = calculate.calculate();
    
    assertTrue(false);
    assertNotNull(averageDistribution);
  }
  
  @Test
  public void test4_7() {
    final Calculate calculate = new Calculate(BigDecimal.valueOf(4.7), BigInteger.valueOf(1), BigInteger.valueOf(5));
    final AverageDistribution averageDistribution = calculate.calculate();
    
    assertTrue(false);
    assertNotNull(averageDistribution);
  }
  
  @Test
  public void test3_7() {
    final Calculate calculate = new Calculate(BigDecimal.valueOf(3.7), BigInteger.valueOf(1), BigInteger.valueOf(5));
    final AverageDistribution averageDistribution = calculate.calculate();
    
    assertTrue(false);
    assertNotNull(averageDistribution);
  }
  
}
