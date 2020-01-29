package com.sap.sgs.phosphor.fosstars.model.weight;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.sgs.phosphor.fosstars.model.Visitor;
import com.sap.sgs.phosphor.fosstars.model.Weight;
import java.util.Objects;

/**
 * A mutable weight which should be used during weights adjustment.
 */
public class MutableWeight implements Weight {

  private double value;

  @JsonCreator
  public MutableWeight(@JsonProperty("value") double value) {
    this.value = Weight.check(value);
  }

  @Override
  @JsonGetter("value")
  public final double value() {
    return value;
  }

  public final MutableWeight value(double value) {
    this.value = Weight.check(value);
    return this;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof MutableWeight == false) {
      return false;
    }
    MutableWeight weight = (MutableWeight) o;
    return Double.compare(weight.value, value) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
