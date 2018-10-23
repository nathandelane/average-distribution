package org.github.nathandelane.average.distribution;

import java.math.BigInteger;
import java.util.List;

public class AverageDistribution {
  
  private final int minimumNumberOfValues;
  
  private final BigInteger minimumValue;
  
  private final BigInteger maximumValue;
  
  private final List<BigInteger> values;

  public AverageDistribution(int minimumNumberOfValues, BigInteger minimumValue, BigInteger maximumValue, List<BigInteger> values) {
    this.minimumNumberOfValues = minimumNumberOfValues;
    this.minimumValue = minimumValue;
    this.maximumValue = maximumValue;
    this.values = values;
  }
  
  

}
