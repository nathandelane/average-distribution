package org.github.nathandelane.average.distribution;

import java.math.BigDecimal;
import java.util.List;

public interface Algorithm {

  List<BigDecimal> calculate(BigDecimal multiplier);
  
}
