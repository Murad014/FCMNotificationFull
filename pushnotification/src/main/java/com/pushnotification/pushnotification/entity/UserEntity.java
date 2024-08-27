package com.pushnotification.pushnotification.entity;


import com.pushnotification.pushnotification.constant.Platform;
import com.pushnotification.pushnotification.constant.PlatformLanguages;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table(name = "users")
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLRestriction("is_active = true")
public class UserEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String cif;

    @ManyToMany
    @JoinTable(
            name = "users_topics",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    Set<TopicEntity> topics = new HashSet<>();

    String uuid = UUID.randomUUID().toString();
    String token;
    @Enumerated(EnumType.STRING)
    Platform platform;

    @Column(name = "platform_language", nullable = false)
    @Enumerated(EnumType.STRING)
    PlatformLanguages platformLanguage;
}
