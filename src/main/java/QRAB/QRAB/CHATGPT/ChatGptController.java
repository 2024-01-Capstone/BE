package QRAB.QRAB.CHATGPT;



import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/gpt")
@RequiredArgsConstructor
public class ChatGptController {
    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private final RestTemplate template;

    public int tokenCount(String str) {
        // 토큰화 로직, 여기서는 단어당 1개의 토큰으로 간주
        return str.split("\\s+").length;
    }

    @GetMapping("/zeroshot")
    public String chat1(@RequestParam(name = "prompt", required = false) String prompt){
        if (prompt == null) {
            prompt = "";
        }

        // 퀴즈 정보 조립
        String quizInfoTemplate  = "위의 자료는 블로그 학습자료이고 사용자는 블로그 내용을 바탕으로 퀴즈를 풀려고 합니다.\n"+
                "사용자는 컴퓨터공학과를 주전공으로 하고있으며 경영학과를 복수전공으로 하고 있습니다.\n"+
                "블로그 내용은 주전공 혹은 복수전공과 관련된 내용일 수도 있고 아닐 수도 있습니다.\n" +
                "블로그 내용을 바탕으로 퀴즈를 생성하려고 합니다.\n" +
                "퀴즈의 조건은 다음과 같습니다:\n" +
                "각 퀴즈 질문 형식은 ‘{문제번호. 질문} (쉬움)’ 혹은 ‘{문제번호. 질문} (어려움)’ 이 형식으로 다음과 같이 작성해주세요.\n" +
                "1. 이것의 정의는 무엇인가? (쉬움)\n" +
                "각 퀴즈는 객관식 사지선다형으로 다음과 같이 작성해주세요.\n" +
                "‘a. {답변}\n" +
                "b. {답변}\n" +
                "c. {답변}\n" +
                "d. {답변}’ 이 형식으로 작성해주세요.\n" +
                "각 퀴즈의 정답 형식은 다음과 같이 작성해주세요:\n" +
                "'답: {c. 정답 내용}'\n" +
                "각 퀴즈 풀이의 형식은 다음과 같이 작성해주세요:\n" +
                "'풀이: {풀이 내용}'\n" +
                "각 퀴즈의 한 줄 요약 형식은 다음과 같이 작성해주세요:\n" +
                "'요약: {요약 내용}'\n";

        int totalTokenCount = tokenCount(prompt);
        int numberOfQuestions = totalTokenCount < 2000 ? 5 : 10;
        String quizInfo = quizInfoTemplate  + "10개의 퀴즈를 출제해주세요. 이때, 각 퀴즈에는 퀴즈 질문 형식, 객관식 형식, 정답 형식, 풀이 형식, 요약 형식이 포함되어야 합니다.";
        String combinedPrompt = prompt + "\n\n" + quizInfo;
        ChatGptRequestDTO chatGptRequestDTO = new ChatGptRequestDTO(model, combinedPrompt);
        ChatGptResponseDTO chatGptResponseDTO =  template.postForObject(apiURL, chatGptRequestDTO, ChatGptResponseDTO.class);
        return chatGptResponseDTO.getChoices().get(0).getMessage().getContent();
    }


