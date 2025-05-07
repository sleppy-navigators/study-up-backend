package sleppynavigators.studyupbackend.infrastructure.common.jpa.attribute.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[인프라] URL Converter 테스트")
class UrlConverterTest {

    @Test
    @DisplayName("URL 목록을 DB 컬럼으로 변환 - 성공")
    void convertURLsToDatabaseColumn() throws MalformedURLException {
        // given
        UrlConverter converter = new UrlConverter();
        List<URL> urls = List.of(
                new URL("https://example.com/1"),
                new URL("https://example.com/2"),
                new URL("https://example.com/3"));

        // when
        String dbData = converter.convertToDatabaseColumn(urls);

        // then
        assertThat(dbData).isEqualTo("https://example.com/1\nhttps://example.com/2\nhttps://example.com/3");
    }

    @Test
    @DisplayName("null URL 목록을 DB 컬럼으로 변환 - 성공")
    void convertNullURLsToDatabaseColumn() {
        // given
        UrlConverter converter = new UrlConverter();
        List<URL> urls = null;

        // when
        String dbData = converter.convertToDatabaseColumn(urls);

        // then
        assertThat(dbData).isEqualTo("");
    }

    @Test
    @DisplayName("DB 컬럼을 URL 목록으로 변환 - 성공")
    void convertDatabaseColumnToURLs() throws MalformedURLException {
        // given
        UrlConverter converter = new UrlConverter();
        String dbData = "https://example.com/1\nhttps://example.com/2\nhttps://example.com/3";

        // when
        List<URL> urls = converter.convertToEntityAttribute(dbData);

        // then
        assertThat(urls).containsExactly(
                new URL("https://example.com/1"),
                new URL("https://example.com/2"),
                new URL("https://example.com/3"));
    }

    @Test
    @DisplayName("null DB 컬럼을 URL 목록으로 변환 - 성공")
    void convertNullDatabaseColumnToURLs() {
        // given
        UrlConverter converter = new UrlConverter();
        String dbData = null;

        // when
        List<URL> urls = converter.convertToEntityAttribute(dbData);

        // then
        assertThat(urls).isEmpty();
    }

    @Test
    @DisplayName("빈 문자열 DB 컬럼을 URL 목록으로 변환 - 성공")
    void convertEmptyDatabaseColumnToURLs() {
        // given
        UrlConverter converter = new UrlConverter();
        String dbData = "";

        // when
        List<URL> urls = converter.convertToEntityAttribute(dbData);

        // then
        assertThat(urls).isEmpty();
    }

    @Test
    @DisplayName("잘못된 URL이 포함된 DB 컬럼을 URL 목록으로 변환 - 성공")
    void convertInvalidDatabaseColumnToURLs() throws MalformedURLException {
        // given
        UrlConverter converter = new UrlConverter();
        String dbData = "https://example.com/1\ninvalid-url\nhttps://example.com/3";

        // when
        List<URL> urls = converter.convertToEntityAttribute(dbData);

        // then
        assertThat(urls).containsExactly(
                new URL("https://example.com/1"),
                new URL("https://example.com/3"));
    }
}
