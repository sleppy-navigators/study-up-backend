package sleppynavigators.studyupbackend.application.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.notification.FcmToken;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.notification.FcmTokenRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public FcmToken registerToken(Long userId, String token, String deviceId, FcmToken.DeviceType deviceType) {
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

    @Transactional(readOnly = true)
    public List<String> getTokenStringsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        return fcmTokenRepository.findByUser(user).stream()
                .map(FcmToken::getToken)
                .collect(Collectors.toList());
    }
}