    @GetMapping("/oneshot")
    public String chat2(@RequestParam(name = "prompt", required = false) String prompt){
        if (prompt == null) {
            prompt = "";
        }

        // 퀴즈 정보 조립
        String quizInfoTemplate = "위의 자료는 블로그 학습자료이고 사용자는 블로그 내용을 바탕으로 퀴즈를 풀려고 합니다.\n" +
                "사용자는 컴퓨터공학과를 주전공으로 하고있으며 경영학과를 복수전공으로 하고 있습니다.\n" +
                "블로그 내용은 주전공 혹은 복수전공과 관련된 내용일 수도 있고 아닐 수도 있습니다.\n" +
                "블로그 내용을 바탕으로 퀴즈를 생성하려고 합니다.\n" +
                "퀴즈의 조건은 다음과 같습니다:\n" +
                "각 퀴즈 질문 형식은 ‘{문제번호. 질문} (쉬움)’ 혹은 ‘{문제번호. 질문} (어려움)’ 이 형식으로 다음과 같이 작성해주세요.\n" +
                "1. 이것의 정의는 무엇인가? (쉬움)\n" +
                "각 퀴즈는 객관식 사지선다형으로 다음과 같이 작성해주세요.\n" +
                "‘a. {답변}\n" +
                "b. {답변}\n" +
                "c. {답변}\n" +
                "d. {답변}’ 이 형식으로 작성해주세요.\n" +
                "각 퀴즈의 정답 형식은 다음과 같이 작성해주세요:\n" +
                "'답: {c. 정답 내용}'\n" +
                "각 퀴즈 풀이의 형식은 다음과 같이 작성해주세요:\n" +
                "'풀이: {풀이 내용}'\n" +
                "각 퀴즈의 한 줄 요약 형식은 다음과 같이 작성해주세요:\n" +
                "'요약: {요약 내용}'\n";

        // One-Shot Prompting 예시 추가
        String examplePrompt = "아래는 예시 퀴즈입니다.\n" +
                "예시 블로그 내용:\n" +
                "어느 재화의 가격이 낮을수록, 소득이 높을수록, 대체재의 가격이 높을수록, 보완재의 가격이 낮을수록 그 재화에 대한 소비자들의 수요는 늘어난다. 그런데 수요에 대한 논의는 정량적(quantitative)이 아니고 정성적(qualitative)이었다. 즉, 수요량의 변화 방향에 대해서만 설명했을 뿐 변화의 크기에 대해서는 언급하지 않은 것이다. 경제학자들은 수요 결정변수의 변화에 대해 수요량이 얼마나 변하는지 파악하기 위해 탄력성(elasticity)을 사용한다.\n\n" +
                "1. 대체재가 많을수록 그 재화의 수요는 어떻게 되나요? (쉬움)\n" +
                "a. 비탄력적이다\n" +
                "b. 탄력적이다\n" +
                "c. 변화가 없다\n" +
                "d. 감소한다\n" +
                "답: b. 탄력적이다\n" +
                "풀이: 대체재가 많을수록 소비자들은 그 재화 대신 다른 재화를 사용할 수 있어 그 재화의 수요는 탄력적입니다. 예를 들어, 버터와 마가린은 서로 대체재로, 가격 변화에 민감하게 반응합니다.\n" +
                "요약: 대체재가 많을수록 그 재화의 수요는 탄력적입니다.\n\n" +
                "다음은 실제 사용자가 입력한 블로그 내용입니다.\n\n";

        // 토큰 수에 따라 퀴즈 수 결정
        int totalTokenCount = tokenCount(prompt);
        int numberOfQuestions = totalTokenCount < 2000 ? 5 : 10;

        // 퀴즈 요청 정보 조립
        String quizInfo = quizInfoTemplate +   "5개의 퀴즈를 출제해주세요. 이때, 각 퀴즈에는 퀴즈 질문 형식, 객관식 형식, 정답 형식, 풀이 형식, 요약 형식이 포함되어야 합니다.";

        // 최종 프롬프트 조립
        String combinedPrompt = examplePrompt + prompt + "\n\n" + quizInfo;

        // API 요청 및 응답 처리
        ChatGptRequestDTO chatGptRequestDTO = new ChatGptRequestDTO(model, combinedPrompt);
        ChatGptResponseDTO chatGptResponseDTO =  template.postForObject(apiURL, chatGptRequestDTO, ChatGptResponseDTO.class);

        // 응답 결과 반환
        return chatGptResponseDTO.getChoices().get(0).getMessage().getContent();
        //return chatGptResponseDTO; //응답결과 전체 반환
    }


