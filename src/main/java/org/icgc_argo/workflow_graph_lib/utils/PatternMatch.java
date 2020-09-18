package org.icgc_argo.workflow_graph_lib.utils;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Functional style pattern matcher. A basic usage example is as follows: <br>
 * <br>
 * Integer result = PatternMatch.< Integer, Integer >match(50)
 *
 * <p>.on(x -> x < 0, () -> 0)
 *
 * <p>.on(x -> x >= 0 && x <= 1, x -> x
 *
 * <p>.otherwise(x -> x * 10); <br>
 * <br>
 *
 * <p>assertEquals(500, result) == true<br>
 * <br>
 *
 * <p>NOTE: Refer to PatternMatchTest class for extensive usage examples
 */
public class PatternMatch {

  /**
   * Entry point into the matcher, ex. PatternMatch< Integer, Integer >.match(50)
   *
   * @param x match subject (what we are matching on)
   * @param <T> match subject type
   * @param <R> final return value type
   * @return Match functional class that contains methods on and otherwise (each overloaded to fit
   *     the use case of this matcher)
   */
  public static <T, R> Match<T, R> match(T x) {
    return new Match<>() {
      /**
       * Matching statement, for example ".on(x -> x >= 0 && x <= 1, x -> x)" supplies a predicate
       * that is evaluated and if it return true then the fn supplied is evaluated, it's return
       * value is what will ultimately be returned from the match chain
       *
       * @param pred Predicate function that takes matcher subject as input
       * @param fn Unary function to evaluate if the pred function returned true, single parameter
       *     used to call the function is the match subject
       * @return Either another Match that does not have a successful match yet and therefore is
       *     still processing the input on further ".on" calls or a "matched" Match which will
       *     always return the result of the applied function at the point of a match on further
       *     calls to ".on" and ".otherwise"
       */
      @Override
      public Match<T, R> on(Predicate<T> pred, Function<T, R> fn) {
        return pred.test(x) ? matched(fn.apply(x)) : match(x);
      }

      /**
       * Matching statement, for example ".on(x -> x >= 0 && x <= 1, x -> x)" supplies a predicate
       * that is evaluated and if it return true then the fn supplied is evaluated, it's return
       * value is what will ultimately be returned from the match chain
       *
       * @param pred Predicate function that takes matcher subject as input
       * @param fn Supplier (no-argument) function to evaluate if the pred function returned true
       * @return Either another Match that does not have a successful match yet and therefore is
       *     still processing the input on further ".on" calls or a "matched" Match which will
       *     always return the result of the applied function at the point of a match on further
       *     calls to ".on" and ".otherwise"
       */
      @Override
      public Match<T, R> on(Predicate<T> pred, Supplier<R> fn) {
        return pred.test(x) ? matched(fn.get()) : match(x);
      }

      /**
       * Fall-through condition that executes in the event that there is no match in the match chain
       *
       * @param fn Unary function to evaluate, single parameter used to call the function is the
       *     match subject
       * @return Result of the supplied function
       */
      @Override
      public R otherwise(Function<T, R> fn) {
        return fn.apply(x);
      }

      /**
       * Fall-through condition that executes in the event that there is no match in the match chain
       *
       * @param fn Supplier (no-argument) function to evaluate
       * @return Result of the supplied function
       */
      @Override
      public R otherwise(Supplier<R> fn) {
        return fn.get();
      }
    };
  }

  private static <T, R> Match<T, R> matched(R x) {
    return new Match<>() {
      @Override
      public Match<T, R> on(Predicate<T> pred, Function<T, R> fn) {
        return matched(x);
      }

      @Override
      public Match<T, R> on(Predicate<T> pred, Supplier<R> fn) {
        return matched(x);
      }

      @Override
      public R otherwise(Function<T, R> fn) {
        return x;
      }

      @Override
      public R otherwise(Supplier<R> fn) {
        return x;
      }
    };
  }

  /**
   * Match functional interface provides uniform structure for both match and matched cases in a
   * pattern matching chain
   *
   * @param <T> Match subject type
   * @param <R> Final chain return type
   */
  public interface Match<T, R> {
    Match<T, R> on(Predicate<T> pred, Function<T, R> fn);

    Match<T, R> on(Predicate<T> pred, Supplier<R> fn);

    R otherwise(Function<T, R> fn);

    R otherwise(Supplier<R> fn);
  }
}
