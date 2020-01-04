package com.mcb.address.manager.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author william
 */
@Getter
@Setter
@Entity
@Table(name = "address_guards")
public class AddressGuard extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_id", nullable = false)
    private Integer tokenId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "memo")
    private String memo;

    @Column(name = "created_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "updated_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "active")
    private boolean active = true;

    @Column(name = "deleted")
    private boolean deleted = false;
}