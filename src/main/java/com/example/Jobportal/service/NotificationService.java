package com.example.Jobportal.service;

import com.example.Jobportal.model.NotificationResponse;
import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getMyNotifications(Long userId);
    Long getUnreadCount(Long userId);
    void markAllRead(Long userId);
    void sendJobMatchNotifications(Long jobId);
}