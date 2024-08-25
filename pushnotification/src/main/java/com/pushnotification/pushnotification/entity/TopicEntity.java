package com.pushnotification.pushnotification.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Where;


import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "topic")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Filter(name = "activeFilter", condition = ":isActive = is_active")
public class TopicEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    String description;

    @ManyToMany
    @Where(clause = "is_active = true")
    Set<UserEntity> users = new HashSet<>();
}
