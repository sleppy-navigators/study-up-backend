package sleppynavigators.studyupbackend.common;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;

public class RestAssuredBaseTest extends ApplicationBaseTest {

    @LocalServerPort
    private int port;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();

        // Set up RestAssured with the local server port and JSON content type
        RestAssured.reset();
        RestAssured.port = port;
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }
}
