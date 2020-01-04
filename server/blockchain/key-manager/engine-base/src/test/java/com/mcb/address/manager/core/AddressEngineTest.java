package com.mcb.address.manager.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author william
 */
@Slf4j
public abstract class AddressEngineTest {

    protected WalletEngine testWalletEngine;
    protected WalletEngine walletEngine;

    @Getter
    private SeedRule userSeedRule = new DefaultSeedRule(153063, "msrS9yQyF7H59WNEU1X86ZiHRxGFF0odney");

    @Getter
    private SeedRule user2SeedRule = new DefaultSeedRule(152783, "2ba4c2fc17164133b2e72af302392c1a");

    @Getter
    private SeedRule hotWalletSeedRule = new DefaultSeedRule(-12306, "v@9FX9#XNG73Eu#%gKVe2asfy6rU!WWA%JPvJ#Z^&BX@r3#FhFXXq4jb^8jaZkTwyGQF2e&ahqtHUpeHkYcWuCgHbKbPfZcZa5Xn");

}
