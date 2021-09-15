package org.icgc_argo.workflow_graph_lib.polyglot;

import static org.icgc_argo.workflow_graph_lib.polyglot.Polyglot.evaluateBooleanExpression;
import static org.icgc_argo.workflow_graph_lib.polyglot.Polyglot.runMainFunctionWithData;
import static org.icgc_argo.workflow_graph_lib.polyglot.enums.GraphFunctionLanguage.JS;
import static org.icgc_argo.workflow_graph_lib.polyglot.enums.GraphFunctionLanguage.PYTHON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import lombok.val;
import org.icgc_argo.workflow_graph_lib.exceptions.CommittableException;
import org.icgc_argo.workflow_graph_lib.exceptions.NotAcknowledgeableException;
import org.icgc_argo.workflow_graph_lib.polyglot.exceptions.GraphFunctionException;
import org.icgc_argo.workflow_graph_lib.polyglot.exceptions.GraphFunctionValueException;
import org.junit.jupiter.api.Test;

public class PolyglotTest {

  @Test
  public void testRunMainFunctionWithDataJSSuccess() {
    val actual = runMainFunctionWithData(JS, "return {...data, foo: 'bar'};", Map.of("baz", "qux"));
    val expected = Map.of("baz", "qux", "foo", "bar");

    assertEquals(expected, actual);
  }

  @Test
  public void testEvaluateBooleanExpressionJSSuccess() {
    val actual = evaluateBooleanExpression(JS, "data.baz == 'qux'", Map.of("baz", "qux"));

    assertEquals(true, actual);
  }

  @Test
  public void testDataAsJSONString() {
    val mapActual =
        runMainFunctionWithData(JS, "return {...data, foo: 'bar'};", "{\"baz\": \"qux\"}");
    val mapExpected = Map.of("baz", "qux", "foo", "bar");

    val booleanActual = evaluateBooleanExpression(JS, "data.baz == 'qux'", "{\"baz\": \"qux\"}");

    assertEquals(mapExpected, mapActual);
    assertEquals(true, booleanActual);
  }

  @Test
  public void testRunMainFunctionWithDataJSExceptionOnWrongReturnType() {
    assertThrows(
        GraphFunctionValueException.class,
        () -> runMainFunctionWithData(JS, "return 'no bueno';", Map.of("baz", "qux")));
  }

  @Test
  public void testEvaluateBooleanExpressionJSExceptionOnWrongReturnType() {
    assertThrows(
        GraphFunctionValueException.class,
        () -> evaluateBooleanExpression(JS, "'true'", Map.of("baz", "qux")));
  }

  @Test
  public void testRunMainFunctionWithDataJSExceptionOnCodeError() {
    assertThrows(
        GraphFunctionException.class,
        () -> runMainFunctionWithData(JS, "return {bad: code);", Map.of("baz", "qux")));
  }

  @Test
  public void testEvaluateBooleanExpressionJSExceptionOnCodeError() {
    assertThrows(
        GraphFunctionException.class,
        () -> evaluateBooleanExpression(JS, "return {bad: code);", Map.of("baz", "qux")));
  }

  @Test
  public void testBuiltInRejectThrowsCommittableException() {
    assertThrows(
        CommittableException.class,
        () -> runMainFunctionWithData(JS, "return reject(\"Testing REJECT\")", Map.of()));

    assertThrows(
        CommittableException.class,
        () -> runMainFunctionWithData(PYTHON, "return reject(\"Testing REJECT\")", Map.of()));
  }

  @Test
  public void testBuiltInRequeueThrowsNotAcknowledgeableException() {
    assertThrows(
        NotAcknowledgeableException.class,
        () -> runMainFunctionWithData(JS, "return requeue(\"Testing REQUEUE\")", Map.of()));

    assertThrows(
        NotAcknowledgeableException.class,
        () -> runMainFunctionWithData(PYTHON, "return requeue(\"Testing REQUEUE\")", Map.of()));
  }
}
