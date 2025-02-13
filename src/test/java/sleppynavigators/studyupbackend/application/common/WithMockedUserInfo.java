package sleppynavigators.studyupbackend.application.common;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockUserAuthenticationFactory.class, setupBefore = TestExecutionEvent.TEST_EXECUTION)
public @interface WithMockedUserInfo {

    long userId() default 1L;

    String username() default "guest";

    String email() default "example@guest.com";
}
