package org.github.nathandelane.average.distribution;

import java.math.BigInteger;
import java.util.List;

public class AverageDistribution {
  
  private final int mimnimumNumberOfValues;
  
  private final BigInteger minimumValue;
  
  private final BigInteger maximumValue;
  
  private final List<BigInteger> values;

  public AverageDistribution(int mimnimumNumberOfValues, BigInteger minimumValue, BigInteger maximumValue, List<BigInteger> values) {
    this.mimnimumNumberOfValues = mimnimumNumberOfValues;
    this.minimumValue = minimumValue;
    this.maximumValue = maximumValue;
    this.values = values;
  }
  
  

}
