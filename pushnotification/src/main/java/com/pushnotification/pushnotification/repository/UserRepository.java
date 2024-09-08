package com.pushnotification.pushnotification.repository;


import com.pushnotification.pushnotification.entity.NotificationEntity;
import com.pushnotification.pushnotification.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByCif(String cif);
    Optional<UserEntity> findByToken(String token);
    List<UserEntity> findAllByCifIn(List<String> cifs);
    List<UserEntity> findAllByTopics_NameIn(List<String> topics);

    boolean existsByCif(String cif);
    boolean existsByToken(String token);
    boolean existsByUuid(String uuid);
}
