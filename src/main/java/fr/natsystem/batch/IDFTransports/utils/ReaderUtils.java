package fr.natsystem.batch.IDFTransports.utils;

import org.springframework.batch.infrastructure.item.file.transform.FieldSet;

import java.util.Optional;
import java.util.function.Function;

public class ReaderUtils {

    private ReaderUtils(){}

    public static <T> T getFieldValue(FieldSet fs, String fieldName, Function<String, T> converter) {
        return  Optional.ofNullable(fs.readString(fieldName))
                .filter(s -> !s.isBlank())
                .map(converter)
                .orElse(null);
    }
}
