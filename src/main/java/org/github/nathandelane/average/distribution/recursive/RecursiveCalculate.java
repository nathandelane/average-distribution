package org.github.nathandelane.average.distribution.recursive;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

public class RecursiveCalculate {
  
  private static final NumberFormat NUMBER_FORMATTER = new DecimalFormat("#,###");
  
  private static final Logger LOGGER = Logger.getLogger(RecursiveCalculate.class);
  
  private static final BigDecimal ONE = BigDecimal.valueOf(1);

  private final BigDecimal average;
  
  private final BigDecimal minimumValue;
  
  private final BigDecimal maximumValue;
  
  private final int divisionPrecision;

  public RecursiveCalculate(final BigDecimal average, final BigDecimal minimumValue, final BigDecimal maximumValue) {
    this.average = average;
    this.minimumValue = minimumValue;
    this.maximumValue = maximumValue;
    
    final BigDecimal bdDivPrec = new BigDecimal(average.intValue());
    final BigDecimal remainder = average.subtract(bdDivPrec);
    
    divisionPrecision = remainder.precision() + 1;
  }
  
  public List<BigDecimal> calculate() {
    final long startTime = System.nanoTime();
    final List<BigDecimal> algorithmResult = findSums(minimumValue, maximumValue, new ArrayList<BigDecimal>());
    final long endTime = System.nanoTime();
    
    final long algorithmTime = (endTime - startTime);
    
    LOGGER.info(String.format("%s recursive: sum=%s, mean=%s, variance=%s, min=%s, max=%s; %s", NUMBER_FORMATTER.format(algorithmTime), sumValues(algorithmResult), mean(algorithmResult), variance(algorithmResult), min(algorithmResult), max(algorithmResult), algorithmResult));
    
    return new ArrayList<BigDecimal>(algorithmResult);
  }
  
  private List<BigDecimal> findSums(final BigDecimal min, final BigDecimal max, final List<BigDecimal> sum) {
    final List<BigDecimal> tail = new ArrayList<BigDecimal>(sum);
    
    if (tail.isEmpty()) {
      tail.add(min);
    }
    
    if (average(tail).compareTo(average) < 0) {
      final int lastIndex = (tail.size() - 1);
      final BigDecimal lastValue = tail.get(lastIndex);
      
      if (lastValue.compareTo(max) < 0) {
        final BigDecimal lastValuePlusOne = lastValue.add(ONE);
        tail.set(lastIndex, lastValuePlusOne);
  
        return new ArrayList<BigDecimal>(findSums(min, max, tail));
      }
      else {
        tail.add(min);
        
        return new ArrayList<BigDecimal>(findSums(min, max, tail));
      }
    }
    else if (average(tail).compareTo(average) > 0) {
      final int lastIndex = (tail.size() - 1);
      final BigDecimal lastValue = tail.get(lastIndex);
      
      if (lastValue.compareTo(min) > 0) {
        final BigDecimal lastValueMinusOne = lastValue.subtract(ONE);
        tail.set(lastIndex, lastValueMinusOne);
        tail.add(min);
        
        return new ArrayList<BigDecimal>(findSums(min, max, tail));
      }
    }
    
    return new ArrayList<BigDecimal>(tail);
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
    
    if (values == null || values.size() == 0) {
      average = BigDecimal.ZERO;
    }
    else {
      final BigDecimal sum = sumValues(values);
      final BigDecimal numberOfValues = new BigDecimal(values.size());
      
      average = sum.divide(numberOfValues, divisionPrecision, RoundingMode.FLOOR);
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
    
    variance = variance.divide(numberOfValues, divisionPrecision, RoundingMode.FLOOR);
    
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
  
  private BigDecimal average(final Collection<BigDecimal> values) {
    BigDecimal sum = sumValues(values);
    BigDecimal numberOfValues = BigDecimal.valueOf(values.size());
    
    BigDecimal average = BigDecimal.ZERO;
    
    try {
      average = sum.divide(numberOfValues, divisionPrecision, RoundingMode.FLOOR);
    }
    catch (ArithmeticException e) {
      // No-op
    }
    
    return average;
  }

}
