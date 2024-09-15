package com.pushnotification.pushnotification.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "notification")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLRestriction("is_active = true")
@ToString
public class NotificationEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "title")
    String title;

    @Column(name = "body")
    String body;

    @Column(name = "image_url")
    String imageUrl;

    @Column(name = "icon_url")
    String iconUrl;

    @Column(name = "subtitle")
    String subtitle;

    @Column(name = "sent_to_mq")
    boolean sentToMq;

    @Column(name = "sent_to_fcm")
    boolean sentToFcm;

    @ManyToMany
    @JoinTable(
            name = "notifications_users",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    Set<UserEntity> users = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "notifications_topics",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    Set<TopicEntity> topics = new HashSet<>();
}
