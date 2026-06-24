package com.devpulse.Controller;

import com.devpulse.Dto.ActivityUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebhookController(
            SimpMessagingTemplate messagingTemplate
    ) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/github")
    public ResponseEntity<String> githubWebhook(
            @RequestBody Map<String, Object> payload
    ) {

        messagingTemplate.convertAndSend(
                "/topic/activity",
                new ActivityUpdate(
                        "COMMIT",
                        "New GitHub event received!"
                )
        );

        return ResponseEntity.ok("Received");
    }
}