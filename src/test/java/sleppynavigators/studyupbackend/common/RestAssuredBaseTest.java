package sleppynavigators.studyupbackend.common;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;

public class RestAssuredBaseTest extends ApplicationBaseTest {

    protected Validator validator;

    @LocalServerPort
    private int port;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();

        // Initialize the validator using the default factory
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        // Set up RestAssured with the local server port and JSON content type
        RestAssured.reset();
        RestAssured.port = port;
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }
}
