package sleppynavigators.studyupbackend.application.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class UserService {
    private final UserRepository userRepository;

    public User sampleUser() {
        return userRepository.findById(1L).orElseGet(() -> new User("sample"));
    }
}
