package sleppynavigators.studyupbackend.presentation.common;

import com.google.common.base.CaseFormat;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.transaction.Transactional;
import java.util.List;
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
        entityManager.flush();

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
                .map(this::toTableName)
                .toList();
    }

    private boolean isManagedTable(EntityType<?> type) {
        return type.getJavaType().getAnnotation(Entity.class) != null;
    }

    private String toTableName(EntityType<?> type) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, type.getName());
    }
}
