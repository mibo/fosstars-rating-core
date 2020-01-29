package com.sap.sgs.phosphor.fosstars.model.feature.example;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sap.sgs.phosphor.fosstars.model.Value;
import com.sap.sgs.phosphor.fosstars.model.feature.AbstractFeature;
import com.sap.sgs.phosphor.fosstars.model.value.BooleanValue;

/**
 * This is a sample feature which tells if a security review has been done. The feature is stateless
 * and therefore immutable. This feature is only for demo purposes.
 */
@JsonSerialize
public class SecurityReviewDoneExample extends AbstractFeature<Boolean> {

  SecurityReviewDoneExample() {
    super("Security review status (example)");
  }

  @Override
  public BooleanValue value(Boolean object) {
    return new BooleanValue(this, object);
  }

  @Override
  public Value<Boolean> parse(String string) {
    throw new UnsupportedOperationException();
  }

}
