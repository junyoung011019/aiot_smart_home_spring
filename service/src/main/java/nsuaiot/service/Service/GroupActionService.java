package nsuaiot.service.Service;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.ApiResponse;
import nsuaiot.service.Entity.GroupList;
import nsuaiot.service.Entity.GroupPlugManagement;
import nsuaiot.service.Entity.Plug;
import nsuaiot.service.Repository.GroupListRepository;
import nsuaiot.service.Repository.GroupPlugManagementRepository;
import nsuaiot.service.Repository.PlugRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GroupActionService {

    private final RestTemplate restTemplate;
    private final String token = "ba4033b0-778d-4480-8c55-558bfa1a16dc";

    //API 호출 함수
    public ResponseEntity<?> postPlugControl(String plugId, String requestBody) {
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("API 요청 중 오류 발생"));
        }
    }

    private final GroupListRepository groupListRepository;
    private final GroupPlugManagementRepository groupPlugManagementRepository;
    private final PlugRepository plugRepository;

    //그룹 액션 추가/수정/삭제
    public ResponseEntity<?> editAction(Long groupId, List<Map<String, String>> updateDataSet, String userId){
        //그룹 존재 여부 확인
        Optional<GroupList> editGroup = groupListRepository.findByGroupIdAndOwnerId(groupId,userId);
        if(editGroup.isEmpty()){
            return ResponseEntity.status(404).body(new ApiResponse("그룹 id값이 올바르지 않습니다."));
        }

        //존재 데이터 해쉬맵으로 변환
        Optional<List<GroupPlugManagement>> saveList = groupPlugManagementRepository.findByGroupId(groupId);
        Map<String, GroupPlugManagement> existingMap = new HashMap<>();
        for (GroupPlugManagement item : saveList.get()) {
            existingMap.put(item.getPlugId(), item);
        }

        //사용한 플러그 아이디 저장
        Set<String> savePlugId = new HashSet<>();

        //갱신 데이터 저장
        List<GroupPlugManagement> updatedList = new ArrayList<>();
        for (Map<String, String> updataData : updateDataSet) {
            String plugId = updataData.get("plugId");
            savePlugId.add(plugId);
            String plugControl = updataData.get("action");

            //플러그 존재 여부 확인
            if (!plugRepository.existsByPlugId(plugId)) {
                return ResponseEntity.status(404).body(new ApiResponse("플러그 " + plugId + "가 존재하지 않습니다!"));
            }
            
            //플러그에 대한 액션 값있으면 액션값 덮어쓰기
            if (existingMap.containsKey(plugId)) {
                existingMap.get(plugId).setAction(plugControl);
            } else {
                updatedList.add(new GroupPlugManagement(groupId, plugId, plugControl));
            }
        }

        //삭제 데이터 판별
        List<GroupPlugManagement> deleteList = new ArrayList<>();
        for (GroupPlugManagement item : saveList.get()) {
            if (!savePlugId.contains(item.getPlugId())) {
                deleteList.add(item);
            }
        }

        //기존 데이터 저장
        groupPlugManagementRepository.saveAll(new ArrayList<>(existingMap.values()));
        //새로운 데이터 저장
        groupPlugManagementRepository.saveAll(updatedList);
        //사라진 데이터 삭제
        if (!deleteList.isEmpty()) {
            groupPlugManagementRepository.deleteAll(deleteList);
        }

        return ResponseEntity.ok().body(new ApiResponse("해당 그룹에 액션이 추가 / 수정이 완료되었습니다."));
    }


    //그룹 액션 조회
    public ResponseEntity<?> checkAction(Long groupId, String userId){

        //그룹 존재 여부 확인
        Optional<GroupList> groupList = groupListRepository.findByGroupIdAndOwnerId(groupId,userId);
        if(groupList.isEmpty()){
            return ResponseEntity.status(404).body(new ApiResponse("그룹이 존재하지 않습니다!"));
        }

        //그룹 액션 데이터 조회
        Optional<List<GroupPlugManagement>> groupData = groupPlugManagementRepository.findByGroupId(groupId);
        if(groupData.isEmpty()){
            return ResponseEntity.status(204).body(new ApiResponse("그룹에 조회된 액션이 없습니다."));
        }

        //그룹 명과 그룹 아이디 선 저장
        JSONArray groupDataArray = new JSONArray();
        JSONObject groupObject = new JSONObject()
                .put("groupName", groupList.get().getGroupName())
                .put("groupId", groupList.get().getGroupId());
        groupDataArray.put(groupObject);

        //그룹에 있는 그룹 액션 plugAction 배열에 저장
        JSONArray plugArray = new JSONArray();
        for (GroupPlugManagement groupAction : groupData.get()) {
            String plugId= groupAction.getPlugId();
            Optional<Plug> plugData=plugRepository.findByPlugIdAndOwnerId(plugId,userId);
            JSONObject plugAction = new JSONObject()
                    .put("plugName",plugData.get().plugName)
                    .put("plugId", plugId)
                    .put("plugControl", groupAction.getAction());
            plugArray.put(plugAction);
        }
        //plugArray 그룹 액션 리스트에 저장
        groupObject.put("plug", plugArray);

        return ResponseEntity.ok().body(groupDataArray.toString());
    }

    //그룹 액션 실행
    public ResponseEntity<?> runAction(Long groupId, String userId){
        
        //그룹 존재 여부 확인
        if(groupListRepository.findByGroupIdAndOwnerId(groupId,userId).isEmpty()){
            return ResponseEntity.status(404).body(new ApiResponse("그룹이 존재하지 않습니다!"));
        }

        //그룹에 선언된 동작이 없을 경우
        Optional<List<GroupPlugManagement>> groupPlugManagements = groupPlugManagementRepository.findByGroupId(groupId);
        if(groupPlugManagements.isEmpty()){
            return ResponseEntity.status(204).body(new ApiResponse("실행할 동작이 존재하지 않습니다!"));
        }
        
        // (성공/실패) 기기명 저장
        JSONArray successArray = new JSONArray();
        JSONArray errorArray = new JSONArray();

        try{
            for(int i=0; i<groupPlugManagements.get().size();i++){
                GroupPlugManagement action = groupPlugManagements.get().get(i);

                //헤이홈 서버로 API 요청
                String power = String.valueOf("on".equals(action));
                JSONObject requestBody = new JSONObject().put("requirments",new JSONObject().put("power",power));
                ResponseEntity<?> response = postPlugControl(action.getPlugId(), requestBody.toString());

                //응답코드로 (성공/실패) 여부 확인 후 배열에 저장
                if(response.getStatusCode().is2xxSuccessful()){
                    String controlPlugId = action.getPlugId();
                    String controlPlugName = plugRepository.findByPlugIdAndOwnerId(controlPlugId, userId).get().plugName;
                    successArray.put(controlPlugName);
                }else {
                    String controlPlugId = action.getPlugId();
                    String controlPlugName = plugRepository.findByPlugIdAndOwnerId(controlPlugId, userId).get().plugName;
                    errorArray.put(controlPlugName);
                }
            }
            
            //응답 메세지 정리 (성공 횟수, 성공 리스트, 실패 횟수, 실패 리스트 반환)
            JSONObject returnData = new JSONObject();
            returnData.put("successCount",successArray.length())
                    .put("successArray",successArray)
                    .put("errorCount",errorArray.length())
                    .put("errorArray",errorArray);
            return ResponseEntity.ok().body(returnData.toString());
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse("서버 에러 발생"));
        }


    }
}
