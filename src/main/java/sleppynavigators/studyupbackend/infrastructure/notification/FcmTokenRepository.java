package sleppynavigators.studyupbackend.infrastructure.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import sleppynavigators.studyupbackend.domain.notification.FcmToken;
import sleppynavigators.studyupbackend.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    List<FcmToken> findAllByUser(User user);
    Optional<FcmToken> findByDeviceId(String deviceId);
}
