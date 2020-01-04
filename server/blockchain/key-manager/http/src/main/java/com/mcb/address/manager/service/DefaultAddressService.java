package com.mcb.address.manager.service;

import com.mcb.address.manager.core.DefaultSeedRule;
import com.mcb.address.manager.core.SeedRule;
import com.mcb.address.manager.core.WalletEngine;
import com.mcb.address.manager.dao.AddressGuardDAO;
import com.mcb.address.manager.dao.LegacyUserAddressDAO;
import com.mcb.address.manager.dao.PlatformWalletDAO;
import com.mcb.address.manager.dao.TokenDAO;
import com.mcb.address.manager.dao.UserAddressDAO;
import com.mcb.address.manager.dao.UserDAO;
import com.mcb.address.manager.entity.AddressGuard;
import com.mcb.address.manager.entity.PlatformWallet;
import com.mcb.address.manager.entity.Token;
import com.mcb.address.manager.entity.User;
import com.mcb.address.manager.entity.UserAddress;
import com.mcb.address.manager.model.AddressDTO;
import com.mcb.address.manager.model.AddressGuardDTO;
import com.mcb.address.manager.model.HdWalletDTO;
import com.mcb.address.manager.model.KeyImportDTO;
import com.google.cloud.pubsub.v1.Publisher;
import com.mcb.address.manager.util.WalletTypeEnum;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DefaultAddressService implements AddressService {

    @Setter
    @Resource
    private UserDAO userDAO;

    @Setter
    @Resource
    private TokenDAO tokenDAO;

    @Setter
    @Resource
    private LegacyUserAddressDAO legacyUserAddressDAO;

    @Setter
    @Resource
    private UserAddressDAO userAddressDAO;

    @Setter
    @Resource
    private AddressGuardDAO addressGuardDAO;

    @Setter
    @Resource
    private List<WalletEngine> walletEngines;

    @Setter
    @Resource
    private Publisher addressGuardPublisher;

    @Setter
    @Resource
    private Publisher keyImportPublisher;

    @Setter
    @Resource
    private MessageService messageService;

    @Setter
    @Resource
    private PlatformWalletDAO platformWalletDAO;

    @Setter
    @Resource
    private HotWalletService hotWalletService;

    private Integer getRealTokenId(Integer tokenId) {

        Optional<Token> optionalToken = tokenDAO.findById(tokenId);

        if (optionalToken.isPresent()
                && null != optionalToken.get().getParentTokenId()
                && !optionalToken.get().getParentTokenId().equals(0)) {
            return optionalToken.get().getParentTokenId();
        } else {
            return tokenId;
        }
    }

    @Override
    @Transactional
    public AddressDTO getAddress(AddressDTO addressDTO) {
        Integer userId = addressDTO.getUserId();
        Integer tokenId = addressDTO.getTokenId();

        addressDTO.setTokenId(getRealTokenId(tokenId));

        if (userId > 0) {
            //用户地址不再保存到fvirtualaddress表中，by conanliu
            findAddressByHdWallet(addressDTO);
        }

        if (StringUtils.isEmpty(addressDTO.getAddress())) {
            User user = userDAO.getOne(userId);

            if (!user.isNew()) {
                generateAddress(addressDTO);
            }
        }

        addressDTO.setTokenId(tokenId);
        return addressDTO;
    }

//    private void fetchCurrentAddress(AddressDTO addressDTO) {
//        findAddressByHdWallet(addressDTO);
//
//        if (StringUtils.isEmpty(addressDTO.getAddress())) {
//            fetchAddressByLegacy(addressDTO);
//        }
//    }
//
//    private void fetchAddressByLegacy(AddressDTO addressDTO) {
//
//        Integer userId = addressDTO.getUserId();
//        Integer tokenId = addressDTO.getTokenId();
//
//        Token token = tokenDAO.getOne(tokenId);
//
//        Integer realTokenId = (token.getParentTokenId() == 0 ? token.getId() : token.getParentTokenId());
//
//        LegacyUserAddress probe = new LegacyUserAddress();
//        probe.setUserId(userId);
//        probe.setTokenId(realTokenId);
//
//        Optional<LegacyUserAddress> optionalLegacyUserAddress = legacyUserAddressDAO.findOne(Example.of(probe));
//
//        if (optionalLegacyUserAddress.isPresent()) {
//            addressDTO.setAddress(optionalLegacyUserAddress.get().getAddress());
//            addressDTO.setMemo(optionalLegacyUserAddress.get().getMemo());
//        }
//    }

    private void findAddressByHdWallet(AddressDTO addressDTO) {

        Integer userId = addressDTO.getUserId();
        Integer tokenId = addressDTO.getTokenId();

        UserAddress probe = new UserAddress();
        probe.setUserId(userId);
        probe.setTokenId(tokenId);

        Page<UserAddress> data = userAddressDAO.findAll(Example.of(probe), PageRequest.of(0, 1, new Sort(Sort.Direction.DESC, "index")));

        if (data.hasContent()) {
            addressDTO.setAddress(data.getContent().get(0).getAddress());
            addressDTO.setMemo(data.getContent().get(0).getMemo());
        }
    }

    @Override
    @Transactional
    public AddressDTO generateAddress(AddressDTO addressDTO) {

        Integer userId = addressDTO.getUserId();
        Integer tokenId = addressDTO.getTokenId();

        // for legacy purpose
        if (null == tokenId) {
            tokenId = addressDTO.getCoinType();
        }

        Integer realTokenId = getRealTokenId(tokenId);

        addressDTO.setTokenId(realTokenId);

        Assert.notNull(realTokenId, "token id should not be null");

        UserAddress probe = new UserAddress();
        probe.setUserId(userId);
        probe.setTokenId(realTokenId);

        Page<UserAddress> data = userAddressDAO.findAll(Example.of(probe), PageRequest.of(0, 1, new Sort(Sort.Direction.DESC, "index")));

        Integer index = 0;
        if (data.hasContent()) {
            index = data.getContent().get(0).getIndex() + 1;
        }

        String salt;

        boolean isAdmin = addressDTO.isImadmin() && (addressDTO.getUserId() <= -12306 && addressDTO.getUserId() >= -12310);

        if (isAdmin) {
            salt = hotWalletService.getDefaultSalt();

            if (StringUtils.isEmpty(salt)) {
                throw new IllegalArgumentException("give the admin salt");
            }
        } else {
            User user = userDAO.getOne(userId);
            salt = user.getSalt();
        }

        log.info("user {} generate new address on index {} by token {} ", userId, index, realTokenId);

        HdWalletDTO hdWalletDTO = generateAndSaveAddress(userId, realTokenId, index, salt);

        addressDTO.setAddress(hdWalletDTO.getAddress());
        addressDTO.setAddresses(hdWalletDTO.getAddresses());
        addressDTO.setMemo(hdWalletDTO.getMemo());

        if (isAdmin) {

            PlatformWallet.Id id = new PlatformWallet.Id();
            id.setUserId(userId);
            id.setTokenId(realTokenId);
            id.setType(WalletTypeEnum.EGRESS.getValue());

            PlatformWallet walletProbe = new PlatformWallet();
            walletProbe.setId(id);

            Optional<PlatformWallet> hotWalletOptional = platformWalletDAO.findOne(Example.of(walletProbe));

            if (!hotWalletOptional.isPresent()) {
                PlatformWallet hotWallet = new PlatformWallet();
                hotWallet.setId(id);
                hotWallet.setAddress(addressDTO.getAddress());
                hotWallet.setSalt(salt);
                hotWallet.setBalance(BigDecimal.ZERO);
                hotWallet.setCreatedAt(LocalDateTime.now());
                hotWallet.setInsufficientLimit(BigDecimal.ONE.negate());
                hotWallet.setWithdrawLimit(BigDecimal.ONE.negate());
                hotWallet.setAllowingInsufficientLimit(Boolean.FALSE);
                hotWallet.setAllowingWithdrawLimit(Boolean.FALSE);
                platformWalletDAO.saveAndFlush(hotWallet);
            }
        }

        log.info("address {} generated for user {} on index {} by token {}", addressDTO.getAddress(), userId, index, realTokenId);

        addressDTO.setTokenId(tokenId);
        return addressDTO;
    }

    /**
     * 地址格式校验需要各自的engine来实现，不再依赖数据库正则，by conanliu 2019-09-12
     * @param tokenId
     * @param address
     * @return
     */
    @Override
    public Boolean isValid(Integer tokenId, String address) {

        if (null == tokenId || StringUtils.isEmpty(address)) {
            return false;
        }

        Integer realTokenId = getRealTokenId(tokenId);

        for (WalletEngine walletEngine : walletEngines) {

            if (!walletEngine.isSupportEngine(realTokenId)) {
                continue;
            }

            return walletEngine.isValid(address);
        }

        return false;
    }

//    private boolean matchRegex(Integer tokenId, String address) {
//        Optional<Token> optionalToken = tokenDAO.findById(tokenId);
//
//        if (optionalToken.isPresent()) {
//            String regex = optionalToken.get().getRegex();
//            if (!StringUtils.isEmpty(regex)) {
//                Pattern pattern = Pattern.compile(regex);
//                Matcher matcher = pattern.matcher(address);
//                return matcher.matches();
//            }
//        }
//
//        return false;
//    }

    @Override
    public Boolean fix(Integer tokenId) {
        UserAddress probe = new UserAddress();
        probe.setTokenId(tokenId);
        userAddressDAO.findAll(Example.of(probe)).forEach(userAddress -> {
            AddressGuard agProbe = new AddressGuard();
            agProbe.setTokenId(tokenId);
            agProbe.setUserId(userAddress.getUserId().longValue());
            agProbe.setAddress(userAddress.getAddress());

            if (!StringUtils.isEmpty(userAddress.getMemo())) {
                agProbe.setMemo(userAddress.getMemo());
            }

            Optional<AddressGuard> addressGuardOptional = addressGuardDAO.findOne(Example.of(agProbe));

            if (!addressGuardOptional.isPresent()) {

                AddressGuard addressGuard = new AddressGuard();

                addressGuard.setTokenId(tokenId);
                addressGuard.setUserId(userAddress.getUserId().longValue());
                addressGuard.setAddress(userAddress.getAddress());
                addressGuard.setMemo(userAddress.getMemo());

                log.info("save new address guard: token id {}, user id {}, address {}, memo {}", tokenId, userAddress.getUserId(), userAddress.getAddress(), userAddress.getMemo());

                addressGuardDAO.saveAndFlush(addressGuard);
            }
        });


        return true;
    }

    private HdWalletDTO generateAndSaveAddress(Integer userId, Integer realTokenId, Integer index, String salt) {

        HdWalletDTO walletDTO = null;

        for (WalletEngine walletEngine : walletEngines) {

            if (!walletEngine.isSupportEngine(realTokenId)) {
                continue;
            }

            SeedRule seedRule = new DefaultSeedRule(userId, salt);
            walletDTO = walletEngine.generateAddress(seedRule, index, hotWalletService.getHotWalletAddresses(realTokenId));
            importKey(userId, realTokenId, index, walletDTO);
            guardAddress(userId, realTokenId, walletDTO);
            if (CollectionUtils.isEmpty(walletDTO.getAddresses())) {

                UserAddress userAddress = new UserAddress();

                userAddress.setSlip44CoinType(walletDTO.getSlip44CoinType().getValue());
                userAddress.setUserId(userId);
                userAddress.setIndex(index);
                userAddress.setPublicKey(walletDTO.getPublicKey());
                userAddress.setAddress(walletDTO.getAddress());
                userAddress.setRuleName(seedRule.getRuleName());
                userAddress.setKeyImported(true);
                userAddress.setTokenId(realTokenId);
                userAddress.setMemo(walletDTO.getMemo());

                userAddressDAO.saveAndFlush(userAddress);
            } else {
                for (HdWalletDTO.Address address : walletDTO.getAddresses()) {
                    UserAddress userAddress = new UserAddress();

                    userAddress.setSlip44CoinType(walletDTO.getSlip44CoinType().getValue());
                    userAddress.setUserId(userId);
                    userAddress.setIndex(index);
                    userAddress.setPublicKey(walletDTO.getPublicKey());
                    userAddress.setAddress(address.getAddress());
                    userAddress.setRuleName(seedRule.getRuleName());
                    userAddress.setKeyImported(true);
                    userAddress.setTokenId(realTokenId);
                    userAddress.setMemo(walletDTO.getMemo());

                    userAddressDAO.saveAndFlush(userAddress);
                }
            }

            break;
        }
        return walletDTO;
    }

    private void importKey(Integer userId, Integer realTokenId, Integer index, HdWalletDTO walletDTO) {
        if (CollectionUtils.isEmpty(walletDTO.getAddresses())) {
            KeyImportDTO keyImportDTO = new KeyImportDTO();

            keyImportDTO.setTokenId(realTokenId);
            keyImportDTO.setUserId(userId.longValue());
            keyImportDTO.setAddress(walletDTO.getAddress());
            keyImportDTO.setIndex(index);

            log.info("send new address to key import {}", keyImportDTO);

            messageService.send(keyImportPublisher, keyImportDTO);
        } else {
            for (HdWalletDTO.Address address : walletDTO.getAddresses()) {
                KeyImportDTO keyImportDTO = new KeyImportDTO();

                keyImportDTO.setTokenId(realTokenId);
                keyImportDTO.setUserId(userId.longValue());
                keyImportDTO.setAddress(address.getAddress());
                keyImportDTO.setIndex(index);

                log.info("send new address to key import {}", keyImportDTO);

                messageService.send(keyImportPublisher, keyImportDTO);
            }
        }
    }

    private void guardAddress(Integer userId, Integer tokenId, HdWalletDTO walletDTO) {
        if (CollectionUtils.isEmpty(walletDTO.getAddresses())) {
            AddressGuard addressGuard = new AddressGuard();

            addressGuard.setTokenId(tokenId);
            addressGuard.setUserId(userId.longValue());
            addressGuard.setAddress(walletDTO.getAddress());
            addressGuard.setMemo(walletDTO.getMemo());

            addressGuardDAO.saveAndFlush(addressGuard);

            AddressGuardDTO message = new AddressGuardDTO(tokenId, userId.longValue(), walletDTO.getAddress());
            message.setMemo(walletDTO.getMemo());

            log.info("send new address to guard {}", message);

            messageService.send(addressGuardPublisher, message);
        } else {
            for (HdWalletDTO.Address address : walletDTO.getAddresses()) {
                AddressGuard addressGuard = new AddressGuard();

                addressGuard.setTokenId(tokenId);
                addressGuard.setUserId(userId.longValue());
                addressGuard.setAddress(address.getAddress());
                addressGuard.setMemo(walletDTO.getMemo());

                addressGuardDAO.saveAndFlush(addressGuard);

                AddressGuardDTO message = new AddressGuardDTO(tokenId, userId.longValue(), address.getAddress());
                message.setMemo(walletDTO.getMemo());

                log.info("send new address to guard {}", message);

                messageService.send(addressGuardPublisher, message);
            }
        }
    }
}
