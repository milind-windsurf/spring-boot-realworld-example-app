package io.spring.application;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DateTimeCursor extends PageCursor<LocalDateTime> {

  public DateTimeCursor(LocalDateTime data) {
    super(data);
  }

  @Override
  public String toString() {
    return String.valueOf(getData().toInstant(ZoneOffset.UTC).toEpochMilli());
  }

  public static LocalDateTime parse(String cursor) {
    if (cursor == null) {
      return null;
    }
    return LocalDateTime.ofEpochSecond(Long.parseLong(cursor) / 1000, (int) (Long.parseLong(cursor) % 1000) * 1000000, ZoneOffset.UTC);
  }
}
