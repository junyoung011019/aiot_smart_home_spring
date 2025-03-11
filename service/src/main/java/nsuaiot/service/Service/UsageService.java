package nsuaiot.service.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.UsageRequest;
import nsuaiot.service.Entity.Plug;
import nsuaiot.service.Repository.PlugRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsageService {

    private final RestTemplate restTemplate;
    private final PlugRepository plugRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getApiCall(String url) throws JSONException {
        String token = "ba4033b0-778d-4480-8c55-558bfa1a16dc";
        HttpHeaders headers=new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> requestEntity1 = new HttpEntity<>(headers);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> apiResponse = restTemplate.exchange(url, HttpMethod.GET, requestEntity1, String.class);
        return apiResponse.getBody();
    }

    public List<UsageRequest> usageDataFormatting(String usage) throws JsonProcessingException {
            try {
                List<UsageRequest> usageList= objectMapper.readValue(usage, new TypeReference<List<UsageRequest>>() {});
                return usageList;
            } catch (IOException e) {
                throw new RuntimeException("JSON Parsing Error", e);
            }
    }

    //주간 단체 기기 사용량 조회 : 각 기기 별의 전력량 합 (이번주 사용량 ~7일)
    public ResponseEntity<String> weekUsageGroupPlug() throws JsonProcessingException {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6);
        String endDate = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String startDate = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<Plug> plugList = plugRepository.findAll();
        JSONArray plugsUsageData = new JSONArray().put(new JSONObject().put("message", startDate +"부터 "+endDate+"까지의 전력 데이터"));
        for(int i=0;i<plugList.size();i++){
            Plug plugData = plugList.get(i);
            String plugId = plugData.getPlugId();
            String url = "https://goqual.io/openapi/devices/plug/"+plugId+"/history?startDate="+startDate+"&endDate="+endDate;
            String response = getApiCall(url);
            List<UsageRequest> usageList = usageDataFormatting(response);

            double usageTotal = 0;
            for(UsageRequest usage : usageList){
                usageTotal+=usage.getUsage();
            }
            JSONObject plugUsageData = new JSONObject().put("plugId",plugId)
                    .put("plugName",plugData.getPlugName())
                            .put("usage",usageTotal);
            plugsUsageData.put(plugUsageData);
        }

        return ResponseEntity.ok(plugsUsageData.toString());
    }

    public ResponseEntity<String> weekUsagePlug(String plugId) throws JsonProcessingException {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6);
        String endDate = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String startDate = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String url = "https://goqual.io/openapi/devices/plug/"+plugId+"/history?startDate="+startDate+"&endDate="+endDate;
        String response = getApiCall(url);

        List<UsageRequest> usageList = usageDataFormatting(response);
        if(usageList.isEmpty()){
            return ResponseEntity.status(204).body(" ");
        }
        return ResponseEntity.ok(usageList.toString());
    }


    public ResponseEntity<String> monthUsage() throws JsonProcessingException {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(200);
        String endDate = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String startDate = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<Plug> plugIdList = plugRepository.findAll();

        //월간 전력량 데이터 저장소
        Map<String, Double> monthUsageMap = new HashMap<>();

        for(Plug plug : plugIdList){
            String plugId = plug.getPlugId();
            String url = "https://goqual.io/openapi/devices/plug/"+plugId+"/history?startDate="+startDate+"&endDate="+endDate;
            String response = getApiCall(url);
            List<UsageRequest> usageList = usageDataFormatting(response);

            for(UsageRequest usage : usageList){
                String month =usage.getDate().substring(0,7);
                if(monthUsageMap.containsKey(month)){
                    double monthUsageDate = monthUsageMap.get(month);
                    monthUsageMap.put(month,monthUsageDate+usage.getUsage());
                }else{
                    monthUsageMap.put(month,usage.getUsage());
                }
            }
        }


        List<Map<String, Object>> formattedData = monthUsageMap.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", entry.getKey());
                    map.put("usage", Math.round(entry.getValue() * 1000) / 1000.0); // 소수점 3자리 반올림
                    return map;
                })
                .collect(Collectors.toList());

        // JSON 출력 (Jackson 사용)
        ObjectMapper objectMapper = new ObjectMapper();
        String returnData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(formattedData);


        return ResponseEntity.ok(returnData);
    }

}
