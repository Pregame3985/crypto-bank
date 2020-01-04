package com.mcb.address.manager.model;

import lombok.*;

/**
 * @author william
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class AddressGuardDTO {

    @NonNull
    private Integer tokenId;

    @NonNull
    private Long userId;

    @NonNull
    private String address;

    private String memo;
}
