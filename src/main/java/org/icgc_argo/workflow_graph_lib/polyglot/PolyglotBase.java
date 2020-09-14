package org.icgc_argo.workflow_graph_lib.polyglot;

import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.util.Map;

@Slf4j
public class PolyglotBase {
  private static final Context ctx = Context.newBuilder("python", "js").build();

  private static org.graalvm.polyglot.Value runJsScript(
      final String jsScript, final Map<String, Object> eventMap) {
    return runFunctionMain("js", "js", "script.js", jsScript, eventMap);
  }

  private static Value runPythonScript(
      final String pythonScript, final Map<String, Object> eventMap) {
    return runFunctionMain("python", "python", "script.py", pythonScript, eventMap);
  }

  private static Value runFunctionMain(
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
