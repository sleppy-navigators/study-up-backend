package sleppynavigators.studyupbackend.infrastructure.common.attribute.converter;

import java.util.List;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StringConverter implements AttributeConverter<List<String>, String> {

    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(List<String> attributes) {
        return attributes != null ? String.join(DELIMITER, attributes) : null;
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return dbData != null ? List.of(dbData.split(DELIMITER)) : List.of();
    }
}
