package com.pushnotification.pushnotification.entity;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    LocalDateTime updatedDate;

    @CreatedBy
    @Column(updatable = false)
    String createdBy;


    @LastModifiedBy
    @Column(insertable = false)
    String updatedBy;

    @Column(name="is_active")
    Boolean isActive = true;

}