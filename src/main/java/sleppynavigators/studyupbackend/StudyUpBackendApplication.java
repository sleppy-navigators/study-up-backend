package sleppynavigators.studyupbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class StudyUpBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudyUpBackendApplication.class, args);
    }
}
