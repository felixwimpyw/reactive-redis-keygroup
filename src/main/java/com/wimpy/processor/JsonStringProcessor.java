package com.wimpy.processor;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.wimpy.redis.reactive.ReactiveCacheKeygroupServiceImpl;

public class JsonStringProcessor implements StringProcessor {

  private static final Logger log = LoggerFactory.getLogger(ReactiveCacheKeygroupServiceImpl.class);

  private ObjectMapper objectMapper;

  @Override
  public <T> T readValue(String content, Class<T> valueType) {
    try {
      return objectMapper.readValue(content, valueType);
    } catch (IOException e) {
      log.error("#readValue ", e);
      return null;
    }
  }

  @Override
  public <T> List<T> readListValue(String content, Class<T> valueType) {
    try {
      CollectionType collectionType =
          objectMapper.getTypeFactory().constructCollectionType(List.class, valueType);
      return objectMapper.readValue(content, collectionType);
    } catch (IOException e) {
      log.error("#readListValue ", e);
      return null;
    }
  }

  @Override
  public String writeAsString(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.error("#readListValue ", e);
      return null;
    }
  }

}
