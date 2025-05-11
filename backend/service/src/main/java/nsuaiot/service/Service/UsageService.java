package nsuaiot.service.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.ApiResponse;
import nsuaiot.service.Entity.Plug;
import nsuaiot.service.Entity.Usage;
import nsuaiot.service.Repository.PlugRepository;
import nsuaiot.service.Repository.UsageRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import java.util.*;
import java.time.LocalDateTime;
import java.time.ZoneId;


@Service
@RequiredArgsConstructor
public class UsageService {

    private final RestTemplate restTemplate;
    private final PlugRepository plugRepository;
    private final UsageRepository usageRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JSONArray usageDataFormatting(List<Usage> usageDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        JSONArray returnData = new JSONArray();
        for (int i = 0; i < usageDate.size(); i++) {
            Usage usage = usageDate.get(i);
            JSONObject object = new JSONObject()
                    .put("usage", usage.getUsageValue())
                    .put("date", formatter.format(usage.getUsageDate()));
            returnData.put(object);
        }
        return returnData;
    }


    //주간 단일 기기 사용량 조회
    public ResponseEntity<?> weekUsagePlug(String plugId, String userId) throws IOException, InterruptedException {

        //플러그 ID 유무 확인
        if (plugRepository.findByPlugIdAndOwnerId(plugId, userId).isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse("플러그 ID가 올바르지 않습니다."));
        }

