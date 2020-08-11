package com.tokimi.chain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "chn_deposit_flows")
public class DepositFlow extends TokenFlow {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "height")
    private Long height;

    @Column(name = "`index`")
    private Integer index;

    @Column(name = "from_address")
    private String fromAddress;

    @Column(name = "to_address", nullable = false)
    private String toAddress;

    @Column(name = "swept")
    private Boolean swept;

    @Column(name = "swept_at")
    private LocalDateTime sweptAt;
}
