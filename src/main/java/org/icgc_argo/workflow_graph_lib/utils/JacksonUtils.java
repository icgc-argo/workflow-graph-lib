package org.icgc_argo.workflow_graph_lib.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.util.Map;

/** Utility functions for Jackson for common operations */
public class JacksonUtils {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /**
   * Wrapper over OBJECT_MAPPER.writeValueAsString using class supplied static ObjectMapper
   *
   * @param o any
   * @return result of OBJECT_MAPPER.writeValueAsString(o)
   */
  @SneakyThrows
  public static String toJsonString(Object o) {
    return OBJECT_MAPPER.writeValueAsString(o);
  }

  /**
   * Wrapper over OBJECT_MAPPER.convertValue using class supplied static ObjectMapper
   *
   * @param fromValue Convert target object
   * @param toValueType Desired conversion output class
   * @param <T> Return value of method is of same type as toValueType
   * @return Result of OBJECT_MAPPER.convertValue(fromValue, toValueType)
   */
  @SneakyThrows
  public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
    return OBJECT_MAPPER.convertValue(fromValue, toValueType);
  }

  /**
   * Wrapper over OBJECT_MAPPER.readValue using class supplied static ObjectMapper
   *
   * @param jsonString Input JSON string to convert to Map
   * @return Result of OBJECT_MAPPER.readValue(jsonString, Map.class)
   */
  @SneakyThrows
  public static Map<String, Object> toMap(String jsonString) {
    return OBJECT_MAPPER.readValue(jsonString, Map.class);
  }
}
