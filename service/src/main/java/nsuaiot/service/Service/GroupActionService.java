package nsuaiot.service.Service;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.Entity.GroupList;
import nsuaiot.service.Entity.GroupPlugManagement;
import nsuaiot.service.Repository.GroupListRepository;
import nsuaiot.service.Repository.GroupPlugManagementRepository;
import nsuaiot.service.Repository.PlugRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupActionService {

    private final RestTemplate restTemplate;
    private final String token = "ba4033b0-778d-4480-8c55-558bfa1a16dc";

    //API 호출 함수
    public ResponseEntity<String> postPlugControl(String plugId, String requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "https://goqual.io/openapi/control/" + plugId;
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            System.out.println("Response Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException e) {
            System.out.println(e.getStatusCode());
            System.out.println(e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("뭔가 에러 2");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("API 요청 중 오류 발생");
        }
    }

    private final GroupListRepository groupListRepository;
    private final GroupPlugManagementRepository groupPlugManagementRepository;
    private final PlugRepository plugRepository;

    public ResponseEntity<String> addAction(Long groupId, List<Map<String, String>> devices){
        if(!groupListRepository.existsById(groupId)){
            return ResponseEntity.status(404).body("그룹 id값이 올바르지 않습니다.");
        }

        List<GroupPlugManagement> saveList = new ArrayList<>();

        for(Map<String, String> device : devices){
            String plugId = device.get("plugId");
            String plugControl = device.get("action");
            GroupPlugManagement newAction=new GroupPlugManagement(groupId, plugId, plugControl);

            if(!plugRepository.findByPlugId(plugId).isPresent()){
                return ResponseEntity.status(404).body("플러그 "+plugId+"가 존재하지 않습니다!");
            }

            //같은 그룹 아이디에 같은 플러그 제어 있는지 확인해야함
            if(groupPlugManagementRepository.findByGroupIdAndPlugId(groupId, plugId).isPresent()){
                return ResponseEntity.status(400).body("해당 그룹에서 플러그 "+plugId+"의 동작이 이미 선언 되어있습니다!");
            }
            saveList.add(newAction);
        }
        groupPlugManagementRepository.saveAll(saveList);
        return ResponseEntity.ok().body("해당 그룹에 액션이 추가가 완료되었습니다.");
    }

    public ResponseEntity<String> checkAction(Long groupId){

        List<GroupPlugManagement> groupData = groupPlugManagementRepository.findByGroupId(groupId);

        if(!groupListRepository.existsById(groupId)){
            return ResponseEntity.status(404).body("그룹이 존재하지 않습니다!");
        }

        if(groupData.size()==0){
            return ResponseEntity.status(204).body(" ");
        }

        JSONArray groupDataArray =new JSONArray();

        for(int i=0;i<groupData.size();i++){
            GroupPlugManagement groupAction =groupData.get(i);
            JSONObject data =new JSONObject()
                            .put("groupId",groupAction.getGroupId())
                            .put("plugControl",groupAction.getAction())
                            .put("plugId",groupAction.getPlugId());
            groupDataArray.put(data);
        }

        return ResponseEntity.ok().body(groupDataArray.toString());
    }

    public ResponseEntity<String> runAction(Long groupId){
        if(!groupListRepository.existsById(groupId)){
            return ResponseEntity.status(404).body("그룹이 존재하지 않습니다!");
        }

        List<GroupPlugManagement> groupPlugManagements = groupPlugManagementRepository.findByGroupId(groupId);
        if(groupPlugManagements.size()==0){
            return ResponseEntity.ok().body("그룹에 등록된 액션이 없습니다");
        }
        System.out.println(groupPlugManagements);

        String url = "https://goqual.io/openapi/control/";
        String requestBody=new String();
        int successCount=0;
        int errorCount=0;

        try{
            for(int i=0; i<groupPlugManagements.size();i++){
                GroupPlugManagement action = groupPlugManagements.get(i);
                System.out.println("action = " + action);

                if(action.getAction().equals("on")){
                    requestBody="{\n" +
                            "    \"requirments\": {\n" +
                            "        \"power\": true\n" +
                            "    }\n" +
                            "}";
                }else if(action.getAction().equals("off")){
                    requestBody="\"{\\n\" +\n" +
                            "                    \"    \\\"requirments\\\": {\\n\" +\n" +
                            "                    \"        \\\"power\\\": false\\n\" +\n" +
                            "                    \"    }\\n\" +\n" +
                            "                    \"}\"";
                }else {
                    return ResponseEntity.status(400).body("잘못된 요청입니다.");
                }

                ResponseEntity<String> postResult = postPlugControl(action.getPlugId(),requestBody);
                if(postResult.getStatusCode().is2xxSuccessful()){
                    successCount+=1;
                }else {
                    errorCount+=1;
                }
            }
            return ResponseEntity.ok().body(successCount+"개의 기기가 작동 되었고, "+errorCount+"개의 기기가 작동 실패 했습니다.");
        }catch (Exception e){
            return ResponseEntity.status(500).body("서버 에러 발생");
        }


    }
}
