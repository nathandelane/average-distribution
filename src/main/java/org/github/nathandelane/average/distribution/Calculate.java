package org.github.nathandelane.average.distribution;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * <a href="https://math.stackexchange.com/questions/2969383/is-there-a-better-algorithm-for-finding-the-minimum-potential-set-of-integers-th">
 * Is There a Better Algorithm for Finding the Minimum Potential Set of Integers that Could Comprise a Given Mean?</a>
 * <div>
 * <p>I'm trying to find the inverse of the mean, or in other words find the set of positive integers between an inclusive range that equals the mean, when divided by the number of integers identified.</p>
 * <p>Given a mean or average, such as {@code 4.3}, and a minimum value, such as 1, and a maximum value, such as {@code 5}, I could use the following set of assumptions to identify one possible set of integers that would result in the average:
 * <ol>
 *   <li>Multiply the average by powers of ten until the number is whole, for example: 
 *   {@code 4.3×10=43}</li>
 *   <li>Since I multiplied {@code 4.3} by {@code 10}, create a list of ten integers, with a value of {@code 5}</li>
 *   <li>But since {@code 5×10=50}, subtract 1 from each of seven values to have the sum of {@code 43}</li>
 * </ol>
 * <p>The resulting set of values is: {@code 5,5,5,4,4,4,4,4,4,4}</p>
 * <p>Checking I get: {@code (5+5+5+4+4+4+4+4+4+4)/10=4.3}</p>
 * <p>This feels cumbersome and naive to me, and I feel like there could be a better way to do this than by guessing, but every search I've tried doesn't seem to provide what I'm looking for.</p>
 * <p>Some other solutions I came up with for this are:
 * <ul>
 *   <li>{@code (5+5+5+5+4+4+4+4+4+3)/10=4.3}</li>
 *   <li>{@code (5+5+5+5+5+4+4+4+3+3)/10=4.3}</li>
 *   <li>{@code (5+5+5+5+5+5+4+4+3+2)/10=4.3}</li>
 * </ul></p>
 * <p><b>As an aside</b> I can guess other answers that are close to this number, or if rounded or truncated to one decimal place would approximate 4.3
 * as well, such as:
 * <ul>
 * <li>{@code 30/7=4.28571428571428571428}<li>
 * <li>{@code 26/6=4.33333333333333333333}</li>
 * <li>{@code 17/4=4.25}</li>
 * </ul></p>
 * <p>The biggest problem I see with all of these is that they are guesses, or assumptions.</p>
 * <p>I'm trying to identify a better algorithm, if one exists.</p>
 * <p>I'm thinking this is some kind of a distribution.</p>
 * </div>
 * @author nathandelane
 *
 */
public class Calculate {
  
  private static final NumberFormat NUMBER_FORMATTER = new DecimalFormat("#,###");
  
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
    
    final Map<String, Algorithm> algorithms = new HashMap<String, Algorithm>();
    final Map<String, List<BigDecimal>> algorithmResults = new HashMap<String, List<BigDecimal>>();
    final Map<String, Long> algorithmTimes = new HashMap<String, Long>();
    
    algorithms.put("movingAverage", findByMovingAverage);
    algorithms.put("maximalDistribution", distributeMaximally);
    algorithms.put("randomDistribution", calculateRandom);
    algorithms.put("subtractionDistribution", subtractionDistribution);
    
    LOGGER.info(String.format("Average: %s, Minimum Value: %s, Maximum Value: %s", average, minimumValue, maximumValue));
    
    for (Map.Entry<String, Algorithm> nextAlgorithm : algorithms.entrySet()) {
      final String algorithmName = nextAlgorithm.getKey();
      final Algorithm algorithm = nextAlgorithm.getValue();
      final long startTime = System.nanoTime();
      final List<BigDecimal> results = algorithm.calculate(multiplier);
      final long endTime = System.nanoTime();
      
      algorithmResults.put(algorithmName, results);
      algorithmTimes.put(algorithmName, (endTime - startTime));
    }
    
    for (Map.Entry<String, List<BigDecimal>> nextAlgorithm : algorithmResults.entrySet()) {
      final String algorithmName = nextAlgorithm.getKey();
      final List<BigDecimal> algorithmResult = nextAlgorithm.getValue();
      final long algorithmTime = algorithmTimes.get(algorithmName);
      
      LOGGER.info(String.format("%s %s: sum=%s, mean=%s, variance=%s, min=%s, max=%s, numElements=%s; %s", NUMBER_FORMATTER.format(algorithmTime), algorithmName, sumValues(algorithmResult), mean(algorithmResult), variance(algorithmResult), min(algorithmResult), max(algorithmResult), algorithmResult.size(), algorithmResult));
    }
    
    LOGGER.info("------------------------------");
    
