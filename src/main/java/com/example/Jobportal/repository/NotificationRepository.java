package com.example.Jobportal.repository;

import com.example.Jobportal.entity.NotificationEntity;
import com.example.Jobportal.entity.NotificationEntity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    // All notifications for a user, newest first (for notification bell/dropdown)
    List<NotificationEntity> findByRecipientIdOrderByCreatedAtDesc(Long userId);

    // Only unread notifications — used for the unread count badge in the UI
    List<NotificationEntity> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    // Count of unread notifications — lightweight query for the badge number
    Long countByRecipientIdAndIsReadFalse(Long userId);

    // Mark all notifications as read for a user in one UPDATE (no N+1 loop)
    @Modifying
    @Transactional
    @Query("UPDATE NotificationEntity n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.recipient.id = :userId AND n.isRead = false")
    void markAllAsRead(@Param("userId") Long userId);

    // Check if an AI_MATCH_ALERT was already sent for this application
    // Prevents duplicate alerts if the AI re-scores the same application
    boolean existsByRecipientIdAndTypeAndReferenceId(Long userId, NotificationType type, Long referenceId);
}