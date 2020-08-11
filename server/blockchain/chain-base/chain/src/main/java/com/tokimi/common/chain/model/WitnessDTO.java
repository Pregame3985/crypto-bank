package com.tokimi.common.chain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author william
 */
@Getter
@Setter
@ToString
public class WitnessDTO {

    private String invocationScript;

    private String verificationScript;
}