package nsuaiot.service.Service;

import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.cglib.core.Local;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UsageWeekService {

    private final RestTemplate restTemplate;

    private ResponseEntity<String> getApiCall(String url) throws JSONException {
        String token = "ba4033b0-778d-4480-8c55-558bfa1a16dc";
        HttpHeaders headers=new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> requestEntity1 = new HttpEntity<>(headers);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> apiResponse = restTemplate.exchange(url, HttpMethod.GET, requestEntity1, String.class);
        return apiResponse;
    }

    public ResponseEntity<String> weekUsagePlug(String plugId) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6);
        String endDate = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String startDate = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String url = "https://goqual.io/openapi/devices/plug/"+plugId+"/history?startDate="+startDate+"&endDate="+endDate;
        ResponseEntity<String> response = getApiCall(url);
        System.out.println(response.getBody());

        return ResponseEntity.ok(response.getBody());
    }


}
