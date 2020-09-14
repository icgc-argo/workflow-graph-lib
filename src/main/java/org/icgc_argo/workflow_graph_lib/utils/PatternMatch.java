package org.icgc_argo.workflow_graph_lib.utils;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PatternMatch {

  public static <T, R> Match<T, R> match(T x) {
    return new Match<>() {
      @Override
      public Match<T, R> on(Predicate<T> pred, Function<T, R> fn) {
        return pred.test(x) ? matched(fn.apply(x)) : match(x);
      }

      @Override
      public Match<T, R> on(Predicate<T> pred, Supplier<R> fn) {
        return pred.test(x) ? matched(fn.get()) : match(x);
      }

      @Override
      public R otherwise(Function<T, R> fn) {
        return fn.apply(x);
      }

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

  public interface Match<T, R> {
    Match<T, R> on(Predicate<T> pred, Function<T, R> fn);

    Match<T, R> on(Predicate<T> pred, Supplier<R> fn);

    R otherwise(Function<T, R> fn);

    R otherwise(Supplier<R> fn);
  }
}
