package com.saferoute.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PythonChatRequestDTO {
    private String message;
    private String currentRoadContext;
    private List<ChatMessageDTO> history;
}
