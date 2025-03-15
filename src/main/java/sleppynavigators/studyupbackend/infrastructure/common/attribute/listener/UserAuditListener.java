package sleppynavigators.studyupbackend.infrastructure.common.attribute.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import sleppynavigators.studyupbackend.domain.common.UserAuditAttribute;
import sleppynavigators.studyupbackend.infrastructure.common.util.ReflectionField;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserPrincipal;

public class UserAuditListener {

    private static final Long SYSTEM_USER_ID = 0L;

    @PrePersist
    public void setCreatedBy(Object entity) {
        ReflectionField<UserAuditAttribute> userAuditAttribute =
                new ReflectionField<>(entity, UserAuditAttribute.class);
        userAuditAttribute.set(new UserAuditAttribute(getCurrentUserId(), null));
    }

    @PreUpdate
    public void setUpdatedBy(Object entity) {
        ReflectionField<UserAuditAttribute> userAuditAttribute =
                new ReflectionField<>(entity, UserAuditAttribute.class);
        UserAuditAttribute updatedAttribute =
                new UserAuditAttribute(userAuditAttribute.get().createdBy(), getCurrentUserId());
        userAuditAttribute.set(updatedAttribute);
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
