package com.example.Jobportal.model;

import com.example.Jobportal.enums.NotifType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String message;
    private NotifType type;
    private Boolean isRead;
    private Long jobId;
    private LocalDateTime createdAt;
}