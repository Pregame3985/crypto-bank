package com.mcb.address.manager.service;

import com.mcb.address.manager.dao.PlatformWalletDAO;
import com.mcb.address.manager.dao.SaltConfigDAO;
import com.mcb.address.manager.entity.PlatformWallet;
import com.mcb.address.manager.entity.SaltConfig;
import com.mcb.address.manager.util.WalletTypeEnum;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @auther: conanliu
 * @date: 18-9-18 10:38
 */
@Slf4j
@Service
public class HotWalletServiceAdapter implements HotWalletService {

    @Setter
    @Resource
    private PlatformWalletDAO platformWalletDAO;
    @Setter
    @Resource
    private SaltConfigDAO saltConfigDAO;

    @Override
    public List<String> getHotWalletAddresses(Integer tokenId) {
        PlatformWallet.Id id = new PlatformWallet.Id();
        id.setTokenId(tokenId);
        id.setType(WalletTypeEnum.EGRESS.getValue());
        PlatformWallet probe = new PlatformWallet();
        probe.setId(id);
        List<PlatformWallet> platformWallets = platformWalletDAO.findAll(Example.of(probe));

        List<String> addresses = platformWallets.stream().map(PlatformWallet::getAddress).collect(Collectors.toList());
        return addresses;
    }

    @Override
    public String getDefaultSalt() {
        List<SaltConfig> list = saltConfigDAO.findAll();
        if (list != null && list.size() > 0) {
            return list.get(0).getSalt();
        }
        return null;
    }

    @Override
    public List<String> getSaltByTokenId(Integer tokenId) {
        SaltConfig probe = new SaltConfig();
        probe.setTokenId(tokenId);
        List<SaltConfig> list = saltConfigDAO.findAll(Example.of(probe));
        List<String> salts = list.stream().map(SaltConfig::getSalt).collect(Collectors.toList());
        return salts;
    }
}
