package sleppynavigators.studyupbackend.common;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleaner {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MongoTemplate mongoTemplate;

    private List<String> tableNames;

    @PostConstruct
    public void init() {
        tableNames = getManagedTables();
    }

    @Transactional
    public void execute() {
        cleanRelationalDatabase();
        cleanMongoDatabase();
    }

    private void cleanRelationalDatabase() {
        entityManager.flush();
        entityManager.clear();

        entityManager.createNativeQuery("SET foreign_key_checks = 0").executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }
        entityManager.createNativeQuery("SET foreign_key_checks = 1").executeUpdate();
    }

    private void cleanMongoDatabase() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            mongoTemplate.dropCollection(collectionName);
        }
    }

    private List<String> getManagedTables() {
        Metamodel metamodel = entityManager.getMetamodel();
        return metamodel.getEntities().stream()
                .filter(this::isManagedTable)
                .map(EntityType::getName)
                .toList();
    }

    private boolean isManagedTable(EntityType<?> type) {
        return type.getJavaType().getAnnotation(Entity.class) != null;
    }
}
