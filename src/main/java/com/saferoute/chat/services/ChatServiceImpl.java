package com.saferoute.chat.services;

import com.saferoute.chat.dto.ChatMessageDTO;
import com.saferoute.chat.dto.ChatRequestDTO;
import com.saferoute.chat.dto.PythonChatRequestDTO;
import com.saferoute.chat.dto.PythonChatResponseDTO;
import com.saferoute.chat.models.ChatMessage;
import com.saferoute.chat.models.ChatSession;
import com.saferoute.chat.repositories.ChatMessageRepository;
import com.saferoute.chat.repositories.ChatSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RestTemplate restTemplate;

    @Value("${ai.service.url:http://localhost:8000/chat}")
    private String aiServiceUrl;

    @Autowired
    public ChatServiceImpl(ChatSessionRepository chatSessionRepository, ChatMessageRepository chatMessageRepository, RestTemplate restTemplate) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<ChatMessageDTO> getChatHistory(UUID sessionId) {
        return fetchHistory(sessionId);
    }

    @Override
    @Transactional
    public List<ChatMessageDTO> processUserMessage(UUID sessionId, ChatRequestDTO requestDTO) {
        // 1 & 2. Fetch or create ChatSession
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseGet(() -> {
                    ChatSession newSession = new ChatSession();
                    newSession.setId(sessionId);
                    return chatSessionRepository.save(newSession);
                });

        // 3. Save incoming user message
        ChatMessage userMessage = new ChatMessage();
        userMessage.setSession(session);
        userMessage.setRole("USER");
        userMessage.setContent(requestDTO.getMessage());
        chatMessageRepository.save(userMessage);

        // 4. Fetch historical messages
        List<ChatMessageDTO> history = fetchHistory(sessionId);

        // 5. Construct payload for Python
        PythonChatRequestDTO pythonPayload = new PythonChatRequestDTO();
        pythonPayload.setMessage(requestDTO.getMessage());
        pythonPayload.setCurrentRoadContext(requestDTO.getCurrentRoadContext());
        // Do not include the message we just saved in the history block to avoid duplication
        List<ChatMessageDTO> previousHistory = history.stream()
                .limit(Math.max(0, history.size() - 1))
                .collect(Collectors.toList());
        pythonPayload.setHistory(previousHistory);

        // 6. Make synchronous HTTP POST request to Python API
        PythonChatResponseDTO pythonResponse = restTemplate.postForObject(
                aiServiceUrl,
                pythonPayload,
                PythonChatResponseDTO.class
        );

        // 7 & 8. Extract and save AI response
        if (pythonResponse != null && pythonResponse.getReply() != null) {
            ChatMessage aiMessage = new ChatMessage();
            aiMessage.setSession(session);
            aiMessage.setRole("AI");
            aiMessage.setContent(pythonResponse.getReply());
            chatMessageRepository.save(aiMessage);
        }

        // 9. Return updated conversation history
        return fetchHistory(sessionId);
    }

    private List<ChatMessageDTO> fetchHistory(UUID sessionId) {
        return chatMessageRepository.findBySessionIdOrderByTimestampAsc(sessionId)
                .stream()
                .map(msg -> new ChatMessageDTO(msg.getRole(), msg.getContent()))
                .collect(Collectors.toList());
    }
}
