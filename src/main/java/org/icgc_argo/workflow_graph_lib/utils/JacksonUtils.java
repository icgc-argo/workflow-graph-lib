package org.icgc_argo.workflow_graph_lib.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.util.Map;

public class JacksonUtils {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @SneakyThrows
  public static String toJsonString(Object o) {
    return OBJECT_MAPPER.writeValueAsString(o);
  }

  @SneakyThrows
  public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
    return OBJECT_MAPPER.convertValue(fromValue, toValueType);
  }

  @SneakyThrows
  public static Map<String, Object> toMap(String jsonString) {
    return OBJECT_MAPPER.readValue(jsonString, Map.class);
  }
}
