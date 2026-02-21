package com.ehtisham.splitsync.infrastructure.out.persistence.util;

import org.postgresql.util.PGobject;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public final class JdbcUtils {

    private JdbcUtils() {
        // Utility class.
    }

    public static Timestamp toTimestamp(LocalDateTime value) {
        return value != null ? Timestamp.valueOf(value) : null;
    }

    public static LocalDateTime toLocalDateTime(Timestamp value) {
        return value != null ? value.toLocalDateTime() : null;
    }

    public static PGobject toJsonbObject(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            PGobject jsonbObject = new PGobject();
            jsonbObject.setType("jsonb");
            jsonbObject.setValue(value);
            return jsonbObject;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to bind jsonb value", e);
        }
    }
}
