package org.github.nathandelane.average.distribution;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

public class Calculate {
  
  private static final Logger LOGGER = Logger.getLogger(Calculate.class);
  
  private static final BigDecimal TEN = BigDecimal.valueOf(10);

  private static final BigDecimal ONE = BigDecimal.valueOf(1);

  private final BigDecimal average;
  
  private final BigInteger minimumValue;
  
  private final BigInteger maximumValue;

  public Calculate(final BigDecimal average, final BigInteger minimumValue, final BigInteger maximumValue) {
    this.average = average;
    this.minimumValue = minimumValue;
    this.maximumValue = maximumValue;
  }
  
  public AverageDistribution calculate() {
    final BigInteger integerValueOfAverage = average.toBigInteger();
    
    if (integerValueOfAverage.equals(BigInteger.ZERO)) {
      throw new IllegalStateException(String.format("Cannot divide by zero: average=%s, integerValueOfAverage=%s", average, integerValueOfAverage));
    }
    if (minimumValue.equals(maximumValue)) {
      throw new IllegalStateException(String.format("Minimum value %s cannot be equal to maximum value %s.", minimumValue, maximumValue));
    }
    if (minimumValue.compareTo(maximumValue) > 0) {
      throw new IllegalStateException(String.format("Minimum value %s cannot be greater than maximum value %s.", minimumValue, maximumValue));
    }
    if (new BigDecimal(minimumValue).compareTo(average) > 0) {
      throw new IllegalStateException(String.format("Minimum value cannot be larger than average: minimumValue=%s, average=%s", minimumValue, average));
    }
    if (average.compareTo(new BigDecimal(maximumValue)) > 0) {
      throw new IllegalStateException(String.format("Maximum value cannot be larger than average: maximumValue=%s, average=%s", maximumValue, average));
    }
    
    final BigDecimal doubleValueOfIntegerValueOfAverage = new BigDecimal(integerValueOfAverage);
    
    BigDecimal decimalPartOfValue = average.subtract(doubleValueOfIntegerValueOfAverage);
    BigDecimal multiplier = ONE;
    
    do {
      multiplier = multiplier.multiply(TEN);
      
      final BigDecimal multiple = decimalPartOfValue.multiply(TEN);
      final BigDecimal doubleValueOfIntegerValueOfMultiple = new BigDecimal(multiple.toBigInteger());
      
      decimalPartOfValue = multiple.subtract(doubleValueOfIntegerValueOfMultiple);
    }
    while (decimalPartOfValue.compareTo(BigDecimal.ZERO) > 0);
    
    LOGGER.info(String.format("Average: %s, Minimum Value: %s, Maximum Value: %s", average, minimumValue, maximumValue));
    
    final List<BigDecimal> maximalDistribution = distributeMaximally(multiplier);
    
    LOGGER.info(String.format("maximalDistribution: %s, sum=%s, mean=%s, variance=%s, min=%s, max=%s", maximalDistribution, sumValues(maximalDistribution), mean(maximalDistribution), variance(maximalDistribution), min(maximalDistribution), max(maximalDistribution)));

    final List<BigDecimal> randomDistribution = calculateRandom(multiplier);
    
    LOGGER.info(String.format("randomDistribution: %s, sum=%s, mean=%s, variance=%s, min=%s, max=%s", randomDistribution, sumValues(randomDistribution), mean(randomDistribution), variance(randomDistribution), min(randomDistribution), max(randomDistribution)));
    
    return null;
  }
  
