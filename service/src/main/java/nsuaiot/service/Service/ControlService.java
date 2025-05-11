package nsuaiot.service.Service;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import nsuaiot.service.DTO.ApiResponse;
import nsuaiot.service.Entity.Plug;
import org.json.JSONArray;
import org.json.JSONObject;
import lombok.RequiredArgsConstructor;
import nsuaiot.service.Repository.PlugRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ControlService {

    private final PlugRepository plugRepository;
    private final RestTemplate restTemplate;

    @Value("${heyhome.token}")
    private String HEYHOME_TOKEN;

    //헤이홈 서버로 기기 제어 요청
    public ResponseEntity<?> postPlugControl(String plugId, String requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + HEYHOME_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "https://goqual.io/openapi/control/" + plugId;
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse("API 요청 중 오류 발생"));
        }
    }

    //단일 기기 제어(플러그 아이디, 액션 방법, 회원 아이디)
    public ResponseEntity<?> controlDevice(String plugId, String action, String userId) {

        //plugId로 DB 검색
        Optional<Plug> plugData = plugRepository.findByPlugIdAndOwnerId(plugId,userId);

        //사용자 요청 본문 검증 (1. 플러그 ID 확인 / 2. 토큰 사용자와 플러그 주인 비교 / 3. 액션 값 확인)
        if(plugData.isEmpty()){
            return ResponseEntity.status(404).body(new ApiResponse("제어하려는 플러그가 없습니다."));
        }
        if(!userId.equals(plugData.get().getOwnerId())){
            return ResponseEntity.status(403).body(new ApiResponse("제어하려는 플러그의 소유자가 아닙니다."));
        }
        if (!"on".equals(action) && !"off".equals(action)) {
            return ResponseEntity.status(400).body(new ApiResponse("잘못된 동작 방식입니다."));
        }

        //헤이홈에서 요청하는 본문 생성 및 생성
        String power = String.valueOf("on".equals(action));
        JSONObject requestBody = new JSONObject().put("requirments",new JSONObject().put("power",power));
        ResponseEntity<?> response = postPlugControl(plugId, requestBody.toString());

        //헤이홈으로부터 응답에 따라 리턴 데이터 정리
        boolean isSuccess = response.getStatusCode().is2xxSuccessful();
        JSONObject responseBody = new JSONObject().put("plugName",plugData.get().getPlugName())
                        .put("status", isSuccess ? "success" : "error")
                                .put("message", isSuccess ? "플러그 제어 성공" : "플러그 제어 실패")
                                                .put("response", response.getBody());

        return ResponseEntity.status(response.getStatusCode()).body(responseBody.toString());
    }
}

