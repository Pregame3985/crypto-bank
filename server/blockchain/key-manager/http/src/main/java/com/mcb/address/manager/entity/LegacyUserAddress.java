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
@Table(name = "fvirtualaddress")
public class LegacyUserAddress extends AbstractEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fId", unique = true, nullable = false)
    private Integer id;

    @Column(name = "fVi_fId")
    private Integer tokenId;

    @Column(name = "fuid")
    private Integer userId;

    @Column(name = "fAdderess")
    private String address;

    @Column(name = "fmemo")
    private String memo;

    @Column(name = "fCreateTime", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}