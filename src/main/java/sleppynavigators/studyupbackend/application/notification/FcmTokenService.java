package sleppynavigators.studyupbackend.application.notification;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.notification.FcmToken;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.notification.FcmTokenRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.notification.dto.request.FcmTokenRequest;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
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
    public void deleteTokens(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        fcmTokenRepository.deleteAllByUserId(user.getId());
    }

    @Transactional
    public void deleteTokensByUserIdAndDeviceId(Long userId, String deviceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        fcmTokenRepository.deleteByDeviceIdAndUserId(deviceId, user.getId());
    }
}
