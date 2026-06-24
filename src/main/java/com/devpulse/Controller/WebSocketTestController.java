package com.devpulse.Controller;

import com.devpulse.Dto.ActivityUpdate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketTestController {

    @MessageMapping("/hello")
    @SendTo("/topic/activity")
    public ActivityUpdate sendMessage(
            String message) {

        return new ActivityUpdate(
                "TEST",
                "WebSocket Working: " + message
        );
    }
}
