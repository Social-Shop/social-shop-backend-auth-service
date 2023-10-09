package com.socialshop.backend.authservice.mq.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailSendEvent {
    private String email;
    private String content;
}
