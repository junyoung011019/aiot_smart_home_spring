package nsuaiot.service.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ControlService {

    private final RestTemplate restTemplate;
    private final String token = "ba4033b0-778d-4480-8c55-558bfa1a16dc";


    public ResponseEntity<String> postPlugControl(String plugId, String requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "https://goqual.io/openapi/control/" + plugId;
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("API 요청 중 오류 발생");
        }
    }

    public ResponseEntity<Map<String, Object>> controlDevice(String plugId, String action) {
        ResponseEntity<String> response;
        Map<String, Object> responseBody = new HashMap<>();

        if ("on".equals(action)) {
            response = postPlugControl(plugId, "{\n" +
                    "    \"requirments\": {\n" +
                    "        \"power\": true\n" +
                    "    }\n" +
                    "}");
        } else if ("off".equals(action)) {
            response = postPlugControl(plugId, "{\n" +
                    "    \"requirments\": {\n" +
                    "        \"power\": false\n" +
                    "    }\n" +
                    "}");
        } else {
            responseBody.put("status", "error");
            responseBody.put("message", "잘못된 요청 인자입니다.");
            return ResponseEntity.badRequest().body(responseBody);
        }

        boolean isSuccess = response.getStatusCode().is2xxSuccessful();
        responseBody.put("status", isSuccess ? "success" : "error");
        responseBody.put("message", isSuccess ? "플러그 제어 성공" : "플러그 제어 실패");
        responseBody.put("statusCode", response.getStatusCodeValue());
        responseBody.put("response", response.getBody());

        return ResponseEntity.status(response.getStatusCode()).body(responseBody);
    }
}

