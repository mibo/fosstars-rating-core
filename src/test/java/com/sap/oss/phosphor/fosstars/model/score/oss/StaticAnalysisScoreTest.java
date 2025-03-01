package com.sap.oss.phosphor.fosstars.model.score.oss;

import static com.sap.oss.phosphor.fosstars.TestUtils.DELTA;
import static com.sap.oss.phosphor.fosstars.model.Score.MIN;
import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssFeatures.LANGUAGES;
import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssFeatures.RUNS_CODEQL_SCANS;
import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssFeatures.USES_CODEQL_CHECKS;
import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssFeatures.USES_FIND_SEC_BUGS;
import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssFeatures.USES_LGTM_CHECKS;
import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssFeatures.WORST_LGTM_GRADE;
import static com.sap.oss.phosphor.fosstars.model.value.Language.JAVA;
import static com.sap.oss.phosphor.fosstars.model.value.LgtmGrade.D;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.sap.oss.phosphor.fosstars.model.Confidence;
import com.sap.oss.phosphor.fosstars.model.Score;
import com.sap.oss.phosphor.fosstars.model.value.Languages;
import com.sap.oss.phosphor.fosstars.model.value.ScoreValue;
import com.sap.oss.phosphor.fosstars.util.Json;
import java.io.IOException;
import org.junit.Test;

public class StaticAnalysisScoreTest {

  @Test
  public void testCalculateWithFeatureValues() {
    StaticAnalysisScore score = new StaticAnalysisScore();

    ScoreValue scoreValue = score.calculate(
        WORST_LGTM_GRADE.value(D),
        USES_LGTM_CHECKS.value(true),
        USES_CODEQL_CHECKS.value(true),
        RUNS_CODEQL_SCANS.value(true),
        LANGUAGES.value(Languages.of(JAVA)),
        USES_FIND_SEC_BUGS.value(false));

    assertFalse(scoreValue.isUnknown());
    assertFalse(scoreValue.isNotApplicable());
    assertTrue(Score.INTERVAL.contains(scoreValue.get()));
    assertEquals(Confidence.MAX, scoreValue.confidence(), DELTA);
    assertSame(score, scoreValue.score());
    assertEquals(3, scoreValue.usedValues().size());
  }

  @Test
  public void testCalculateWithAllUnknown() {
    StaticAnalysisScore score = new StaticAnalysisScore();

    ScoreValue scoreValue = score.calculate(
        WORST_LGTM_GRADE.unknown(),
        USES_LGTM_CHECKS.unknown(),
        USES_CODEQL_CHECKS.unknown(),
        RUNS_CODEQL_SCANS.unknown(),
        LANGUAGES.unknown(),
        USES_FIND_SEC_BUGS.unknown());

    assertTrue(scoreValue.isUnknown());
    assertFalse(scoreValue.isNotApplicable());
    assertEquals(Confidence.MIN, scoreValue.confidence(), DELTA);
    assertSame(score, scoreValue.score());
    assertEquals(3, scoreValue.usedValues().size());
  }

  @Test
  public void testCalculateWithAllNotApplicable() {
    StaticAnalysisScore score = new StaticAnalysisScore();

    ScoreValue codeqlScoreValue = new ScoreValue(new CodeqlScore())
        .makeNotApplicable()
        .confidence(Confidence.MAX);
    ScoreValue lgtmScoreValue = new ScoreValue(new LgtmScore())
        .makeNotApplicable()
        .confidence(Confidence.MAX);
    ScoreValue findSecBugsScoreValue = new ScoreValue(new FindSecBugsScore())
        .makeNotApplicable()
        .confidence(Confidence.MAX);

    ScoreValue scoreValue = score.calculate(
        codeqlScoreValue, lgtmScoreValue, findSecBugsScoreValue);

    assertFalse(scoreValue.isUnknown());
    assertTrue(scoreValue.isNotApplicable());
    assertEquals(Confidence.MAX, scoreValue.confidence(), DELTA);
    assertSame(score, scoreValue.score());
    assertEquals(3, scoreValue.usedValues().size());
    assertTrue(scoreValue.usedValues().contains(lgtmScoreValue));
    assertTrue(scoreValue.usedValues().contains(findSecBugsScoreValue));
  }

  @Test
  public void testCalculateWithSubScoreValues() {
    StaticAnalysisScore score = new StaticAnalysisScore();

    ScoreValue codeqlScoreValue = new ScoreValue(new CodeqlScore())
        .set(MIN)
        .confidence(Confidence.MAX);
    ScoreValue lgtmScoreValue = new ScoreValue(new LgtmScore())
        .set(MIN)
        .confidence(Confidence.MAX);
    ScoreValue findSecBugsScoreValue = new ScoreValue(new FindSecBugsScore())
        .set(MIN)
        .confidence(Confidence.MAX);

    ScoreValue scoreValue = score.calculate(
        codeqlScoreValue, lgtmScoreValue, findSecBugsScoreValue);

    assertFalse(scoreValue.isUnknown());
    assertFalse(scoreValue.isNotApplicable());
    assertEquals(MIN, scoreValue.get(), DELTA);
    assertEquals(Confidence.MAX, scoreValue.confidence(), DELTA);
    assertSame(score, scoreValue.score());
    assertEquals(3, scoreValue.usedValues().size());
    assertTrue(scoreValue.usedValues().contains(lgtmScoreValue));
    assertTrue(scoreValue.usedValues().contains(findSecBugsScoreValue));
  }

  @Test
  public void testCalculateWithFindSecBugsNotApplicable() {
    StaticAnalysisScore score = new StaticAnalysisScore();

    final double value = 5.5;

    ScoreValue codeqlScoreValue = new ScoreValue(new CodeqlScore())
        .set(value)
        .confidence(Confidence.MAX);
    ScoreValue lgtmScoreValue = new ScoreValue(new LgtmScore())
        .set(value)
        .confidence(Confidence.MAX);
    ScoreValue findSecBugsScoreValue = new ScoreValue(new FindSecBugsScore())
        .makeNotApplicable()
        .confidence(Confidence.MAX);

    ScoreValue scoreValue = score.calculate(
        codeqlScoreValue, lgtmScoreValue, findSecBugsScoreValue);

    assertFalse(scoreValue.isUnknown());
    assertFalse(scoreValue.isNotApplicable());
    assertEquals(value, scoreValue.get(), DELTA);
    assertEquals(Confidence.MAX, scoreValue.confidence(), DELTA);
    assertSame(score, scoreValue.score());
    assertEquals(3, scoreValue.usedValues().size());
    assertTrue(scoreValue.usedValues().contains(lgtmScoreValue));
    assertTrue(scoreValue.usedValues().contains(findSecBugsScoreValue));
  }

  @Test
  public void testEqualsAndHashCode() {
    StaticAnalysisScore one = new StaticAnalysisScore();
    StaticAnalysisScore two = new StaticAnalysisScore();
    assertTrue(one.equals(two) && two.equals(one));
    assertEquals(one.hashCode(), two.hashCode());
  }

  @Test
  public void testSerialization() throws IOException {
    StaticAnalysisScore score = new StaticAnalysisScore();
    StaticAnalysisScore clone = Json.read(Json.toBytes(score), StaticAnalysisScore.class);
    assertEquals(score, clone);
  }

}