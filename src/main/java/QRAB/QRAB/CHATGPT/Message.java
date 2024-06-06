package QRAB.QRAB.CHATGPT;

import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String role;
    private String content;

}
