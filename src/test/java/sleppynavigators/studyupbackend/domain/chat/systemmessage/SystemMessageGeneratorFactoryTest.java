package sleppynavigators.studyupbackend.domain.chat.systemmessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.SystemMessageEvent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemMessageGeneratorFactoryTest {

    private SystemMessageGeneratorFactory factory;
    
    @Mock
    private SystemMessageGenerator<SystemMessageEvent> generator1;
    
    @Mock
    private SystemMessageGenerator<SystemMessageEvent> generator2;

    @BeforeEach
    void setUp() {
        when(generator1.getEventType()).thenReturn(EventType.USER_JOIN);
        when(generator2.getEventType()).thenReturn(EventType.USER_LEAVE);
        
        factory = new SystemMessageGeneratorFactory(List.of(generator1, generator2));
    }

    @Test
    void generateMessage_WithRegisteredEvent_ShouldReturnMessage() {
        // given
        SystemMessageEvent event = mock(SystemMessageEvent.class);
        when(event.getType()).thenReturn(EventType.USER_JOIN);
        when(generator1.generate(event)).thenReturn("테스트 메시지");
        
        // when
        String message = factory.generateMessage(event);
        
        // then
        assertThat(message).isEqualTo("테스트 메시지");
    }
    
    @Test
    void generateMessage_WithUnregisteredEvent_ShouldThrowException() {
        // given
        SystemMessageEvent event = mock(SystemMessageEvent.class);
        when(event.getType()).thenReturn(EventType.CHALLENGE_CREATE);
        
        // when & then
        assertThrows(IllegalArgumentException.class, () -> factory.generateMessage(event));
    }
}
