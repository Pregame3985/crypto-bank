package com.mcb.address.manager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@JsonIgnoreProperties(value = {"imadmin"}, allowSetters = true)
@ToString
public class AddressDTO {

    private Integer userId;

    private Integer tokenId;

    private Integer coinType;

    private String address;

    private String memo;

    private boolean imadmin;

    private List<HdWalletDTO.Address> addresses;
}
