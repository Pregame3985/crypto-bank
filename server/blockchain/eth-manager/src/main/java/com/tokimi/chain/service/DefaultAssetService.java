package com.tokimi.chain.service;

import com.tokimi.chain.dao.AssetDAO;
import com.tokimi.chain.dao.ExchangeWalletDAO;
import com.tokimi.chain.dao.PropertyDAO;
import com.tokimi.chain.entity.Asset;
import com.tokimi.chain.entity.ExchangeWallet;
import com.tokimi.chain.entity.Property;
import com.tokimi.chain.model.ChainDTO;
import com.tokimi.common.chain.model.AssetDTO;
import com.tokimi.common.chain.model.PropertyDTO;
import com.tokimi.common.chain.service.AssetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tokimi.common.chain.utils.WalletType.EGRESS;

/**
 * @author william
 */
@Slf4j
@Service
public class DefaultAssetService implements AssetService {

    @Value("${app.chain.includeChildren:true}")
    private boolean includeChildren;

    @Resource
    private ChainDTO chainDTO;

    @Resource
    private AssetDAO assetDAO;

    @Resource
    private PropertyDAO propertyDAO;

    @Resource
    private ExchangeWalletDAO exchangeWalletDAO;

    @Override
    @Transactional
    public AssetDTO getManagerAsset() {

        Long assetId = chainDTO.getId();
        return getAsset(assetId);
    }

    @Override
    @Transactional
    public AssetDTO getAsset(Long assetId) {

        Asset probe = new Asset();
        probe.setId(assetId);
        probe.setStatus(1);

        Optional<Asset> any = assetDAO.findOne(Example.of(probe));

        if (any.isPresent()) {
            Asset asset = any.get();
            AssetDTO assetDTO = new AssetDTO();

            assetDTO.setId(assetId);
            assetDTO.setStatus(asset.getStatus());
            assetDTO.setName(asset.getName());
            assetDTO.setShortName(asset.getShortName());
            assetDTO.setSymbol(asset.getSymbol());
            assetDTO.setParentId(asset.getParentId());
            assetDTO.setType(asset.getType());
            assetDTO.setDepositConfirmations(asset.getDepositConfirmations());
            assetDTO.setWithdrawConfirmations(asset.getWithdrawConfirmations());
            assetDTO.setOnNetwork(chainDTO.getNetwork());

            ExchangeWallet walletProbe = new ExchangeWallet();
            walletProbe.setTokenId(assetId);
            walletProbe.setType(EGRESS.getValue());

            List<ExchangeWallet> platformWallets = exchangeWalletDAO.findAll(Example.of(walletProbe));

            List<String> addresses = platformWallets.stream().map(ExchangeWallet::getAddress)
                    .collect(Collectors.toList());

            assetDTO.setAddresses(addresses);

            Property propertyProbe = new Property();
            propertyProbe.setTokenId(assetId);

            List<Property> propertyData = propertyDAO.findAll(Example.of(propertyProbe));

            List<PropertyDTO> properties = propertyData.stream().map(property -> {
                PropertyDTO propertyDTO = new PropertyDTO();

                propertyDTO.setTokenId(assetId);
                propertyDTO.setKey(property.getKey());
                propertyDTO.setDecimal(property.getDecimal());

                return propertyDTO;
            }).collect(Collectors.toList());

            assetDTO.setProperties(properties);

            return assetDTO;
        } else {
            String message = String.format("asset not found, asset id: %s", assetId);
            log.error(message);
            throw new IllegalStateException(message);
        }
    }

    @Override
    @Transactional
    public List<AssetDTO> getMyAssets() {
        return getAssets(chainDTO.getId()).stream().map(asset -> getAsset(asset.getId())).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Long> getMyAssetIds() {
        return getAssets(chainDTO.getId()).stream().map(Asset::getId).collect(Collectors.toList());
    }

    private List<Asset> getAssets(Long assetId) {

        List<Asset> assets = new ArrayList<>();

        Asset probe = new Asset();
        probe.setId(assetId);
        probe.setStatus(1);

        Optional<Asset> any = assetDAO.findOne(Example.of(probe));

        if (any.isPresent()) {

            Asset parentAsset = any.get();

            boolean isParent = (null == parentAsset.getParentId() || 0 == parentAsset.getParentId());

            if (!isParent) {
                probe = new Asset();
                probe.setId(parentAsset.getParentId());
                probe.setStatus(1);
                any = assetDAO.findOne(Example.of(probe));

                if (any.isPresent()) {
                    parentAsset = any.get();
                }
            }

            assets.add(parentAsset);

            if (includeChildren) {
                Asset childrenProbe = new Asset();
                childrenProbe.setStatus(1);
                childrenProbe.setParentId(parentAsset.getId());
                List<Asset> childrenAssets = assetDAO.findAll(Example.of(childrenProbe));
                if (!CollectionUtils.isEmpty(childrenAssets)) {
                    assets.addAll(childrenAssets);
                }
            }
        }

        return assets;
    }
}
