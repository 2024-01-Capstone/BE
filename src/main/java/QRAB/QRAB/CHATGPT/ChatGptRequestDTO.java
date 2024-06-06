package QRAB.QRAB.CHATGPT;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatGptRequestDTO {
    private String model;
    private List<Message> messages;
    private double temperature;
    private int max_tokens;
    private int top_p;
    private int frequency_penalty;
    private int presence_penalty;
    public ChatGptRequestDTO(String model, String prompt) {
        this.model = model;
        this.messages =  new ArrayList<>();
        this.messages.add(new Message("system", "You are a helpful assistant.")); // 시스템 메시지 추가
        this.messages.add(new Message("user", prompt));
        this.temperature = 0.7;//사실을 기반으로 퀴즈 생성하도록
        this.max_tokens = 4096; // 최대 토큰
        this.top_p = 1;//모든 단어를 고려
        this.frequency_penalty = 0;
        this.presence_penalty = 0;

    }
}