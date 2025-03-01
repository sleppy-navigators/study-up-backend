package sleppynavigators.studyupbackend.infrastructure.common.attribute.converter;

import java.net.URL;
import java.util.List;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class UrlConverter implements AttributeConverter<List<URL>, String> {

    private static final String DELIMITER = "\n";

    @Override
    public String convertToDatabaseColumn(List<URL> attributes) {
        return attributes != null ?
                String.join(DELIMITER, attributes.stream().map(URL::toString).toList())
                : null;
    }

    @Override
    public List<URL> convertToEntityAttribute(String dbData) {
        return dbData != null ?
                Stream.of(dbData.split(DELIMITER))
                        .map(this::convertToURL)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList()
                : null;
    }

    private Optional<URL> convertToURL(String url) {
        try {
            return Optional.of(new URL(url));
        } catch (Exception e) {
            log.error("Can not convert to URL, {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
}