  private List<BigDecimal> distributeMaximally(final BigDecimal multiplier) {
    int numberOfValues = 0;
    
    List<BigDecimal> listOfValues;
    
    if (multiplier != null) {
      numberOfValues = multiplier.toBigInteger().intValue();
      listOfValues = new ArrayList<BigDecimal>(numberOfValues);
      
      for (int i = 0; i < numberOfValues; i++) {
        final BigDecimal nextValue = average.multiply(ONE);
        
        listOfValues.add(i, nextValue);
      }
      
      BigDecimal decimalPartsAdded = BigDecimal.ZERO;
      
      while (containsValuesWithDecimals(listOfValues)) {
        for (int i = 0; i < numberOfValues; i++) {
          final BigDecimal nextDecimal = listOfValues.get(i);
          final BigDecimal integerPart = new BigDecimal(nextDecimal.toBigInteger());
          final BigDecimal remainder = nextDecimal.subtract(integerPart);
          
          decimalPartsAdded = decimalPartsAdded.add(remainder);
          
          listOfValues.set(i, integerPart);
        }
      }
      
      final BigDecimal maxValueAsBigDecimal = new BigDecimal(maximumValue);
      
      for (int i = 0; i < numberOfValues; i++) {
        if (decimalPartsAdded.compareTo(BigDecimal.ZERO) > 0) {
          final BigDecimal nextDecimal = listOfValues.get(i);
          
          if (nextDecimal.compareTo(maxValueAsBigDecimal) < 0) {
            final BigDecimal nextDecimalWithOneAdded = nextDecimal.add(ONE);
            
            decimalPartsAdded = decimalPartsAdded.subtract(ONE);
            
            listOfValues.set(i, nextDecimalWithOneAdded);
          }
        }
        else {
          break;
        }
      }
    }
    else {
      throw new IllegalStateException("No multiplier identified.");
    }
    
    return new ArrayList<BigDecimal>(listOfValues);
  }
  
  
  public List<BigDecimal> calculateRandom(final BigDecimal multiplier) {
    final int numberOfValues = multiplier.intValue();
    final BigDecimal expectedSumOfValues = multiplier.multiply(average);
    final Random random = new Random(System.currentTimeMillis());
    final List<BigDecimal> values = new ArrayList<BigDecimal>(numberOfValues);
    
    BigDecimal total = BigDecimal.ZERO;
    
    for (int i = 0; i < numberOfValues; i++) {
      int n = 0;
      
      do {
        n = random.nextInt(maximumValue.intValue());
      }
      while (n <= 0);
      
      final BigDecimal nextValue = new BigDecimal(n);
      
      values.add(nextValue);
      
      total = total.add(nextValue);
    }
    
    while (total.compareTo(expectedSumOfValues) < 0) {
      for (int i = 0; i < values.size(); i++) {
        BigDecimal x = values.get(i);
        
        if (x.compareTo(new BigDecimal(maximumValue)) < 0) {
          x = x.add(ONE);
          total = total.add(ONE);
          values.set(i, x);
        }
        
        if (total.compareTo(expectedSumOfValues) == 0) {
          break;
        }
      }
    }
    
    return new ArrayList<BigDecimal>(values);
  }
  
  private BigDecimal sumValues(final Collection<BigDecimal> values) {
    BigDecimal sum = BigDecimal.ZERO;
    
    for (final BigDecimal nextValue : values) {
      sum = sum.add(nextValue);
    }
    
    return sum;
  }
  
  private BigDecimal mean(final Collection<BigDecimal> values) {
    final BigDecimal average;
    
    if (values == null || values.size() ==0) {
      average = BigDecimal.ZERO;
    }
    else {
      final BigDecimal sum = sumValues(values);
      final BigDecimal numberOfValues = new BigDecimal(values.size());
      
      average = sum.divide(numberOfValues);
    }
    
    return average;
  }
  
  private BigDecimal variance(final Collection<BigDecimal> values) {
    final BigDecimal mean = mean(values);
    final BigDecimal numberOfValues = new BigDecimal(values.size());
    
    BigDecimal variance = BigDecimal.ZERO;
    
    for (final BigDecimal nextValue : values) {
      final BigDecimal difference = nextValue.subtract(mean);
      final BigDecimal square = difference.pow(2);
      
      variance = variance.add(square);
    }
    
    variance = variance.divide(numberOfValues);
    
    return variance;
  }
  
  private BigDecimal min(final Collection<BigDecimal> values) {
    BigDecimal min = null;
    
    for (final BigDecimal nextValue : values) {
      if (min == null || min.compareTo(nextValue) > 0) {
        min = nextValue;
      }
    }
    
    return min;
  }
  
  private BigDecimal max(final Collection<BigDecimal> values) {
    BigDecimal max = null;
    
    for (final BigDecimal nextValue : values) {
      if (max == null || max.compareTo(nextValue) < 0) {
        max = nextValue;
      }
    }
    
    return max;
  }

  private boolean containsValuesWithDecimals(final List<BigDecimal> listOfValues) {
    boolean containsDecimals = false;
    
    for (final BigDecimal nextDecimal : listOfValues) {
      final BigDecimal integerPart = new BigDecimal(nextDecimal.toBigInteger());
      final BigDecimal remainder = nextDecimal.subtract(integerPart);
      
      if (remainder.compareTo(BigDecimal.ZERO) > 0) {
        containsDecimals = true;
        break;
      }
    }
    
    return containsDecimals;
  }
  
}
