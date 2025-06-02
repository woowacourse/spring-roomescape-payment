package roomescape.common.exception;

import lombok.Getter;

@Getter
public abstract class CustomException extends RuntimeException {

    protected final ErrorCode errorCode;

    protected CustomException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getMessageForClient() {
        return errorCode.getMessage();
    }
}
