package com.pushnotification.pushnotification.repository;

import com.pushnotification.pushnotification.entity.TopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TopicRepository extends JpaRepository<TopicEntity, Long> {
    boolean existsByName(String name);
    Set<TopicEntity> findAllByNameIn(List<String> names);
    Optional<TopicEntity> findByName(String name);

}
