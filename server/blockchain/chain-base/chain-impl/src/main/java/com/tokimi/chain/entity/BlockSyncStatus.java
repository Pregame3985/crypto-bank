package com.tokimi.chain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author william
 */
@Getter
@Setter
@Entity
@Table(name = "chn_block_sync_statuses")
public class BlockSyncStatus extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "token_id", nullable = false)
    private Long tokenId;

    @Column(name = "synced_height", nullable = false)
    private Long syncedHeight;

    @Column(name = "current_height")
    private Long currentHeight;
}