        //현재로부터 7일 확인
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        Date start = Date.from(startDate.atZone(ZoneId.of("Asia/Seoul")).toInstant());
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);
        Date end = Date.from(endDate.atZone(ZoneId.of("Asia/Seoul")).toInstant());

        List<Usage> usageData = usageRepository.findByPlugIdAndUsageDateBetween(plugId, start, end);

        if (usageData.isEmpty()) {
            return ResponseEntity.status(204).body(new ApiResponse("플러그의 사용량이 없습니다"));
        }



        JSONObject returnData = new JSONObject()
                .put("usage",usageDataFormatting(usageData).toString());
        return ResponseEntity.ok().body(returnData.toString());
    }


    //주간 단체 기기 사용량 조회 : 각 기기 별의 전력량 합 (이번주 사용량 ~7일)
    public ResponseEntity<?> weekUsageGroupPlug(String userId) throws IOException, InterruptedException {

        //현재로부터 7일 확인
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        Date start = Date.from(startDate.atZone(ZoneId.of("Asia/Seoul")).toInstant());
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);
        Date end = Date.from(endDate.atZone(ZoneId.of("Asia/Seoul")).toInstant());

        //userId에 해당하는 플러그 정보 가져오기
        Optional<List<Plug>> optionalPlugList = plugRepository.findByOwnerId(userId);
        if (optionalPlugList.isEmpty()) {
            return ResponseEntity.status(204).body(new ApiResponse("조회할 기기가 없습니다."));
        }

        List<Plug> plugList = optionalPlugList.get();
        JSONObject returnData = new JSONObject().put("message", start + "부터 " + end + "까지의 전력 데이터");
        JSONArray totalPlugData = new JSONArray();
        for (Plug plug : plugList) {
            List<Usage> usageData = usageRepository.findByPlugIdAndUsageDateBetween(plug.getPlugId(), start, end);

            double total_usage = 0.0;
            for (Usage usage : usageData) {
                total_usage += usage.getUsageValue();
            }
            JSONObject plugData = new JSONObject()
                    .put("plugName", plug.getPlugName())
                    .put("plugId", plug.getPlugId())
                    .put("usage", total_usage);

            totalPlugData.put(plugData);
        }
        returnData.put("plugUsageData", totalPlugData)
                .put("advice",aiAdvicePython("device"));

        if (returnData.isEmpty()) {
            return ResponseEntity.status(204).body(new ApiResponse("플러그의 사용량이 없습니다"));
        }

        return ResponseEntity.ok(returnData.toString());
    }


    //주간 사용량 조회 (7일간 매일 각각 얼마나 썼는지)
    public ResponseEntity<?> weekUsage(String userId) throws IOException, InterruptedException {

        //현재로부터 7일 확인
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        Date start = Date.from(startDate.atZone(ZoneId.of("Asia/Seoul")).toInstant());
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);
        Date end = Date.from(endDate.atZone(ZoneId.of("Asia/Seoul")).toInstant());

        //(토큰) 사용자에 해당하는 플러그만 가져옴
        Optional<List<Plug>> optionalPlugList = plugRepository.findByOwnerId(userId);
        if (optionalPlugList.isEmpty()) {
            return ResponseEntity.status(204).body(new ApiResponse("조회할 기기가 없습니다."));
        }

        Map<String, Double> weekData = new TreeMap<>();

        for (Plug plug : optionalPlugList.get()) {
            List<Usage> usageData = usageRepository.findByPlugIdAndUsageDateBetween(plug.getPlugId(), start, end);
            //한 기기가 날마다 얼마나 썼는지
            for (Usage usage : usageData) {

                //날짜 Date -> String 변환
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                String date = formatter.format(usage.getUsageDate());

                //맵안에 날짜 데이터 있는지 판단 (없으면 추가)
                if (!weekData.containsKey(date)) {
                    weekData.put(date, 0.0);
                }
                double newData = weekData.get(date) + usage.getUsageValue();
                weekData.put(date, newData);
            }
        }

        JSONObject returnData = new JSONObject()
                .put("message", start + "부터 " + end + "까지의 전력 데이터")
                .put("weekUsage", new JSONObject(weekData))
                .put("advice",aiAdvicePython("daily"));

        return ResponseEntity.ok(returnData.toString());
    }


    //월간 사용량 조회 (6개월 간의 월간 데이터)
    public ResponseEntity<?> monthUsage(String userId) throws IOException, InterruptedException {
        //현재로부터 7일 확인
        LocalDateTime startDate = LocalDateTime.of(2024, 9, 1, 0, 0);
        Date start = Date.from(startDate.atZone(ZoneId.of("Asia/Seoul")).toInstant());
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);
        Date end = Date.from(endDate.atZone(ZoneId.of("Asia/Seoul")).toInstant());

        //(토큰) 사용자에 해당하는 플러그만 가져옴
        Optional<List<Plug>> optionalPlugList = plugRepository.findByOwnerId(userId);
        if (optionalPlugList.isEmpty()) {
            return ResponseEntity.status(204).body(new ApiResponse("조회할 기기가 없습니다."));
        }

        Map<String, Double> monthData = new TreeMap<>();

        for (Plug plug : optionalPlugList.get()) {
            List<Usage> usageData = usageRepository.findByPlugIdAndUsageDateBetween(plug.getPlugId(), start, end);
            //한 기기가 날마다 얼마나 썼는지
            for (Usage usage : usageData) {

                //날짜 Date -> String 변환
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
                formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                String date = formatter.format(usage.getUsageDate());

                //맵안에 날짜 데이터 있는지 판단 (없으면 추가)
                if (!monthData.containsKey(date)) {
                    monthData.put(date, 0.0);
                }
                double newData = monthData.get(date) + usage.getUsageValue();
                monthData.put(date, newData);
            }
        }
        JSONObject returnData = new JSONObject()
                .put("message", start + "부터 " + end + "까지의 전력 데이터")
                .put("monthData", new JSONObject(monthData))
                .put("advice",aiAdvicePython("monthly"));

        return ResponseEntity.ok(returnData.toString());
    }

    //파이썬 코드
    @Value("${python.path}")
    private String PYTHON_PATH;

    @Value("${python.code_one_line_path}")
    private String PYTHON_CODE_PATH;

    //파이썬 코드 실행
    public String aiAdvicePython(String type) throws IOException, InterruptedException {
        //AI 조언 봇 실행
        Process process = null;
        try {
            ProcessBuilder pb = new ProcessBuilder(PYTHON_PATH, "main.py",type);
            pb.directory(new File(PYTHON_CODE_PATH));
            process = pb.start();
        } catch (Exception e) {
            System.out.println("실행 오류 : "+e.getMessage());
            return "실행 오류";
        }

        // 결과 저장용 StringBuilder
        StringBuilder output = new StringBuilder();

        // 결과 읽기
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
        );
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = process.waitFor();

        String result = output.toString().trim();

        BufferedReader errReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8)
        );
        StringBuilder errOutput = new StringBuilder();
        String errLine;
        while ((errLine = errReader.readLine()) != null) {
            errOutput.append(errLine).append("\n");
        }

        if (errOutput.length() > 0) {
            System.out.println("❗ Python stderr: " + errOutput.toString());
        }

        if (result.isEmpty()) {
            return null;
        }
        return result;
    }
}
