package org.icgc_argo.workflow_graph_lib.polyglot;

import static java.lang.String.format;
import static org.icgc_argo.workflow_graph_lib.utils.JacksonUtils.toMap;

import java.util.Map;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.icgc_argo.workflow_graph_lib.polyglot.enums.GraphFunctionLanguage;
import org.icgc_argo.workflow_graph_lib.utils.PatternMatch;

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
   *     the user defined function, accepts both Java Map and String, the latter of which is
   *     transformed into a Map before being marshalled into a NestedProxyObject
   * @return generic value type returned by user function
   */
  public static Value runMainFunctionWithData(
      final GraphFunctionLanguage language,
      final String scriptContent,
      final Map<String, Object> data) {
    return PatternMatch.<GraphFunctionLanguage, Value>match(language)
        .on(
            lang -> lang.equals(GraphFunctionLanguage.JS),
            () -> runJsScript(format("function main(data) { %s }", scriptContent), data))
        .on(
            lang -> lang.equals(GraphFunctionLanguage.PYTHON),
            () -> runPythonScript(format("def main(data):\n    %s", scriptContent), data))
        .otherwise(
            () -> {
              throw new UnsupportedOperationException(
                  format("Operation %s is not supported", language));
            });
  }

  public static Value runMainFunctionWithData(
      final GraphFunctionLanguage language, final String scriptContent, final String data) {
    return runMainFunctionWithData(language, scriptContent, toMap(data));
  }

  protected static org.graalvm.polyglot.Value runJsScript(
      final String jsScript, final Map<String, Object> eventMap) {
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
      final Map<String, Object> eventMap) {
    NestedProxyObject eventMapProxy = new NestedProxyObject(eventMap);
    final Source source = Source.newBuilder(language, script, scriptFileName).buildLiteral();
    ctx.eval(source);
    return ctx.getBindings(languageId).getMember("main").execute(eventMapProxy);
  }
}
