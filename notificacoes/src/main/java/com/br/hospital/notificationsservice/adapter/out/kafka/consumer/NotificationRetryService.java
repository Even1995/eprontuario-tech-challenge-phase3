package com.br.hospital.notificationsservice.adapter.out.kafka.consumer;

import com.br.hospital.notificationsservice.adapter.out.jpa.entity.NotificationJpaEntity;
import com.br.hospital.notificationsservice.adapter.out.jpa.repository.NotificationJpaRepository;
import com.br.hospital.notificationsservice.domain.entity.NotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationRetryService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationRetryService.class);
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private final NotificationJpaRepository notificationJpaRepository;

    public NotificationRetryService(NotificationJpaRepository notificationJpaRepository) {
        this.notificationJpaRepository = notificationJpaRepository;
    }

    @Scheduled(fixedDelayString = "${kafka.retry-schedule:60000}")
    @Transactional
    public void retryFailedNotifications() {
        try {
            logger.info("Starting retry process for failed notifications");
            
            List<NotificationJpaEntity> pendingNotifications = 
                    notificationJpaRepository.findByStatusAndAttemptCountLessThan(
                            NotificationStatus.PENDING, MAX_RETRY_ATTEMPTS);
            
            for (NotificationJpaEntity notification : pendingNotifications) {
                retryNotification(notification);
            }
            
            logger.info("Retry process completed. Processed {} notifications", pendingNotifications.size());
        } catch (Exception e) {
            logger.error("Error during retry process", e);
        }
    }

    private void retryNotification(NotificationJpaEntity notification) {
        try {
            // Simulate notification sending
            notification.setAttemptCount(notification.getAttemptCount() + 1);
            notification.setUpdatedAt(LocalDateTime.now());

            // In a real scenario, this would send the notification via email, SMS, etc.
            logger.info("Retrying notification - ID: {}, Attempt: {}", 
                    notification.getId(), notification.getAttemptCount());

            if (notification.getAttemptCount() >= MAX_RETRY_ATTEMPTS) {
                notification.setStatus(NotificationStatus.FAILED);
                notification.setLastError("Max retry attempts exceeded");
                logger.warn("Notification {} marked as FAILED after {} attempts", 
                        notification.getId(), MAX_RETRY_ATTEMPTS);
            } else {
                // Simulate successful send
                notification.setStatus(NotificationStatus.SENT);
                notification.setLastError(null);
                logger.info("Notification {} sent successfully", notification.getId());
            }

            notificationJpaRepository.save(notification);
        } catch (Exception e) {
            notification.setAttemptCount(notification.getAttemptCount() + 1);
            notification.setLastError(e.getMessage());
            notification.setUpdatedAt(LocalDateTime.now());
            
            if (notification.getAttemptCount() >= MAX_RETRY_ATTEMPTS) {
                notification.setStatus(NotificationStatus.FAILED);
            }
            
            notificationJpaRepository.save(notification);
            logger.error("Error retrying notification {}", notification.getId(), e);
        }
    }

}
