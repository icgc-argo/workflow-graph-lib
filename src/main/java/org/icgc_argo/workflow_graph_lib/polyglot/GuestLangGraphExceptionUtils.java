package org.icgc_argo.workflow_graph_lib.polyglot;

import static java.lang.String.format;

import java.util.Map;
import java.util.Objects;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.graalvm.polyglot.Source;
import org.icgc_argo.workflow_graph_lib.exceptions.CommittableException;
import org.icgc_argo.workflow_graph_lib.exceptions.GraphException;
import org.icgc_argo.workflow_graph_lib.exceptions.NotAcknowledgeableException;

/**
 * Collection of helper functions used by Polyglot to set up guest language functions to create
 * GraphException objects from a guest language.
 *
 * <p>
 *
 * <p>Within the guest language, a GraphException is simply represented as an object/map:
 *
 * <p>{"graphExceptionType": $graphExceptionType, "message": $message }
 */
@Slf4j
@UtilityClass
public class GuestLangGraphExceptionUtils {
  private static final String EXCEPTION_TYPE_KEY = "graphExceptionType";
  private static final String MESSAGE_KEY = "message";

  public static void throwErrorIfMapIsGuestLangGraphException(Map<String, Object> map)
      throws GraphException {
    if (Objects.equals(
        map.get(EXCEPTION_TYPE_KEY), GraphExceptionTypes.CommittableException.name())) {
      throw new CommittableException(map.get(MESSAGE_KEY).toString());
    } else if (Objects.equals(
        map.get(EXCEPTION_TYPE_KEY), GraphExceptionTypes.NotAcknowledgeableException.name())) {
      throw new NotAcknowledgeableException(map.get(MESSAGE_KEY).toString());
    }
  }

  @SneakyThrows
  public static Source buildJsGraphExceptionCreator(
      String functionName, GraphExceptionTypes graphExceptionType) {
    val code =
        format(
            "const %s = (message) => ({ %s: \"%s\", %s: message });",
            functionName, EXCEPTION_TYPE_KEY, graphExceptionType.name(), MESSAGE_KEY);
    return Source.newBuilder("js", code, functionName).build();
  }

  @SneakyThrows
  public static Source buildPythonGraphExceptionCreator(
      String functionName, GraphExceptionTypes graphExceptionType) {
    val code =
        format(
            "%s = lambda message : { \"%s\": \"%s\", \"%s\": message }",
            functionName, EXCEPTION_TYPE_KEY, graphExceptionType.name(), MESSAGE_KEY);
    return Source.newBuilder("python", code, functionName).build();
  }

  public enum GraphExceptionTypes {
    CommittableException,
    NotAcknowledgeableException
  }
}
