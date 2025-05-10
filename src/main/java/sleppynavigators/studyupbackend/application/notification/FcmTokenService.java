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
import sleppynavigators.studyupbackend.presentation.notification.dto.FcmTokenRequest;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public FcmToken upsertToken(Long userId, FcmTokenRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        return fcmTokenRepository.findByDeviceId(request.deviceId())
                .map(existingToken -> {
                    existingToken.updateToken(request.token());
                    return fcmTokenRepository.save(existingToken);
                })
                .orElseGet(() -> {
                    FcmToken newToken = FcmToken.builder()
                            .token(request.token())
                            .deviceId(request.deviceId())
                            .deviceType(request.deviceType())
                            .user(user)
                            .build();
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
