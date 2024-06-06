package QRAB.QRAB.CHATGPT;

import lombok.*;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGptResponseDTO {
    private List<Choice> choices;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private int index; // gpt 인덱스 번호
        private Message message; // [role, content] response

    }
}