package com.tokimi.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
@NoArgsConstructor
public final class ChainManagerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private ErrorConstants error;

    // root cause message
    private String reason;

    // store rpc error
    private String desc;

    private Object attached;

    public ChainManagerException(ErrorConstants error) {
        this.error = error;
    }

    public ChainManagerException(ErrorConstants error, String reason) {
        this(error);
        this.reason = reason;
    }

    public ChainManagerException(ErrorConstants error, String reason, String desc) {
        this(error, reason);
        this.desc = desc;
    }

    public ChainManagerException(ErrorConstants error, String reason, String desc, Object attached) {
        this(error, reason, desc);
        this.attached = attached;
    }
}