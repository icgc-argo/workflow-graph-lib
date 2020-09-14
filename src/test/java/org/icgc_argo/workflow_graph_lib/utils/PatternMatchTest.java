package org.icgc_argo.workflow_graph_lib.utils;

import lombok.val;
import org.icgc_argo.workflow_graph_lib.polyglot.enums.EdgeFunctionLanguage;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.icgc_argo.workflow_graph_lib.polyglot.enums.EdgeFunctionLanguage.JS;
import static org.icgc_argo.workflow_graph_lib.polyglot.enums.EdgeFunctionLanguage.PYTHON;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PatternMatchTest {

  @Test
  public void testPatternNoMatch() {
    val result =
        PatternMatch.<Integer, Integer>match(50)
            .on(x -> x < 0, () -> 0)
            .on(x -> x >= 0 && x <= 1, x -> x)
            .otherwise(x -> x * 10);

    assertEquals(500, result);
  }

  @Test
  public void testPatternFirstMatch() {
    val result =
        PatternMatch.<Integer, Integer>match(0)
            .on(x -> x >= 0 && x <= 1, () -> 1)
            .on(x -> x < 0, () -> 0)
            .on(x -> x == 10, () -> 0)
            .otherwise(x -> x * 10);

    assertEquals(1, result);
  }

  @Test
  public void testPatternMidMatch() {
    val result =
        PatternMatch.<Integer, Integer>match(0)
            .on(x -> x < 0, () -> 0)
            .on(x -> x >= 0 && x <= 1, () -> 1)
            .on(x -> x == 10, () -> 0)
            .otherwise(x -> x * 10);

    assertEquals(1, result);
  }

  @Test
  public void testPatternLastMatch() {
    val result =
        PatternMatch.<Integer, Integer>match(0)
            .on(x -> x < 0, () -> 0)
            .on(x -> x > 0, () -> 0)
            .on(x -> x >= 0 && x <= 1, () -> 1)
            .otherwise(x -> x * 10);

    assertEquals(1, result);
  }

  @Test
  public void testPatternDoubleMatch() {
    val result =
        PatternMatch.<Integer, Integer>match(0)
            .on(x -> x == 0, () -> 1)
            .on(x -> x >= 0 && x <= 1, () -> 0)
            .on(x -> x < 0, () -> 0)
            .on(x -> x == 10, () -> 0)
            .otherwise(x -> x * 10);

    assertEquals(1, result);
  }

  @Test
  public void testStringMatch() {
    val result =
        PatternMatch.<String, String>match("Blue")
            .on(x -> x.equals("Green"), () -> "Green")
            .on(x -> x.equals("Blue"), () -> "Blue")
            .otherwise(x -> "Red");

    assertEquals("Blue", result);
  }

  @Test
  public void testEnumToFuncCall() {
    // this test is basically the runEdgeFilterScript match
    Function<Integer, Integer> plusTwo = (Integer num) -> num + 2;
    val result =
        PatternMatch.<EdgeFunctionLanguage, Integer>match(JS)
            .on(x -> x.equals(JS), () -> plusTwo.apply(2))
            .on(x -> x.equals(PYTHON), () -> 0)
            .otherwise(() -> 0);

    assertEquals(4, result);
  }
}
