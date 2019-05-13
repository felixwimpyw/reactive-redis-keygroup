package com.wimpy.processor;

import java.util.List;

public interface StringProcessor {

  String writeAsString(Object object);

  <T> T readValue(String content, Class<T> valueType);

  <T> List<T> readListValue(String content, Class<T> valueType);

}