    return null;
  }
  
  private final Algorithm subtractionDistribution = new Algorithm() {

    public List<BigDecimal> calculate(final BigDecimal multiplier) {
      final List<BigDecimal> listOfValues = new ArrayList<BigDecimal>(multiplier.intValue());
      
      for (int i = 0; i < multiplier.intValue(); i++) {
        listOfValues.add(new BigDecimal(maximumValue));
      }
      
      final BigDecimal sum = average.multiply(multiplier);
      
      int currentIndex = 0;
      
      while (sumValues(listOfValues).compareTo(sum) > 0) {
        final BigDecimal currentValue = listOfValues.get(currentIndex);
        final BigDecimal newValue = currentValue.subtract(ONE);
        
        listOfValues.set(currentIndex, newValue);
        
        currentIndex++;
        
        if (currentIndex >= listOfValues.size()) {
          currentIndex = 0;
        }
      }
      
      return new ArrayList<BigDecimal>(listOfValues);
    }
    
  };

  private final Algorithm distributeMaximally = new Algorithm() {

    public List<BigDecimal> calculate(final BigDecimal multiplier) {
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
        
        int index = 0;
        
        while (average(listOfValues).compareTo(average) != 0) {
//          if (average(listOfValues).compareTo(average) )
          if (decimalPartsAdded.compareTo(BigDecimal.ZERO) > 0) {
            final BigDecimal nextDecimal = listOfValues.get(index);
            
            if (nextDecimal.compareTo(maxValueAsBigDecimal) < 0) {
              final BigDecimal nextDecimalWithOneAdded = nextDecimal.add(ONE);
              
              decimalPartsAdded = decimalPartsAdded.subtract(ONE);
              
              listOfValues.set(index, nextDecimalWithOneAdded);
            }
          }
          
          index++;
          
          if (index >= listOfValues.size()) {
            index = 0;
          }
        }
      }
      else {
        throw new IllegalStateException("No multiplier identified.");
      }
      
      return new ArrayList<BigDecimal>(listOfValues);
    }
    
  };
  
  private final Algorithm findByMovingAverage = new Algorithm() {

    public List<BigDecimal> calculate(final BigDecimal multiplier) {
      final List<BigDecimal> listOfValues = new ArrayList<BigDecimal>(multiplier.intValue());
      
      for (int i = 0; i < multiplier.intValue(); i++) {
        listOfValues.add(new BigDecimal(minimumValue));
      }
      
      BigDecimal movingAverage = average(listOfValues);;

      int currentIndex = 0;
      
      do {
        final BigDecimal lastValue = listOfValues.get(currentIndex);
        
        if (lastValue.compareTo(new BigDecimal(maximumValue)) < 0) {
          final BigDecimal newValue = lastValue.add(ONE);
          
          listOfValues.set(currentIndex, newValue);
        }
        else {
          listOfValues.add(new BigDecimal(minimumValue));
        }
        
        try {
          movingAverage = average(listOfValues);
        }
        catch (ArithmeticException e) {
          continue;
        }
        
        currentIndex++;
        
        if (currentIndex >= listOfValues.size()) {
          currentIndex = 0;
        }
      }
      while (movingAverage.compareTo(average) != 0);
      
      return new ArrayList<BigDecimal>(listOfValues);
    }
    
  };
  
  private final Algorithm calculateRandom = new Algorithm() {

    public List<BigDecimal> calculate(final BigDecimal multiplier) {
      final int numberOfValues = multiplier.intValue();
      final BigDecimal expectedSumOfValues = multiplier.multiply(average);
      final Random random = new Random(System.currentTimeMillis());
      final List<BigDecimal> listOfValues = new ArrayList<BigDecimal>(numberOfValues);
      
      for (int i = 0; i < numberOfValues; i++) {
        int n = 0;
        
        do {
          n = random.nextInt(maximumValue.intValue());
        }
        while (n <= 0);
        
        final BigDecimal nextValue = new BigDecimal(n);
        
        listOfValues.add(nextValue);
      }

      final BigDecimal maxValueAsBigDecimal = new BigDecimal(maximumValue);
      final BigDecimal minValueAsBigDecimal = new BigDecimal(minimumValue);
      final BigDecimal quarterMaxValue = maxValueAsBigDecimal.divide(BigDecimal.valueOf(4), 2, RoundingMode.FLOOR);
      
      BigDecimal total = sumValues(listOfValues);
      
      if (total.compareTo(expectedSumOfValues) < 0) {
        while (total.compareTo(expectedSumOfValues) < 0) {
          for (int i = 0; i < listOfValues.size(); i++) {
            BigDecimal x = listOfValues.get(i);
            
            if (x.compareTo(maxValueAsBigDecimal) < 0) {
              final BigDecimal difference = maxValueAsBigDecimal.subtract(x);
              
              if (difference.compareTo(quarterMaxValue) < 0) {
                x = x.add(difference);
                total = total.add(difference);
              }
              else {
                x = x.add(ONE);
                total = total.add(ONE);
              }
              
              listOfValues.set(i, x);
              
              if (total.compareTo(expectedSumOfValues) == 0) {
                break;
              }
            }
            
            if (total.compareTo(expectedSumOfValues) == 0) {
              break;
            }
          }
        }
      }
      else if (total.compareTo(expectedSumOfValues) > 0) {
        while (total.compareTo(expectedSumOfValues) > 0) {
          for (int i = 0; i < listOfValues.size(); i++) {
            BigDecimal x = listOfValues.get(i);
            
            if (x.compareTo(minValueAsBigDecimal) > 0) {
              final BigDecimal difference = x.subtract(minValueAsBigDecimal);
              
              if (difference.compareTo(quarterMaxValue) < 0) {
                x = x.subtract(difference);
                total = total.subtract(difference);
              }
              else {
                x = x.subtract(ONE);
                total = total.subtract(ONE);
              }
              
              listOfValues.set(i, x);
              
              if (total.compareTo(expectedSumOfValues) == 0) {
                break;
              }
            }
            
            if (total.compareTo(expectedSumOfValues) == 0) {
              break;
            }
          }
        }
      }
      
      return new ArrayList<BigDecimal>(listOfValues);
    }
    
  };
  
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
  
  private BigDecimal average(final Collection<BigDecimal> values) {
    BigDecimal sum = sumValues(values);
    BigDecimal numberOfValues = BigDecimal.valueOf(values.size());
    
    BigDecimal average = BigDecimal.ZERO;
    
    try {
      average = sum.divide(numberOfValues, 10, RoundingMode.FLOOR);
    }
    catch (ArithmeticException e) {
      // No-op
    }
    
    return average;
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
