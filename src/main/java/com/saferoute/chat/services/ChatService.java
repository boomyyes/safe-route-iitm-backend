package com.saferoute.chat.services;

import com.saferoute.chat.dto.ChatMessageDTO;
import com.saferoute.chat.dto.ChatRequestDTO;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    
    List<ChatMessageDTO> getChatHistory(UUID sessionId);
    
    List<ChatMessageDTO> processUserMessage(UUID sessionId, ChatRequestDTO requestDTO);
}
