package sleppynavigators.studyupbackend.common.support;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.application.notification.FcmNotificationService;
import sleppynavigators.studyupbackend.application.notification.FcmTokenService;
import sleppynavigators.studyupbackend.domain.notification.FcmToken;
import sleppynavigators.studyupbackend.domain.notification.FcmToken.DeviceType;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.infrastructure.notification.FcmClient;
import sleppynavigators.studyupbackend.infrastructure.notification.FcmTokenRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.notification.dto.request.FcmTokenRequest;
import sleppynavigators.studyupbackend.presentation.notification.dto.request.TestNotificationRequest;

@Transactional
@Component
public class FcmSupport {

    @Autowired
    private FcmTokenService fcmTokenService;

    @Autowired
    private FcmNotificationService fcmNotificationService;

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public FcmToken registerFcmTokenToDB(User user, String token, String deviceId, DeviceType deviceType) {
        User foundUser = userRepository.findById(user.getId()).orElseThrow();
        FcmToken fcmToken = new FcmToken(token, deviceId, deviceType, foundUser);
        return fcmTokenRepository.save(fcmToken);
    }

    public List<FcmToken> registerMultipleFcmTokensToDB(User user, int count) {
        User foundUser = userRepository.findById(user.getId()).orElseThrow();
        return List.of(
                new FcmToken("token1", "device1", DeviceType.ANDROID, foundUser),
                new FcmToken("token2", "device2", DeviceType.IOS, foundUser),
                new FcmToken("token3", "device3", DeviceType.WEB, foundUser)
        ).subList(0, Math.min(count, 3)).stream()
                .map(fcmTokenRepository::save)
                .toList();
    }
} 