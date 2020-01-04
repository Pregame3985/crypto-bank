package com.mcb.address.manager.core;

import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author william
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultSeedRule extends SeedRule {

    @NonNull
    @Getter
    private Integer userId;

    @NonNull
    @Getter
    private String salt;

    @Override
    public String performRule() {

        String sha1st = Hashing.sha256().hashString(String.valueOf(this.userId) + salt, StandardCharsets.UTF_8).toString();
        String sha2nd = Hashing.sha256().hashString(sha1st + salt, StandardCharsets.UTF_8).toString();
        String sha3rd = Hashing.sha256().hashString(sha2nd + salt, StandardCharsets.UTF_8).toString();

        if (log.isDebugEnabled()) {
            log.debug("sha1st {}, sha2nd {}, sha3rd {}", sha1st, sha2nd, sha3rd);
        }

        return sha3rd;
    }
}
