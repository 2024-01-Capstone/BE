package QRAB.QRAB.CLOVA;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class CheckController {

    @Value("${naver.ocr.key}")
    private String apiKey;

    private final ClovaOcrApi clovaOcrApi;

    public CheckController(ClovaOcrApi clovaOcrApi) {
        this.clovaOcrApi = clovaOcrApi;
    }

    @PostMapping("/clovaOcr")
    public ResponseEntity<List<String>> ocr() throws IOException {
        String fileName = "test.jpg";
        Resource resource = new ClassPathResource("static/image/test/" + fileName);
        File file = resource.getFile();

        List<String> result = clovaOcrApi.callApi("POST", file.getPath(), "jpg");

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
