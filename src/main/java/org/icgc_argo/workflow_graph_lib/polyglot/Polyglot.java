package org.icgc_argo.workflow_graph_lib.polyglot;

import lombok.val;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.icgc_argo.workflow_graph_lib.polyglot.enums.GraphFunctionLanguage;
import org.icgc_argo.workflow_graph_lib.polyglot.exceptions.GraphFunctionException;
import org.icgc_argo.workflow_graph_lib.polyglot.exceptions.GraphFunctionUnsupportedLanguageException;
import org.icgc_argo.workflow_graph_lib.polyglot.exceptions.GraphFunctionValueException;
import org.icgc_argo.workflow_graph_lib.utils.PatternMatch;

import java.util.Map;

import static java.lang.String.format;
import static org.icgc_argo.workflow_graph_lib.polyglot.enums.GraphFunctionLanguage.JS;
import static org.icgc_argo.workflow_graph_lib.polyglot.enums.GraphFunctionLanguage.PYTHON;
import static org.icgc_argo.workflow_graph_lib.utils.JacksonUtils.toMap;

/**
 * Provides a single static context for all GraalVM Polyglot function executions as well as generic
 * static functions that are used to execute code in that GraalVM Polyglot context
 */
public class Polyglot {
  protected static final Context ctx = Context.newBuilder("python", "js").build();

  /**
   * Runs a user defined function using the language specified with, a single argument that is
   * marshalled into a NestedProxyObject.
   *
   * @param language polyglot language context to be used to execute scriptContent
   * @param scriptContent user defined script to be executed in chosen language
   * @param data object/dictionary/map-like data structure to be passed as the single argument for
   *     the user defined function
   * @return generic value type returned by user function
   */
  @SuppressWarnings("unchecked")
  public static Map<String, Object> runMainFunctionWithData(
      final GraphFunctionLanguage language,
      final String scriptContent,
      final Map<String, Object> data) {
    try {
      val returnValue =
          PatternMatch.<GraphFunctionLanguage, Value>match(language)
              .on(
                  lang -> lang.equals(GraphFunctionLanguage.JS),
                  () -> runJsScript(format("function main(data) { %s }", scriptContent), data))
              .on(
                  lang -> lang.equals(GraphFunctionLanguage.PYTHON),
                  () -> runPythonScript(format("def main(data):\n    %s", scriptContent), data))
              .otherwise(
                  () -> {
                    throw new GraphFunctionUnsupportedLanguageException(
                        format("Operation %s is not supported", language));
                  });

      return (Map<String, Object>) returnValue.as(Map.class);
    } catch (PolyglotException ex) {
      throw new GraphFunctionException(ex.getLocalizedMessage());
    } catch (IllegalStateException | ClassCastException ex) {
      throw new GraphFunctionValueException(
          format(
              "Unable to convert returned ProxyObject to Map<String, Object>: %s",
              ex.getLocalizedMessage()));
    }
  }

  /**
   * Runs a user defined function using the language specified with, a single argument that is
   * marshalled into a NestedProxyObject.
   *
   * @param language polyglot language context to be used to execute scriptContent
   * @param scriptContent user defined script to be executed in chosen language
   * @param data string input that can be converted to a map using utils.JacksonUtils.toMap, will be
   *     passed as the single argument for the user defined function
   * @return generic value type returned by user function
   */
  public static Map<String, Object> runMainFunctionWithData(
      final GraphFunctionLanguage language, final String scriptContent, final String data) {
    return runMainFunctionWithData(language, scriptContent, toMap(data));
  }

  /**
   * Evaluates a user defined boolean expression using the language specified, has access to data
   * passed in, which is marshalled into a NestedProxyObject
   *
   * @param language polyglot language context to be used to evaluate expression
   * @param expression ser defined expression to be evaluated in chosen language
   * @param data object/dictionary/map-like data structure to be made available as language native
   *     data object (object in JS, dictionary in python) inside expression context
   * @return result of evaluate boolean expression
   */
  public static Boolean evaluateBooleanExpression(
      final GraphFunctionLanguage language,
      final String expression,
      final Map<String, Object> data) {
    try {
      val returnValue =
          PatternMatch.<GraphFunctionLanguage, Value>match(language)
              .on(
                  lang -> lang.equals(JS),
                  () -> runJsScript(format("function main(data) { return %s; }", expression), data))
              .on(
                  lang -> lang.equals(PYTHON),
                  () -> runPythonScript(format("def main(data):\n    return %s", expression), data))
              .otherwise(
                  () -> {
                    throw new GraphFunctionValueException(
                        format("Operation %s is not supported", language));
                  });

      if (!returnValue.isBoolean()) {
        throw new GraphFunctionValueException("Return value must be of boolean type");
      }

      return returnValue.asBoolean();
    } catch (PolyglotException ex) {
      throw new GraphFunctionException(ex.getLocalizedMessage());
    }
  }

  /**
   * Evaluates a user defined boolean expression using the language specified, has access to data
   * passed in, which is marshalled into a NestedProxyObject
   *
   * @param language polyglot language context to be used to evaluate expression
   * @param expression ser defined expression to be evaluated in chosen language
   * @param data string input that can be converted to a map using utils.JacksonUtils.toMap, will be
   *     made available as language native data object (object in JS, dictionary in python) inside
   *     expression context
   * @return result of evaluate boolean expression
   */
  public static Boolean evaluateBooleanExpression(
      final GraphFunctionLanguage language, final String expression, final String data) {
    return evaluateBooleanExpression(language, expression, toMap(data));
  }

  protected static Value runJsScript(final String jsScript, final Map<String, Object> eventMap) {
    return runFunctionMain("js", "js", "script.js", jsScript, eventMap);
  }

  protected static Value runPythonScript(
      final String pythonScript, final Map<String, Object> eventMap) {
    return runFunctionMain("python", "python", "script.py", pythonScript, eventMap);
  }

  protected static Value runFunctionMain(
      final String language,
      final String languageId,
      final String scriptFileName,
      final String script,
      final Map<String, Object> data) {
    NestedProxyObject eventMapProxy = new NestedProxyObject(data);
    final Source source = Source.newBuilder(language, script, scriptFileName).buildLiteral();
    ctx.eval(source);
    return ctx.getBindings(languageId).getMember("main").execute(eventMapProxy);
  }
}
