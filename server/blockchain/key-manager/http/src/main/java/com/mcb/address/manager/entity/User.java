package com.mcb.address.manager.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author william
 */
@Getter
@Setter
@Entity
@Table(name = "fuser")
public class User extends AbstractEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fId", unique = true, nullable = false)
    private Integer id;

    @Column(name = "floginName")
    private String loginName;

    @Column(name = "salt")
    private String salt;
}