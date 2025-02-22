package sleppynavigators.studyupbackend.presentation.common;

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

    private List<String> tableNames;

    @PostConstruct
    public void init() {
        tableNames = getManagedTables();
    }

    @Transactional
    public void execute() {
        cleanRelationalDatabase();
        // TODO(@Jayon): MongoDB 데이터 삭제 로직 추가
    }

    private void cleanRelationalDatabase() {
        entityManager.flush();
        entityManager.clear();

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
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
