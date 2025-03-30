package sleppynavigators.studyupbackend.common.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;

@Transactional
@Component
public class UserSupport {

    @Autowired
    private UserRepository userRepository;

    public User registerUser() {
        User user = new User("test-user", "test-email");
        return userRepository.save(user);
    }
}
