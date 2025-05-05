package sleppynavigators.studyupbackend.domain.notification;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sleppynavigators.studyupbackend.domain.common.TimeAuditBaseEntity;
import sleppynavigators.studyupbackend.domain.user.User;

@Entity
@Table(name = "fcm_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmToken extends TimeAuditBaseEntity {

    @Column(nullable = false)
    private String token;

    @Column(nullable = false, unique = true)
    private String deviceId;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public FcmToken(String token, String deviceId, DeviceType deviceType, User user) {
        this.token = token;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.user = user;
    }

    public void updateToken(String token) {
        this.token = token;
    }

    public enum DeviceType {
        ANDROID, IOS, WEB
    }
}
