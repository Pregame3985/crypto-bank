package com.mcb.address.manager.service;

import com.mcb.address.manager.model.AddressDTO;

/**
 * 地址service
 */
public interface AddressService {

    AddressDTO getAddress(AddressDTO addressDTO);

    AddressDTO generateAddress(AddressDTO addressDTO);

    Boolean isValid(Integer tokenId, String address);

    Boolean fix(Integer tokenId);

}
