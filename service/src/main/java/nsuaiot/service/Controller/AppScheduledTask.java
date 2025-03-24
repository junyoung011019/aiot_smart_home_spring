package nsuaiot.service.Controller;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import nsuaiot.service.Entity.User;
import nsuaiot.service.Repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@Component
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AppScheduledTask {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 30 * * * ?")
    public void sendPush() throws FirebaseMessagingException {
        LocalTime localTime = LocalTime.now();
        System.out.println("알림 전송 시간 : "+localTime);
        List<User> user = userRepository.findAll();
        for(int i =0;i<user.size();i++){
            User getUser = user.get(i);
            if(getUser.getFcmKey() == null){
                continue;
            }
            String fcmKey = getUser.getFcmKey();

            // This registration token comes from the client FCM SDKs.
            //String fcmKey = "cm9aHJKiQTiGUHuqFvWAMh:APA91bGv3quWLqxRci20JQEAfRlVD2lKCS9tVNMJmPZy4aJEiJm44Wd3qGhDN4FlNEnFF7xqw97GELtlpe6fJPdHqGBo194Gf-wVV95ungri6DX-mq1MpLA";

            // See documentation on defining a message payload.
            Message message = Message.builder()
                    .setToken(fcmKey)
                    .setNotification(
                            Notification.builder()
                                    .setTitle("알림 옴")
                                    .setBody("알림 시간 : "+localTime)
                                    .setBody("안준섭은 게이다")
                                    .setImage("https://picsum.photos/100/100")
                                    .build()
                    )
                    .putData("type", "chat")
                    .build();

            // Send a message to the device corresponding to the provided
            // registration token.
            String response = FirebaseMessaging.getInstance().send(message);
        }


    }
}
