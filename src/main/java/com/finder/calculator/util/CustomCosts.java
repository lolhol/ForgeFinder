package com.finder.calculator.util;

import org.apache.commons.lang3.tuple.Triple;

public interface CustomCosts {
  // Usage - > 1st will add to gCost, 2nd will add to hCost, 3rd will add to totalCost idk y u need that tho
  Triple<Double, Double, Double> addCost(Node block);
}
