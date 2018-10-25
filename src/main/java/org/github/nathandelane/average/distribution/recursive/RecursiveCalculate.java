package org.github.nathandelane.average.distribution.recursive;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * <a href="https://math.stackexchange.com/questions/2969383/is-there-a-better-algorithm-for-finding-the-minimum-potential-set-of-integers-th">
 * Is There a Better Algorithm for Finding the Minimum Potential Set of Integers that Could Comprise a Given Mean?</a>
 * <div>
 * <p>The way you've phrased the question makes it more a question about computer programming than mathematics, so let's think about it from a programming perspective.</p>
 * <p>As you've noticed, the question reduces to "express the mean as a ratio of whole numbers s/n, and then find n numbers that sum to s, subject to certain constraints".</p>
 * <p>Let's sketch out a recursive algorithm that solves your problem. A recursive algorithm has the form:
 *   <ul>
 *     <li>Is there an obvious solution? Then solve the problem, and we're done.</li>
 *     <li>Is the problem obviously unsolvable? Then fail, and we're done.</li>
 *     <li>There is no obvious solution.</li>
 *     <li>Reduce the problem to one or more smaller problems.</li>
 *     <li>Solve each of the smaller problems.</li>
 *     <li>If any is unsolvable, fail.</li>
 *     <li>We solved all the smaller problems.</li>
 *     <li>Combine the solutions of the smaller problems to solve the larger problem.</li>
 *   </ul>
 * </p>
 * <p>A "backtracking" algorithm modifies this basic pattern slightly: we make many attempts to solve many smaller problems, and if one of them succeeds, we succeed; if all of them fail, then we fail.</p>
 * <p>All right. What's the version of this problem that has an obvious solution? "Find zero numbers that sum to zero", well that's easy, all sequences of zero numbers sum to zero. Moreover, "find zero numbers that sum to non zero" is also easy because we know that is impossible. Let's also assume that all the numbers are positive.</p>
 * <p>So let's write up our algorithm in pseudocode:
 *   <code>
// Find n numbers that sum to s where each is between min and max
FindSums(min, max, s, n)
  if s < 0 then fail
  if n < 0 then fail
  if s > 0 and n = 0 then fail
  if s = 0 and n = 0 then the solution is the empty sequence
  // All right, those were the easy ones.
  // The solution is not obvious, so let's break it down into a smaller
  // problem:
  let c take on values starting from min and going to max
    // c is a proposed first number in our sequence. The
    // remainder of the sequence sums to s - c, and it is of length n - 1
    // So let's find a solution to that problem:
    let tail = FindSums(min, max, s - c, n - 1)
    if that succeeded, then the solution is to append c to tail and we're done.
    if it failed, try again with a different c if there is one
  // If we get here then every attempt failed, so there is no solution.
  fail
      </code>
 * </p>
 * <p>Try that algorithm out with pencil and paper for some small examples and see if you can make any progress. It is not very efficient; can you think of ways to make this simple algorithm more efficient?</p>
 * <p>You say in your question that you are concerned that your algorithm is little better than "make a guess", but lots of algorithms in mathematics and computer programming are of the form "make a guess and then refine it in a principled way". Often the trick to getting an algorithm that performs well is to be smart about making good initial guesses, and fast at rejecting bad ones. This algorithm is poor in both regards; can you improve it?</p>
 * <p>
@MISC {2969426,
    TITLE = {Is There a Better Algorithm for Finding the Minimum Potential Set of Integers that Could Comprise a Given Mean?},
    AUTHOR = {Eric Lippert (https://math.stackexchange.com/users/21264/eric-lippert)},
    HOWPUBLISHED = {Mathematics Stack Exchange},
    NOTE = {URL:https://math.stackexchange.com/q/2969426 (version: 2018-10-24)},
    EPRINT = {https://math.stackexchange.com/q/2969426},
    URL = {https://math.stackexchange.com/q/2969426}
}
 * </p>
 * </div>
 * @author nathanlane
 *
 */
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
    
    LOGGER.info(String.format("%s recursive: sum=%s, mean=%s, variance=%s, min=%s, max=%s, numElements=%s; %s", NUMBER_FORMATTER.format(algorithmTime), sumValues(algorithmResult), mean(algorithmResult), variance(algorithmResult), min(algorithmResult), max(algorithmResult), algorithmResult.size(), algorithmResult));
    
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
