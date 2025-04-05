package sleppynavigators.studyupbackend.infrastructure.common.jpa.attribute.converter;

import ch.qos.logback.core.util.StringUtil;
import java.net.URL;
import java.util.ArrayList;
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
                : "";
    }

    @Override
    public List<URL> convertToEntityAttribute(String dbData) {
        return !StringUtil.isNullOrEmpty(dbData) ?
                Stream.of(dbData.split(DELIMITER))
                        .map(this::convertToURL)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList()
                : new ArrayList<>();
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
