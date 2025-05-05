package sleppynavigators.studyupbackend.application.notification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.notification.FcmToken;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.notification.FcmTokenRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public FcmToken upsertToken(Long userId, String token, String deviceId, FcmToken.DeviceType deviceType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        return fcmTokenRepository.findByDeviceId(deviceId)
                .map(existingToken -> {
                    existingToken.updateToken(token);
                    return fcmTokenRepository.save(existingToken);
                })
                .orElseGet(() -> {
                    FcmToken newToken = new FcmToken(token, deviceId, deviceType, user);
                    return fcmTokenRepository.save(newToken);
                });
    }

    @Transactional
    public void deleteTokenByDeviceId(String deviceId) {
        fcmTokenRepository.findByDeviceId(deviceId)
                .ifPresent(fcmTokenRepository::delete);
    }

    @Transactional
    public void deleteAllTokensByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<FcmToken> tokens = fcmTokenRepository.findAllByUser(user);
        fcmTokenRepository.deleteAll(tokens);
    }
}
