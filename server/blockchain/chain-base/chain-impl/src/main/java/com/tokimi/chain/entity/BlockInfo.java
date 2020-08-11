package com.tokimi.chain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @author william
 */
@Getter
@Setter
@Entity(name = "chn_block_infos")
public class BlockInfo extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long height;

    @Column(name = "current_hash", nullable = false)
    private String currentHash;

    @Column(name = "prev_hash", nullable = false)
    private String prevHash;

    @Column(name = "confirmed_time")
    private LocalDateTime confirmedTime;

    @Column(name = "tx_count", nullable = false)
    private Integer txCount;

    @Column(name = "parse_seconds", nullable = false)
    private Integer parseSeconds;

    @Column(name = "reorg", nullable = false)
    private Boolean reorg;
}
