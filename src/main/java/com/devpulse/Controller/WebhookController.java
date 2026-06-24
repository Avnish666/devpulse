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

        Map<String, Object> repository =
                (Map<String, Object>) payload.get("repository");

        Map<String, Object> headCommit =
                (Map<String, Object>) payload.get("head_commit");

        Map<String, Object> pusher =
                (Map<String, Object>) payload.get("pusher");

        String repoName =
                repository.get("name").toString();

        String commitMessage =
                headCommit.get("message").toString();

        String pusherName =
                pusher.get("name").toString();

        messagingTemplate.convertAndSend(
                "/topic/activity",
                new ActivityUpdate(
                        "COMMIT",
                        pusherName +
                                " pushed to " +
                                repoName +
                                " : " +
                                commitMessage
                )
        );

        return ResponseEntity.ok("Received");
    }
}