package sleppynavigators.studyupbackend.domain.common;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserPrincipal;

public class UserAuditorListener {

    private static final Long SYSTEM_USER_ID = 0L;

    @PrePersist
    public void setCreatedBy(Object entity) {
        try {
            Field userAuditAttributeField = getUserAuditAttributeField(entity);
            UserAuditAttribute createdUserAuditAttribute = new UserAuditAttribute(getCurrentUserId(), null);
            userAuditAttributeField.set(entity, createdUserAuditAttribute);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    @PreUpdate
    public void setUpdatedBy(Object entity) {
        try {
            Field userAuditAttributeField = getUserAuditAttributeField(entity);
            UserAuditAttribute userAuditAttribute = (UserAuditAttribute) userAuditAttributeField.get(entity);
            UserAuditAttribute updatedUserAuditAttribute =
                    new UserAuditAttribute(userAuditAttribute.createdBy(), getCurrentUserId());
            userAuditAttributeField.set(entity, updatedUserAuditAttribute);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Field getUserAuditAttributeField(Object entity) {
        Field userAuditAttribute = Stream.of(entity.getClass().getDeclaredFields())
                .filter(field -> field.getType().equals(UserAuditAttribute.class))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        userAuditAttribute.setAccessible(true);
        return userAuditAttribute;
    }

    private Long getCurrentUserId() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(UserPrincipal.class::cast)
                .map(UserPrincipal::userId)
                .orElse(SYSTEM_USER_ID);
    }
}
