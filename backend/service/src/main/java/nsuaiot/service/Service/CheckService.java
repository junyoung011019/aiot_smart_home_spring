package nsuaiot.service.Service;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.ApiResponse;
import nsuaiot.service.Entity.Plug;
import nsuaiot.service.Repository.PlugRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckService {
    private final RestTemplate restTemplate;
    private final PlugRepository plugRepository;

    @Value("${heyhome.token}")
    private String HEYHOME_TOKEN;

    //API 호출 함수
    private Object getApiCall(String url) throws JSONException {
        String token = HEYHOME_TOKEN;
        HttpHeaders headers=new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> apiResponse = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        String jsonDate=apiResponse.getBody();
        // (JsonArray / JsonObject) 확인
        if (jsonDate.trim().startsWith("[")) {
            return new JSONArray(jsonDate);
        } else {
            return new JSONObject(jsonDate);
        }
    }

    //단일 플러그 조회
    public ResponseEntity<?> checkPlug(String plugId, String userId) {
        if(plugRepository.findByPlugIdAndOwnerId(plugId,userId).isEmpty()){
            return ResponseEntity.status(404).body(new ApiResponse("조회할 플러그 ID가 올바르지 않습니다."));
        };
        
        //리턴할 데이터
        LinkedHashMap<String,Object> findDevice = new LinkedHashMap<>();

        //기기 정보 확인 (플러그 Id, 플러그 이름, 온라인 여부)
        try {
            String devicesUrl = "https://goqual.io/openapi/devices";
            JSONArray devicesArray = (JSONArray) getApiCall(devicesUrl);

            //여러 기기 정보 중에 plugId 찾기
            for(int i=0; i<devicesArray.length();i++){
                JSONObject deviceInfo =devicesArray.getJSONObject(i);
                if(deviceInfo.getString("id").equals(plugId)){
                    findDevice.put("id", deviceInfo.get("id"));
                    findDevice.put("name", deviceInfo.get("name"));
                    findDevice.put("online", deviceInfo.get("online"));
                    break;
                }
            }
        }catch (Exception e){
            System.out.println("기기 정보 확인중 에러 발생 : "+e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse("기기 정보 확인중 서버 에러 발생"));
        }

        //기기 상태 확인 (전원, curCurrnet 현재 전류, curPower 전력, curVoltage 전압)
        try {
            String deviceUrl = "https://goqual.io/openapi/device/"+plugId;
            JSONObject deviceState = (JSONObject) getApiCall(deviceUrl);

            //여러 정보중 deviceState만 파싱
            JSONObject state = deviceState.getJSONObject("deviceState");

            //전원, 전류, 전력, 전압 주입
            findDevice.put("power",state.optBoolean("power"));
            findDevice.put("curCurrent",state.optInt("curCurrent"));
            findDevice.put("curPower",state.optInt("curPower"));
            findDevice.put("curVoltage",state.optInt("curVoltage"));
        }catch (Exception e){
            System.out.println("기기 상태 확인중 에러 발생 : "+e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse("기기 상태 확인중 서버 에러 발생"));
        }

        return ResponseEntity.ok(findDevice);
    }



    //기기 상태 리스트 리턴 함수
    public ResponseEntity<?> checkPlugList(String userId){
        
        //리턴할 데이터
        Map<String, JSONObject> deviceStore = new HashMap<>();

        //기기 정보 조회
        try {
            String devicesUrl = "https://goqual.io/openapi/devices";
            JSONArray devicesInfo = (JSONArray) getApiCall(devicesUrl);
            for (int i = 0; i < devicesInfo.length(); i++) {
                JSONObject deviceInfo = devicesInfo.getJSONObject(i);
                
                //해쉬 맵(중복 저장 X) 저장을 위해 id 값 저장
                String deviceId = deviceInfo.optString("id", "unknown");

                //만약 플러그가 (토큰) 사용자가 아니라면 패스
                if(plugRepository.findByPlugIdAndOwnerId(deviceId,userId).isEmpty()){
                    continue;
                }

                JSONObject deviceDate = new JSONObject()
                        .put("id", deviceInfo.optString("id"))
                        .put("name", deviceInfo.optString("name"))
                        .put("online", deviceInfo.optBoolean("online"));
                deviceStore.put(deviceId, deviceDate);
            }
        }catch (Exception e) {
            System.out.println("기기 정보 갱신중 에러 발생 : "+e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse("기기 정보 갱신중 서버 에러 발생"));
        }

        //기기 상태 조회
        try {
            String devicesStateUrl ="https://goqual.io/openapi/devices/state";
            JSONArray deviceStates = (JSONArray) getApiCall(devicesStateUrl);

            //상태 정보 파싱용
            for(int i = 0; i< deviceStates.length(); i++){
                JSONObject deviceState = deviceStates.getJSONObject(i);
                System.out.println(deviceState.toString());
                String id = deviceState.getString("id");
                
                //해쉬 맵에서 plugId가 존재할 경우
                if(deviceStore.containsKey(id)){
                    JSONObject device =deviceStore.get(id);

                    //여러 정보중 deviceState만 파싱
                    JSONObject state= deviceState.getJSONObject("deviceState");

                    //전원, 전류, 전력, 전압 주입
                    device.put("power",state.optBoolean("power"))
                            .put("curCurrent",state.optInt("curCurrent"))
                            .put("curPower",state.optInt("curPower"))
                            .put("curVoltage",state.optInt("curVoltage"));
                }
            }
        }catch (Exception e){
            System.out.println("기기 상태 갱신중 에러 발생 : "+e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse("기기 상태 갱신중 서버 에러 발생"));
        }

        try {
            JSONArray mergedDeviceList = new JSONArray(deviceStore.values());
            return ResponseEntity.ok(mergedDeviceList.toString());
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse("서버 에러 발생"));
        }
    }

}
