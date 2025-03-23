package nsuaiot.service.Controller;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/app")
public class AppController {

    @GetMapping("/push")
    public ResponseEntity<?> checkPlug() throws FirebaseMessagingException {
        // This registration token comes from the client FCM SDKs.
        String registrationToken = "cm9aHJKiQTiGUHuqFvWAMh:APA91bGv3quWLqxRci20JQEAfRlVD2lKCS9tVNMJmPZy4aJEiJm44Wd3qGhDN4FlNEnFF7xqw97GELtlpe6fJPdHqGBo194Gf-wVV95ungri6DX-mq1MpLA";

        for(int i =0; i<1000;i++){
            // See documentation on defining a message payload.
            Message message = Message.builder()
                    .setToken(registrationToken)
                    .setNotification(
                            Notification.builder()
                                    .setTitle("알림 옴")
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

        //System.out.println("✅ Successfully sent message: " + response);

        return ResponseEntity.ok().body("성공");
    }
}
