package roomescape.common.exception.impl;

import org.springframework.core.NestedRuntimeException;

public class DeserializationException extends NestedRuntimeException {
    
    public DeserializationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
