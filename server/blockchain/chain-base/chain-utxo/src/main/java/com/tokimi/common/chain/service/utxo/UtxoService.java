package com.tokimi.common.chain.service.utxo;

import com.tokimi.common.chain.model.UtxoDTO;

import java.util.List;

/**
 * @author william
 */
public interface UtxoService {

    boolean spent(Long tokenId, Long id, boolean value);

    boolean spent(Long tokenId, String txid, Integer index, boolean value);

    boolean lock(Long tokenId, Long id, boolean value);

    boolean lock(Long tokenId, String txid, Integer index, boolean value);

    List<UtxoDTO> listTop10Available(Long tokenId);

    List<UtxoDTO> listAvailableByAddress(Long tokenId, String address);

    void save(UtxoDTO utxoDTO);

    List<UtxoDTO> findExistUtxos(Long tokenId, String txid, Integer index);

    void sync(Long tokenId);
}
