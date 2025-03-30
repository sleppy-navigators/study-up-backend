package sleppynavigators.studyupbackend.domain.event;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.chat.SystemMessageTemplate;

@DisplayName("이벤트 시스템 테스트")
class SystemEventTest {

    @Test
    @DisplayName("UserJoinEvent의 메시지가 올바르게 생성된다")
    void userJoinEvent_GeneratesCorrectMessage() {
        // given
        UserJoinEvent event = new UserJoinEvent("testUser", 1L);
        
        // when
        String message = event.generateMessage(SystemMessageTemplate.USER_JOIN_MESSAGE_TEMPLATE);
        
        // then
        assertThat(message).isEqualTo("testUser님이 그룹에 참여했습니다.");
    }

    @Test
    @DisplayName("UserLeaveEvent의 메시지가 올바르게 생성된다")
    void userLeaveEvent_GeneratesCorrectMessage() {
        // given
        UserLeaveEvent event = new UserLeaveEvent("testUser", 1L);
        
        // when
        String message = event.generateMessage(SystemMessageTemplate.USER_LEAVE_MESSAGE_TEMPLATE);
        
        // then
        assertThat(message).isEqualTo("testUser님이 그룹을 나갔습니다.");
    }

    @Test
    @DisplayName("ChallengeCreateEvent의 메시지가 올바르게 생성된다")
    void challengeCreateEvent_GeneratesCorrectMessage() {
        // given
        ChallengeCreateEvent event = new ChallengeCreateEvent("testUser", "스터디하기", 1L);
        
        // when
        String message = event.generateMessage(SystemMessageTemplate.CHALLENGE_CREATE_MESSAGE_TEMPLATE);
        
        // then
        assertThat(message).isEqualTo("testUser님이 '스터디하기' 챌린지를 생성했습니다.");
    }

    @Test
    @DisplayName("ChallengeCompleteEvent의 메시지가 올바르게 생성된다")
    void challengeCompleteEvent_GeneratesCorrectMessage() {
        // given
        ChallengeCompleteEvent event = new ChallengeCompleteEvent("testUser", "스터디하기", 1L);
        
        // when
        String message = event.generateMessage(SystemMessageTemplate.CHALLENGE_COMPLETE_MESSAGE_TEMPLATE);
        
        // then
        assertThat(message).isEqualTo("testUser님이 '스터디하기' 챌린지를 완료했습니다.");
    }

    @Test
    @DisplayName("이벤트의 groupId가 올바르게 반환된다")
    void event_ReturnsCorrectGroupId() {
        // given
        Long groupId = 1L;
        UserJoinEvent event = new UserJoinEvent("testUser", groupId);
        
        // when
        Long result = event.getGroupId();
        
        // then
        assertThat(result).isEqualTo(groupId);
    }

    @Test
    @DisplayName("이벤트의 타입이 올바르게 반환된다")
    void event_ReturnsCorrectType() {
        // given
        UserJoinEvent event = new UserJoinEvent("testUser", 1L);
        
        // when
        SystemEventType type = event.getType();
        
        // then
        assertThat(type).isEqualTo(SystemEventType.USER_JOIN);
    }
}
