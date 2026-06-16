package com.example.Jobportal.repository;

import com.example.Jobportal.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<NotificationEntity> findByUserIdAndIsRead(Long userId, Boolean isRead);
    Long countByUserIdAndIsRead(Long userId, Boolean isRead);
}