package nsuaiot.service.Service;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.ApiResponse;
import nsuaiot.service.Entity.NotificationAdvice;
import nsuaiot.service.Repository.NotificationRespository;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIService {

    private final NotificationRespository notificationRespository;

    public ResponseEntity<?> notification(){
        List<NotificationAdvice> adviceList=notificationRespository.findAll();

        if(adviceList.isEmpty()){
            return ResponseEntity.status(204).body(new ApiResponse("알림이 없습니다!"));
        }
        JSONObject returnData =new JSONObject().put("sender","AI 에너지 절감 조언 봇");
        returnData.put("advice",adviceList);

        return ResponseEntity.ok().body(returnData.toString());
    }
}
