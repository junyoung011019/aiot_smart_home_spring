package nsuaiot.service.Service;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.Entity.Plug;
import nsuaiot.service.Entity.Usage;
import nsuaiot.service.Repository.PlugRepository;
import nsuaiot.service.Repository.UsageRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.client.RestTemplate;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Component
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsageScheduledTask {

    private final PlugRepository plugRepository;
    private final UsageRepository usageRepository;
    private final RestTemplate restTemplate;

    @Value("${heyhome.token}")
    private String HEYHOME_TOKEN;

    //헤이홈 서버로의 Get 요청
    private JSONArray getApiCallJson(String url) throws JSONException {
        String token = HEYHOME_TOKEN;
        HttpHeaders headers=new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> requestEntity1 = new HttpEntity<>(headers);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> apiResponse = restTemplate.exchange(url, HttpMethod.GET, requestEntity1, String.class);
        return new JSONArray(apiResponse.getBody());
    }

    //매일 00시 50분에 헤이홈 데이터 저장
    @Scheduled(cron = "0 50 0 * * ?")
    public void saveUsage(){
        //어제 날짜
        String yesterday= LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 한국 시간 (KST) 기준으로 Date 변환
        LocalDateTime yesterdayDate = LocalDateTime.now().minusDays(1);
        Date date = Date.from(yesterdayDate.atZone(ZoneId.of("Asia/Seoul")).toInstant());

        List<Plug> plugList = plugRepository.findAll();

        for(int i=0;i<plugList.size();i++){
            String plugId = plugList.get(i).getPlugId();
            try{
                Usage usageData = new Usage(plugId,date);

                String url = "https://goqual.io/openapi/devices/plug/"+plugId+"/history?startDate="+yesterday+"&endDate="+yesterday;
                JSONArray response = getApiCallJson(url);

                double usage=0.0;
                int change=0;

                for(int j=0;j<response.length();j++){
                    JSONObject data =response.getJSONObject(j);
                    Object rawValue= data.get("value");

                    if (rawValue instanceof Integer) {
                        usage += ((Integer) rawValue).doubleValue();
                        change+=1;
                    } else if (rawValue instanceof Double) {
                        usage += (Double) rawValue;
                        change+=1;
                    } else {
                        usage += Double.parseDouble(rawValue.toString());
                        change+=1;
                    }
                }
                if(change != 0){
                    // 소수점 3자리 반올림
                    double roundedUsage = new BigDecimal(usage)
                            .setScale(3, RoundingMode.HALF_UP)
                            .doubleValue();
                    usageData.setUsageValue(roundedUsage);
                    usageRepository.save(usageData);
                }

                System.out.println(plugRepository.findByPlugId(plugId).getPlugName() + " 플러그 저장 완료");
            }catch (Exception e){
                System.out.println(e.getMessage());
                System.out.println("뭔가 오류");
            }


        }


    }

}
