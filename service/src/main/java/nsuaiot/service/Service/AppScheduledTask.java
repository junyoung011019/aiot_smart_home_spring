package nsuaiot.service.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import nsuaiot.service.Entity.User;
import nsuaiot.service.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AppScheduledTask {

    private final UserRepository userRepository;

    @Value("${python.path}") // 환경 변수에서 API 키 로드
    private String PYTHON_PATH;

    @Value("${python.code_notification_path}")
    private String PYTHON_CODE_PATH;

    //파이썬 코드 실행
    public String aiAdvicePython() throws IOException, InterruptedException {
        //AI 조언 봇 실행
        Process process = null;
        try {
            ProcessBuilder pb = new ProcessBuilder(PYTHON_PATH, "gptadvice.py");
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
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    //30분마다 조언 전달
    @Scheduled(cron = "0 30 * * * ?")
    public void sendPush() throws FirebaseMessagingException, IOException, InterruptedException {
        LocalDateTime localTime = LocalDateTime.now();
        System.out.println("알림 전송 시간 : " + localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        //일단 어드민 유저만 fcm 발송
        Optional<User> adminUser = userRepository.findByUserId("admin");
        String fcmKey = adminUser.get().getFcmKey();

        String aiAdvice = aiAdvicePython();
        //만약 메세지 없을때
        if(aiAdvice==null){
            System.out.println(localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + " 불 필요한 기기 없음");
            return;
        }

        // See documentation on defining a message payload.
        Message message = Message.builder()
                .setToken(fcmKey)
                .setNotification(
                        Notification.builder()
                                .setTitle("전력 감소 조언 도우미")
                                .setBody(aiAdvice)
                                .build()
                )
                .putData("type", "chat")
                .build();
        
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println(localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "에 메세지 발송됨");
        } catch (FirebaseMessagingException e) {
            System.out.println(e.getMessage());
            System.out.println(localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "에 메세지 발송 시도됨");
        }
    }

}
