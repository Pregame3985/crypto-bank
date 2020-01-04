package com.mcb.address.manager.service;

import java.util.List;

/**
 * @description:
 * @auther: conanliu
 * @date: 18-9-18 10:44
 */
public interface HotWalletService {

    /**
     * 获取热钱包地址列表
     * @param tokenId
     * @return
     */
    List<String> getHotWalletAddresses(Integer tokenId);

    /**
     * 获取默认salt
     * @return
     */
    String getDefaultSalt();

    /**
     * 根据tokenid获取salt
     * @param tokenId
     * @return
     */
    List<String> getSaltByTokenId(Integer tokenId);
}