    @GetMapping("/fewshot")
    public String chat3(@RequestParam(name = "prompt", required = false) String prompt){
        if (prompt == null) {
            prompt = "";
        }
        // 퀴즈 정보 조립
        String quizInfoTemplate = "위의 자료는 블로그 학습자료이고 사용자는 블로그 내용을 바탕으로 퀴즈를 풀려고 합니다.\n"+
                "사용자는 컴퓨터공학과를 주전공으로 하고있으며 경영학과를 복수전공으로 하고 있습니다.\n"+
                "블로그 내용은 주전공 혹은 복수전공과 관련된 내용일 수도 있고 아닐 수도 있습니다.\n" +
                "블로그 내용을 바탕으로 퀴즈를 생성하려고 합니다.\n" +
                "퀴즈의 조건은 다음과 같습니다:\n" +
                "각 퀴즈 질문 형식은 ‘{문제번호. 질문} (쉬움)’ 혹은 ‘{문제번호. 질문} (어려움)’ 이 형식으로 다음과 같이 작성해주세요.\n" +
                "1. 이것의 정의는 무엇인가? (쉬움)\n" +
                "각 퀴즈는 객관식 사지선다형으로 다음과 같이 작성해주세요.\n" +
                "‘a. {답변}\n" +
                "b. {답변}\n" +
                "c. {답변}\n" +
                "d. {답변}’ 이 형식으로 작성해주세요.\n" +
                "각 퀴즈의 정답 형식은 다음과 같이 작성해주세요:\n" +
                "'답: {c. 정답 내용}'\n" +
                "각 퀴즈 풀이의 형식은 다음과 같이 작성해주세요:\n" +
                "'풀이: {풀이 내용}'\n" +
                "각 퀴즈의 한 줄 요약 형식은 다음과 같이 작성해주세요:\n" +
                "'요약: {요약 내용}'\n" ;


        // few-Shot Prompting 예시 추가

        String examplePrompt1 = "아래는 예시 퀴즈입니다.\n" +
                "예시 블로그 내용:\n" +
                "어느 재화의 가격이 낮을수록, 소득이 높을수록, 대체재의 가격이 높을수록, 보완재의 가격이 낮을수록 그 재화에 대한 소비자들의 수요는 늘어난다. 그런데 수요에 대한 논의는 정량적(quantitative)이 아니고 정성적(qualitative)이었다. 즉, 수요량의 변화 방향에 대해서만 설명했을 뿐 변화의 크기에 대해서는 언급하지 않은 것이다. 경제학자들은 수요 결정변수의 변화에 대해 수요량이 얼마나 변하는지 파악하기 위해 탄력성(elasticity)을 사용한다.\n\n" +
                "1. 대체재가 많을수록 그 재화의 수요는 어떻게 되나요? (쉬움)\n" +
                "a. 비탄력적이다\n" +
                "b. 탄력적이다\n" +
                "c. 변화가 없다\n" +
                "d. 감소한다\n" +
                "답: b. 탄력적이다\n" +
                "풀이: 대체재가 많을수록 소비자들은 그 재화 대신 다른 재화를 사용할 수 있어 그 재화의 수요는 탄력적입니다. 예를 들어, 버터와 마가린은 서로 대체재로, 가격 변화에 민감하게 반응합니다.\n" +
                "요약: 대체재가 많을수록 그 재화의 수요는 탄력적입니다.\n\n";


        String examplePrompt2 =  "아래는 또 다른 예시 퀴즈입니다.\n"+
                "예시 블로그 내용:\n" +
                "이 글은 Amazon Virtual Private Cloud (Amazon VPC)에 대한 설명이다. Amazon VPC는 AWS 리소스를 논리적으로 격리된 가상 네트워크에서 시작할 수 있게 해주는 서비스이다. "+
                "1. Amazon VPC에서 두 VPC 간의 네트워크 트래픽을 라우팅할 수 있는 기능은 무엇인가요? (쉬움)\n" +
                "a. Internet Gateway\n" +
                "b. VPC Endpoint\n" +
                "c. VPC Peering\n" +
                "d. Transit Gateway\n" +
                "답: c. VPC Peering\n" +
                "풀이: VPC Peering은 두 VPC 간의 네트워크 트래픽을 라우팅할 수 있게 해주는 기능입니다.\n" +
                "요약: Amazon VPC에서 두 VPC 간의 네트워크 트래픽을 라우팅할 수 있게 해주는 기능은 VPC Peering입니다.\n\n"+
                "다음은 실제 사용자가 입력한 블로그 내용입니다. 반드시 이 블로그 내용에 관한 퀴즈를 출제해주세요.\n\n";


        // 토큰 수에 따라 퀴즈 수 결정
        int totalTokenCount = tokenCount(prompt);
        int numberOfQuestions = totalTokenCount < 2000 ? 5 : 10;

        // 퀴즈 요청 정보 조립
        String quizInfo = quizInfoTemplate  + "5개의 퀴즈를 출제해주세요. 이때, 각 퀴즈에는 퀴즈 질문 형식, 객관식 형식, 정답 형식, 풀이 형식, 요약 형식이 포함되어야 합니다.";

        // 최종 프롬프트 조립
        String combinedPrompt = examplePrompt1 +examplePrompt2 +  prompt + "\n\n" + quizInfo;
        ChatGptRequestDTO chatGptRequestDTO = new ChatGptRequestDTO(model, combinedPrompt);
        ChatGptResponseDTO chatGptResponseDTO =  template.postForObject(apiURL, chatGptRequestDTO, ChatGptResponseDTO.class);
        return chatGptResponseDTO.getChoices().get(0).getMessage().getContent();
        //return chatGptResponseDTO; //응답결과 전체 반환
    }



