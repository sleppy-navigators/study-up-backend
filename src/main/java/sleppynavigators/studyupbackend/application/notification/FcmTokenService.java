package sleppynavigators.studyupbackend.application.notification;

import java.util.Optional;
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

        Optional<FcmToken> existingToken = fcmTokenRepository.findByDeviceId(request.deviceId());
        if (existingToken.isPresent()) {
            FcmToken token = existingToken.get();
            token.updateToken(request.token());
            return token;
        }

        FcmToken newToken = new FcmToken(
                request.token(),
                request.deviceId(),
                request.deviceType(),
                user
        );
        return fcmTokenRepository.save(newToken);
    }

    @Transactional
    public void deleteTokens(Long userId, String deviceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        if (deviceId != null) {
            fcmTokenRepository.deleteByDeviceIdAndUserId(deviceId, user.getId());
        } else {
            fcmTokenRepository.deleteAllByUserId(user.getId());
        }
    }
}
