package sleppynavigators.studyupbackend.common.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.application.authentication.AuthService;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.authentication.dto.request.SignInRequest;

@Transactional
@Component
public class UserSupport {

    @Autowired
    private UserRepository userRepository;

    /**
     * <b>Caution!</b> This method do directly access the database. There's no consideration about side effects.
     *
     * @see AuthService#googleSignIn(SignInRequest)
     */
    public User registerUserToDB() {
        User user = new User("test-user", "test-email");
        return userRepository.save(user);
    }

    public User registerUserToDB(String username, String email) {
        User user = new User(username, email);
        return userRepository.save(user);
    }
}
