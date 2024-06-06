package QRAB.QRAB.CHATGPT;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/gpt")
public class ChatGptControllerV2 {

    private final ChatGptController chatGptController;

    @GetMapping("/form")
    public String getForm() {
        return "form"; // form.html 타임리프 템플릿
    }

    @PostMapping("/zeroshot")//zero shot
    public String chat1(@RequestParam(name = "prompt", required = false) String prompt, Model model) {
        String response = chatGptController.chat1(prompt);
        model.addAttribute("response", response);
        return "response"; // response.html 타임리프 템플릿
    }

    @PostMapping("/oneshot")//one-shot
    public String chat2(@RequestParam(name = "prompt", required = false) String prompt, Model model) {
        String response = chatGptController.chat2(prompt);
        model.addAttribute("response", response);
        return "response";
    }

    @PostMapping("/fewshot")
    public String chat3(@RequestParam(name = "prompt", required = false) String prompt, Model model) {
        String response = chatGptController.chat3(prompt);
        model.addAttribute("response", response);
        return "response";
    }

    @PostMapping("/analyze")
    public String learningAnalytics(@RequestParam(name = "prompt", required = false) String prompt, Model model) {
        String response = chatGptController.learningAnalytics(prompt);
        model.addAttribute("response", response);
        return "response";
    }
}
