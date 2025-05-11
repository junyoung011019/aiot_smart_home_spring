package nsuaiot.service.Service;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.ApiResponse;
import nsuaiot.service.DTO.MainPlugUsageDTO;
import nsuaiot.service.Entity.Plug;
import nsuaiot.service.Entity.Usage;
import nsuaiot.service.Repository.PlugRepository;
import nsuaiot.service.Repository.UsageRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MainService {

    private final PlugRepository plugRepository;
    private final UsageRepository usageRepository;

    public ResponseEntity<?> main(String userId){
        
        //이번 달 날짜 정하기
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        Date start = Date.from(startDate.atZone(ZoneId.of("Asia/Seoul")).toInstant());
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);
        Date end = Date.from(endDate.atZone(ZoneId.of("Asia/Seoul")).toInstant());

        //플러그 별로 데이터 저장소
        Map<MainPlugUsageDTO,Double> plugUsage = new HashMap<>();
        Optional<List<Plug>> plugList = plugRepository.findByOwnerId(userId);
        if(plugList.isEmpty()){
            return ResponseEntity.status(204).body(new ApiResponse("저장된 플러그가 없습니다."));
        }

        //각 플러그 꺼내서 사용량 조회
        for(Plug plug : plugList.get()){
            String plugId = plug.getPlugId();
            List<Usage> usageList=usageRepository.findByPlugIdAndUsageDateBetween(plugId,start,end);
            MainPlugUsageDTO mainDTO =new MainPlugUsageDTO(plugId,plug.getPlugName());
            
            //플러그 데이터 저장소에 저장 (키 값 없으면 새로 생성)
            for(Usage usage: usageList){
                if(!plugUsage.containsKey(mainDTO)){
                    plugUsage.put(mainDTO,0.0);
                }
                Double saveData = plugUsage.get(mainDTO)+usage.getUsageValue();
                plugUsage.put(mainDTO,saveData);
            }
        }


        //포맷팅한 날짜 저장
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        JSONObject returnData = new JSONObject()
                .put("startDate",formatter.format(start))
                .put("endDate",formatter.format(end));

        //각 플러그 별 플러그 아이디, 이름, 전력 값 저장
        JSONArray mainUsage = new JSONArray();

        for(Map.Entry<MainPlugUsageDTO, Double> entry : plugUsage.entrySet()){
            MainPlugUsageDTO dto = entry.getKey();
            JSONObject plugJson = new JSONObject()
                    .put("plugId",dto.getPlugId())
                    .put("plugName",dto.getPlugName())
                    .put("usage",entry.getValue());
            mainUsage.put(plugJson);
        }

        if(mainUsage.isEmpty()){
            return ResponseEntity.status(204).body(new ApiResponse("전력 사용량이 없습니다."));
        }
        returnData.put("plugTotalUsage",mainUsage);

        return ResponseEntity.ok().body(returnData.toString());
    }
}
