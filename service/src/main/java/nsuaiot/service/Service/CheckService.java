package nsuaiot.service.Service;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckService {
    private final RestTemplate restTemplate;

    //API 호출 함수
    private Object getApiCall(String url) throws JSONException {
        String token = "ba4033b0-778d-4480-8c55-558bfa1a16dc";
        HttpHeaders headers=new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> requestEntity1 = new HttpEntity<>(headers);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> apiResponse = restTemplate.exchange(url, HttpMethod.GET, requestEntity1, String.class);
        String jsonDate=apiResponse.getBody();
        if (jsonDate.trim().startsWith("[")) {
            return new JSONArray(jsonDate);
        } else {
            return new JSONObject(jsonDate);
        }
    }

    //기기 상태 리턴 함수
    public ResponseEntity<String> checkPlugList(){
        Map<String, JSONObject> deviceStore = new HashMap<>();

        //기기 정보 조회
        try {
            String devicesUrl = "https://goqual.io/openapi/devices";
            JSONArray deviceInfo = (JSONArray) getApiCall(devicesUrl);

            for (int i = 0; i < deviceInfo.length(); i++) {
                JSONObject obj = deviceInfo.getJSONObject(i);
                String deviceId = obj.optString("id", "unknown");
                JSONObject deviceDate = new JSONObject()
                        .put("id", obj.optString("id"))
                        .put("name", obj.optString("name"))
                        .put("online", obj.optBoolean("online"));
                deviceStore.put(deviceId, deviceDate);
            }
        }catch (Exception e) {
            System.out.println("기기 정보 갱신중 에러 발생 : "+e.getMessage());
            return ResponseEntity.status(500).body("기기 정보 갱신중 서버 에러 발생");
        }

        //기기 상태 조회
        try {
            String devicesStateUrl ="https://goqual.io/openapi/devices/state";
            JSONArray deviceState = (JSONArray) getApiCall(devicesStateUrl);

            //상태 정보 파싱용
            for(int i=0; i<deviceState.length();i++){
                JSONObject obj=deviceState.getJSONObject(i);
                String id=obj.getString("id");
                if(deviceStore.containsKey(id)){
                    JSONObject device =deviceStore.get(id);
                    JSONObject state=obj.getJSONObject("deviceState");
                    device.put("power",state.optBoolean("power"))
                            .put("curCurrent",state.optInt("curCurrent"))
                            .put("curPower",state.optInt("curPower"))
                            .put("curVoltage",state.optInt("curVoltage"));
                }
            }
        }catch (Exception e){
            System.out.println("기기 상태 갱신중 에러 발생 : "+e.getMessage());
            return ResponseEntity.status(500).body("기기 상태 갱신중 서버 에러 발생");
        }

        try {
            JSONArray mergedDeviceList = new JSONArray(deviceStore.values());
            return ResponseEntity.ok(mergedDeviceList.toString());
        }catch (Exception e){
            return ResponseEntity.status(500).body("서버 에러 발생");
        }
    }


    public ResponseEntity<String> checkPlug(String plugId) {

        JSONObject findDevice = new JSONObject();
        //기기 정보 확인
        try {
            String devicesUrl = "https://goqual.io/openapi/devices";
            JSONArray devicesArray = (JSONArray) getApiCall(devicesUrl);
            for(int i=0; i<devicesArray.length();i++){
                JSONObject obj=devicesArray.getJSONObject(i);
                if(obj.getString("id").equals(plugId)){
                    findDevice.put("id",obj.optString("id"))
                            .put("name",obj.optString("name"))
                            .put("online",obj.optString("online"));
                    break;
                }
            }
        }catch (Exception e){
            System.out.println("기기 정보 확인중 에러 발생 : "+e.getMessage());
            return ResponseEntity.status(500).body("기기 정보 확인중 서버 에러 발생");
        }
        
        //기기 상태 확인
        try {
            String deviceUrl = "https://goqual.io/openapi/device/"+plugId;
            JSONObject deviceState = (JSONObject) getApiCall(deviceUrl);
            JSONObject state = deviceState.getJSONObject("deviceState");
            findDevice.put("power",state.optBoolean("power"))
                    .put("curCurrent",state.optInt("curCurrent"))
                    .put("curPower",state.optInt("curPower"))
                    .put("curVoltage",state.optInt("curVoltage"));
            System.out.println("찾은 기기 : " + findDevice);

        }catch (Exception e){
            System.out.println("기기 상태 확인중 에러 발생 : "+e.getMessage());
            return ResponseEntity.status(500).body("기기 상태 확인중 서버 에러 발생");
        }

        return ResponseEntity.ok(findDevice.toString());

    }
}
