package com.pushnotification.pushnotification.repository;

import com.pushnotification.pushnotification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    @Query(value = "SELECT DISTINCT pn.* " +
            "FROM push_notification pn " +
            "LEFT JOIN users u ON pn.user_id = u.id " +
            "LEFT JOIN subscribes s ON u.id = s.user_id " +
            "LEFT JOIN topics t ON s.topic_id = t.id " +
            "WHERE u.cif = :cif " +
            "AND (pn.user_id = u.id OR pn.topic_id = t.id) " +
            "AND pn.is_active = TRUE",
            nativeQuery = true)
    List<NotificationEntity> findNotificationsByUserCif(@Param("cif") String cif);
}
