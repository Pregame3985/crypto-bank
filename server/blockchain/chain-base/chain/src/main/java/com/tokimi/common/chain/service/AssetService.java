package com.tokimi.common.chain.service;

import com.tokimi.common.chain.model.AssetDTO;

import java.util.List;

/**
 * @author william
 */
public interface AssetService {

    AssetDTO getManagerAsset();

    AssetDTO getAsset(Long assetId);

    List<AssetDTO> getMyAssets();

    List<Long> getMyAssetIds();
}
