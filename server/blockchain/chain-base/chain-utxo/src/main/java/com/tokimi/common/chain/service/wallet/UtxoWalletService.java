package com.tokimi.common.chain.service.wallet;

import java.math.BigDecimal;
import java.util.List;

import com.tokimi.common.chain.model.SenderDTO;
import com.tokimi.common.chain.model.KeyImportDTO;

/**
 * @author william
 */
public interface UtxoWalletService extends WalletService {

    List<SenderDTO> fundWithAnyoneForMerge(Long tokenId, Integer limit);

    List<SenderDTO> fundWithAnyone(Long tokenId, BigDecimal amount);

    List<SenderDTO> fundWithAddress(Long tokenId, BigDecimal amount, String address);

    List<SenderDTO> fundWithAddress(Long tokenId, String address);

    void releaseFund(Long tokenId, List<Long> utxoIds);

    void importPublicKey(KeyImportDTO keyImportDTO);

    void importAllPublicKeys();

    BigDecimal estimateFee(int estimateTxSize);
}
