package com.pushnotification.pushnotification.repository;

import com.pushnotification.pushnotification.entity.NotificationEntity;
import com.pushnotification.pushnotification.entity.TopicEntity;
import com.pushnotification.pushnotification.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query(value = "SELECT DISTINCT n.* " +
            "FROM users u " +
            "JOIN users_topics ut ON u.id = ut.user_id " +
            "JOIN topics t ON ut.topic_id = t.id " +
            "LEFT JOIN notifications_topics nt ON nt.topic_id = t.id " +
            "LEFT JOIN notification n ON nt.notification_id = n.id " +
            "WHERE u.cif = :cif ",
            nativeQuery = true)
    List<NotificationEntity> findActiveNotificationsByCif(@Param("cif") String cif);

}
