package com.pushnotification.pushnotification.entity;


import com.pushnotification.pushnotification.constant.PlatformLanguages;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLRestriction;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "topics")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLRestriction("is_active = true")
@ToString
public class TopicEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    String description;

    @Enumerated(EnumType.STRING)
    PlatformLanguages language = PlatformLanguages.AZ;

    @ManyToMany
    @Filter(name = "activeFilter", condition = ":isActive = is_active")
    Set<UserEntity> users = new HashSet<>();

    @ManyToMany
    @Filter(name = "activeFilter", condition = ":isActive = is_active")
    Set<NotificationEntity> notifications = new HashSet<>();

    @PrePersist
    public void prePersist() {
        this.name = this.name.toUpperCase();
    }
}
