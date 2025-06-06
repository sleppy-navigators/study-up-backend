package sleppynavigators.studyupbackend.application.notification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.firebase.messaging.BatchResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sleppynavigators.studyupbackend.common.ApplicationBaseTest;
import sleppynavigators.studyupbackend.common.support.FcmSupport;
import sleppynavigators.studyupbackend.common.support.GroupSupport;
import sleppynavigators.studyupbackend.common.support.UserSupport;
import sleppynavigators.studyupbackend.domain.event.challenge.ChallengeCompleteEvent;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.notification.FcmToken.DeviceType;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.infrastructure.notification.FcmClient;

@DisplayName("FcmNotificationService 통합 테스트")
class FcmNotificationServiceTest extends ApplicationBaseTest {

    @Autowired
    private FcmNotificationService fcmNotificationService;

    @MockitoBean
    private FcmClient fcmClient;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private GroupSupport groupSupport;

    @Autowired
    private FcmSupport fcmSupport;

    private User testUser1;
    private User testUser2;
    private Group testGroup;
    private BatchResponse mockBatchResponse;

    @BeforeEach
    void setUp() {
        // given
        testUser1 = userSupport.registerUserToDB("user1", "user1@test.com");
        testUser2 = userSupport.registerUserToDB("user2", "user2@test.com");
        testGroup = groupSupport.registerGroupToDB(List.of(testUser1, testUser2));

        mockBatchResponse = org.mockito.Mockito.mock(BatchResponse.class);
        when(mockBatchResponse.getSuccessCount()).thenReturn(1);
        when(mockBatchResponse.getFailureCount()).thenReturn(0);
    }

    @Test
    @DisplayName("그룹 알림 이벤트를 처리하여 모든 그룹 멤버에게 배치 전송한다")
    void sendNotification_GroupEvent_SendsBatchToAllMembers() {
        // given
        fcmSupport.registerFcmTokenToDB(testUser1, "token1", "device1", DeviceType.ANDROID);
        fcmSupport.registerFcmTokenToDB(testUser1, "token2", "device2", DeviceType.IOS);
        fcmSupport.registerFcmTokenToDB(testUser2, "token3", "device3", DeviceType.WEB);

        ChallengeCompleteEvent event = new ChallengeCompleteEvent(
                "testUser", "testChallenge", testGroup.getId(), 1L, testUser1.getId(), 85.5);

        when(fcmClient.sendMulticast(any(), anyList()))
                .thenReturn(mockBatchResponse);

        // when
        fcmNotificationService.sendNotification(event);

        // then
        verify(fcmClient, times(1)).sendMulticast(any(), anyList());
    }
}
