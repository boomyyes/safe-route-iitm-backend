package com.saferoute.chat.controllers;

import com.saferoute.chat.dto.ChatMessageDTO;
import com.saferoute.chat.dto.ChatRequestDTO;
import com.saferoute.chat.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Send a new message to the AI for a specific session.
     * Triggers the proxy workflow to the Python microservice.
     */
    @PostMapping("/{sessionId}")
    public ResponseEntity<List<ChatMessageDTO>> processMessage(
            @PathVariable UUID sessionId,
            @RequestBody ChatRequestDTO requestDTO) {
        
        List<ChatMessageDTO> updatedHistory = chatService.processUserMessage(sessionId, requestDTO);
        return ResponseEntity.ok(updatedHistory);
    }

    /**
     * Fetch the conversation history for a specific session.
     */
    @GetMapping("/{sessionId}")
    public ResponseEntity<List<ChatMessageDTO>> getChatHistory(@PathVariable UUID sessionId) {
        List<ChatMessageDTO> history = chatService.getChatHistory(sessionId);
        return ResponseEntity.ok(history);
    }
}
