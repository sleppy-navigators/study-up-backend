package sleppynavigators.studyupbackend.presentation.chat.dto.response;

import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;

import java.util.List;

public record ChatMessageListResponse(
        @NotNull @Valid List<ChatMessageDto> messages,
        int currentPage,
        int totalPages,
        long totalElements
) {
    public static ChatMessageListResponse from(Page<ChatMessage> messagePage) {
        List<ChatMessageDto> messages = messagePage.getContent().stream()
                .map(ChatMessageDto::from)
                .toList();

        return new ChatMessageListResponse(
                messages,
                messagePage.getNumber(),
                messagePage.getTotalPages(),
                messagePage.getTotalElements()
        );
    }
} 