    //학습 분석
    @GetMapping("/analyze")
    public String learningAnalytics(@RequestParam(name = "prompt", required = false) String prompt) {
        if (prompt == null) {
            prompt = "";
        }

        // 퀴즈 정보 조립
        String quizInfoTemplate = "1. [카테고리 제목] : 하단의 \"\"\"으로 구분된 [퀴즈요약]을 이해한 후 [퀴즈요약]에 있는 [노트]를 카테고리 별로 크게 분류합니다.\n" +
                "   분류한 카테고리들을 전부 나열합니다. 만약 기존의 카테고리에 속한다면 새롭게 카테고리를 생성하지 않고 해당 카테고리로 분류합니다.\n" +
                "   분류하는 기준은 대학교 학과를 기준으로 합니다.\n" +
                "2. [평균 정답률] : 카테고리 별로 노트를 분류하고, 노트들의 정답률을 평균 내어 새로운 [평균 정답률]을 구합니다.\n" +
                "3. [취약 카테고리] : [평균 정답률]이 가장 낮은 카테고리를 보여줍니다.\n" +
                "4. [학습 평가] : 사용자가 푼 퀴즈들을 요약한 [퀴즈요약] 이해하고 사용자가 잘 이해하고 있는 카테고리부터 사용자가 잘 이해하지 못하는 카테고리까지 알려주며 사용자의 학습을 평가합니다.\n" +
                "5. [학습 분석] : 전문적인 배경 지식을 활용해 학습 분석을 합니다.\n" +
                "6. [학습 요령] : 앞으로 사용자가 해야 할 학습 요령을 제안합니다.\n" +
                "7. [추가 자료] : 사용자가 부족한 부분을 효율적으로 공부할 수 있도록 책이나 웹사이트 같은 추가 자료를 추천해줍니다.\n\n" +
                "다음 JSON 형식의 출력을 이용하세요.\n" +
                "{\n" +
                "    category : [카테고리제목]은 여기,\n" +
                "    avg_correct_rate : [평균 정답률]은 여기,\n" +
                "    Vulnerable_Category: [취약카테고리]는 여기,\n" +
                "    Learning_result : [학습 평가]는 여기,\n" +
                "    analysis : [학습 분석]은 여기,\n" +
                "    action_list : [학습 요령]은 여기,\n" +
                "    addtional_list : [추가 자료]는 여기,\n" +
                "}";

        String quizInfoTemplate2 = """
                첫번째 예시입니다.
                입력이 다음과 같이 들어옵니다.,
                
                ""안의 내용을 기반으로 퀴즈를 생성하였고 각 퀴즈 요약 옆에 사용자가 해당 퀴즈를 맞혔는지 틀렸는지를 괄호로 구분하였습니다.
                [정답률] : 2/5, 40%
                [노트1] : "CNN (Convolutional Neural Network) 관련 노트"
                [노트1]를 기반으로 생성한 5개의 퀴즈 내용 요약
                1. CNN은 컴퓨터 비전 분야에서 사용되는 Deep Learning 신경망 아키텍처 중 하나입니다. (맞힘)
                2. 입력 레이어는 모델에 입력을 제공하는 역할을 합니다. (맞힘)
                3. 출력 계층은 각 클래스의 출력을 확률 점수로 변환하는 역할을 합니다. (틀림)
                4. 손실 함수는 네트워크가 얼마나 잘 작동하는지 측정하는 데 사용됩니다. (틀림)
                5. 역전파는 오류를 계산하고 손실을 최소화하기 위해 사용되는 기법입니다. (틀림)
                
                [정답률] : 3/5, 60%
                [노트2] : "도메인 이름 시스템(DNS) 관리, 트래픽 라우팅, 로드 밸런싱, DNS 레코드 유형 및 라우팅 정책에 관한 노트"
                [노트2]를 기반으로 생성한 5개의 퀴즈 내용 요약
                1. Amazon Route53을 사용하면 사용자는 도메인 이름을 등록하고 관리할 수 있습니다. (맞힘)
                2. Route 53의 글로벌 DNS 확인 기능을 통해 사용자는 전 세계 어디서나 웹 사이트와 서비스에 즉시 액세스할 수 있습니다. (맞힘)
                3. Route53의 지리적 위치 라우팅 정책을 사용하면 사용자의 지리적 위치를 기반으로 트래픽을 리소스로 라우팅할 수 있습니다. (맞힘)
                4. CNAME 레코드는 사용자를 웹 사이트에 연결하는 데 도움을 줍니다. (틀림)
                5. 다중값 라우팅 정책을 사용하면 DNS 쿼리에 대한 응답으로 여러 값을 반환할 수 있습니다. (틀림)
                
                [정답률] : 3/5, 60%
                [노트3] : "Amazon EC2를 사용하여 Linux 인스턴스를 시작하고 관리하는 방법에 관한 노트"
                [노트3]를 기반으로 생성한 5개의 퀴즈 내용 요약
                1. Amazon EC2의 보안 그룹은 수신 및 발신 트래픽을 제어하는 역할을 합니다. (틀림)
                2. Amazon EC2 인스턴스를 시작할 때 키 페어를 생성해야 합니다. (맞힘)
                3. Amazon EC2를 사용하여 인스턴스를 시작합니다. (맞힘)
                4. Amazon EC2의 가용 영역은 AWS 리전 내에 있는 격리된 위치를 의미합니다. (틀림)
                5. Amazon EC2에서 사용할 수 있는 무료 인스턴스 유형은 t2.micro입니다. (맞힘)
                
                [정답률] : 5/5, 100%
                [노트4] : "물질의 전하(charge), 전압, 전류, 저항과 같은 전자공학을 이루는 기본적인 요소들에 관한 노트"
                [노트4]를 기반으로 생성한 5개의 퀴즈 내용 요약
                1. 전자기력은 양전하와 음전하로 구성되어 있습니다. (맞힘)
                2. 전류는 전자의 흐름을 나타내는 개념입니다. (맞힘)
                3. 전압은 전자를 이동시키는 힘을 나타냅니다. (맞힘)
                4. 저항은 전자의 흐름을 방해하는 부품입니다. (맞힘)
                5. 전압의 단위는 볼트(V)입니다. (맞힘)
                
                [정답률] : 4/5, 80%
                [노트5] : "다국적 기업의 외국인 비용에 관한 노트"
                [노트5]를 기반으로 생성한 5개의 퀴즈 내용 요약
                1. 다국적기업이 현지 시장의 특성을 잘 모르기 때문에 부담하는 일종의 유무형상의 모든 비용은 현지화 비용입니다. (맞힘)
                2. CAGE Framework에서 다국적기업이 부담할 외국인비용은 문화, 제도, 지리, 경제적 차이로부터 발생합니다. (맞힘)
                3. 다국적기업은 문화적 차이를 줄임으로써 외국인비용을 감소시키고 경제적 차이를 극복할 수 있습니다. (맞힘)
                4. 외국인 비용의 예시로는 커뮤니케이션 비용, 정보수집 비용, 국제 교통, 통신비 등이 있습니다. (맞힘)
                5. '자본집약적 산업'에서 기업은 규모의 경제를 통해 비용을 절감하고 효율성을 높이려고 합니다. (틀림)
                
                답변은 다음과 같아야 합니다.
                
                {
                    "category" : {
                        "컴퓨터 공학": [ "CNN (Convolutional Neural Network) 관련 노트","도메인 이름 시스템(DNS) 관리, 트래픽 라우팅, 로드 밸런싱, DNS 레코드 유형 및 라우팅 정책에 관한 노트",  "Amazon EC2를 사용하여 Linux 인스턴스를 시작하고 관리하는 방법에 관한 노트"],
                          "전자공학": ["물질의 전하(charge), 전압, 전류, 저항과 같은 전자공학을 이루는 기본적인 요소들에 관한 노트"],
                          "경영학": ["다국적 기업의 외국인 비용에 관한 노트"],      
                    },
                    "avg_correct_rate" : {
                        "컴퓨터 공학": "53.33% (8/15)",
                        "전자공학": "100% (5/5)",
                        "경영학": "80% (4/5)",
                      },
                    "Vulnerable Category" : "컴퓨터 공학",
                    "Learning_result" : {
                        "잘 이해하고 있는 카테고리": [
                          "전자공학",
                          "경영학"
                        ],
                        "이해가 필요한 카테고리": [
                          "컴퓨터 공학"
                        ]
                      },
                    "analysis" : "사용자는 전자공학과 경영학 분야에서 높은 이해도를 보이고 있습니다. 특히 전자공학에서는 모든 문제를 맞히며 매우 높은 이해도를 보여주고 있습니다. 반면, 컴퓨터 공학 분야에서는 상대적으로 낮은 정답률을 보이며 추가 학습이 필요합니다.",
                    "action_list" : [
                        "컴퓨터 공학 분야의 기초 개념을 다시 복습하고 이해도를 높이기 위한 추가 학습을 진행하세요.",
                        "특히 CNN (Convolutional Neural Network) 관련 노트에 대한 학습이 부족하므로 CNN에 대한 개념을 다시 학습하고 관련된 예제 및 응용을 풀어보는 것이 도움이 될 것입니다.",
                        "CNN을 자세히 다룬 웹사이트를 추천해드리겠습니다 {https://jarikki.tistory.com/26, https://www.coursera.org/learn/convolutional-neural-networks}",
                        "실제 프로젝트나 실습을 통해 이론을 실습으로 강화하세요."
                      ],
                    "additional_resources": [
                        {
                            "title": "CNN의 기초 개념 학습해보기"
                            
                        },
                        {
                            "title": "CNN의 기초 개념 학습해보기"
                           
                        },
                        {
                            "title": "CNN의 출력 계층 이해해보기"
                            
                        },
                        {
                            "title": "손실 함수와 역전파에 대한 심화학습해보기"
                           
                        }
                    ]
                }
                """;

        String combinedPrompt = prompt + "\n\n" + quizInfoTemplate + quizInfoTemplate2;
        ChatGptRequestDTO chatGptRequestDTO = new ChatGptRequestDTO(model, combinedPrompt);
        ChatGptResponseDTO chatGptResponseDTO = template.postForObject(apiURL, chatGptRequestDTO, ChatGptResponseDTO.class);

        return chatGptResponseDTO.getChoices().get(0).getMessage().getContent();
    }
}