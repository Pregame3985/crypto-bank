package com.mcb.address.manager.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "fvirtualaddress_platform")
public class PlatformWallet extends AbstractEntity<PlatformWallet.Id> {

    @EmbeddedId
    private Id id;

    @Column(name = "wallet_addr")
    private String address;

    @Column(name = "wallet_salt")
    private String salt;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "mine_token_id")
    private Integer mineTokenId;

    @Column(name = "mine_fee_balance")
    private BigDecimal mineFeeBalance;

    @Column(name = "balance_warning")
    private BigDecimal insufficientLimit;

    @Column(name = "balance_transfer")
    private BigDecimal withdrawLimit;

    @Column(name = "balance_warninged")
    private Boolean allowingInsufficientLimit;

    @Column(name = "balance_transfered")
    private Boolean allowingWithdrawLimit;

    @Column(name = "create_time")
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @EqualsAndHashCode
    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "fId")
        private Integer userId;

        @Column(name = "vi_fId")
        private Integer tokenId;

        private Integer type;
    }
}
