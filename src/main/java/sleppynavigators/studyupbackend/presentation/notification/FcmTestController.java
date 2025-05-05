package sleppynavigators.studyupbackend.presentation.notification;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FcmTestController {

    @GetMapping("/fcm-test")
    public String fcmTestPage() {
        return "fcm";
    }
}
