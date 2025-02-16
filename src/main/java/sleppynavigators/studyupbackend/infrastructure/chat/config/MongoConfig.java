package sleppynavigators.studyupbackend.infrastructure.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "sleppynavigators.studyupbackend.infrastructure.chat")
public class MongoConfig {
} 