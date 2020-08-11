package com.tokimi.common.chain.service.sweep;

import java.util.List;

import com.tokimi.common.chain.service.tokenize.TokenizeService;
import com.tokimi.common.chain.model.AddressBalanceDTO;
import com.tokimi.common.chain.model.UnsweepAmoutDTO;

/**
 * @author william
 */
public interface SweepService extends TokenizeService {

    List<UnsweepAmoutDTO> unsweepAmount();

    List<AddressBalanceDTO> listAvailableByTokenId(Long tokenId);

    List<AddressBalanceDTO> listAvailableByUserId(Long userId);

    List<AddressBalanceDTO> listAvailableByAddress(String address);

    void sweepAll();

    void sweepByTokenId(Long tokenId);

    void sweepByUserId(Long userId);

    void sweepByAddress(String address);

    void fail(Long tokenId, Long sweepId);
}